/**
 * Copyright (c) 2016, smuyyh@gmail.com All Rights Reserved.
 * #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG             #
 * #                                                   #
 */
package com.droidtech.novel.ui.fragment;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;

import com.droidtech.novel.R;
import com.droidtech.novel.R2;
import com.droidtech.novel.base.BaseFragment;
import com.droidtech.novel.component.AppComponent;
import com.droidtech.novel.component.DaggerMainComponent;
import com.droidtech.novel.manager.EventManager;
import com.droidtech.novel.manager.SettingManager;
import com.droidtech.novel.service.DownloadBookService;
import com.droidtech.novel.ui.adapter.TabMainAdapter;
import com.droidtech.novel.ui.contract.MainContract;
import com.droidtech.novel.ui.presenter.MainActivityPresenter;
import com.droidtech.novel.utils.ToastUtils;
import com.droidtech.novel.view.GenderPopupWindow;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * https://github.com/JustWayward/BookReader
 */
public class MainFragment extends BaseFragment implements MainContract.View {

    @Inject
    MainActivityPresenter mPresenter;

    @BindView(R2.id.book_fragment_main_tablayout)
    TabLayout mTablayout;
    @BindView(R2.id.book_fragment_main_viewpager)
    ViewPager mViewpager;

    private GenderPopupWindow genderPopupWindow;

    private ArrayList<Fragment> list;
    private ArrayList<String> titles;

    @Override
    public int getLayoutResId() {
        return R.layout.book_fragment_tab_main;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {
        getActivity().startService(new Intent(getActivity(), DownloadBookService.class));
        setData();
        initAdapter();
    }

    private void initAdapter() {
        TabMainAdapter adapter = new TabMainAdapter(getChildFragmentManager());
        adapter.setData(list, titles);
        mViewpager.setAdapter(adapter);
        mViewpager.setOffscreenPageLimit(2);

        mTablayout.setTabMode(TabLayout.MODE_FIXED);
        mTablayout.setupWithViewPager(mViewpager);
    }

    private void setData() {
        list = new ArrayList<>();
        titles = new ArrayList<>();

        mViewpager.setOffscreenPageLimit(2);

        // 添加数据
        list.add(new RecommendFragment());
        list.add(new TopRankFragment());
        list.add(new TopCategoryListFragment());

        titles.add("书架");
        titles.add("排行");
        titles.add("分类");
    }

    public void configViews() {
        mPresenter.attachView(this);
        mViewpager.postDelayed(new Runnable() {
            @Override
            public void run() {
                showChooseSexPopupWindow();
            }
        }, 500);
    }

    public void showChooseSexPopupWindow() {
        try {
            if (genderPopupWindow == null) {
                genderPopupWindow = new GenderPopupWindow(getActivity());
            }
            if (!SettingManager.getInstance().isUserChooseSex()
                    && !genderPopupWindow.isShowing()) {
                genderPopupWindow.showAtLocation(mViewpager, Gravity.CENTER, 0, 0);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void loginSuccess() {
        ToastUtils.showSingleToast("登陆成功");
    }

    @Override
    public void syncBookShelfCompleted() {
        dismissDialog();
        EventManager.refreshCollectionList();
    }

    @Override
    public void showError() {
        ToastUtils.showSingleToast("同步异常");
        dismissDialog();
    }

    @Override
    public void complete() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DownloadBookService.cancel();
        getActivity().stopService(new Intent(getActivity(), DownloadBookService.class));
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }
}