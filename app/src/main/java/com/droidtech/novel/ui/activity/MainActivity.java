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
package com.droidtech.novel.ui.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.menu.MenuBuilder;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.droidtech.novel.R;
import com.droidtech.novel.R2;
import com.droidtech.novel.base.BaseActivity;
import com.droidtech.novel.base.Constant;
import com.droidtech.novel.component.AppComponent;
import com.droidtech.novel.component.DaggerMainComponent;
import com.droidtech.novel.manager.EventManager;
import com.droidtech.novel.manager.SettingManager;
import com.droidtech.novel.service.DownloadBookService;
import com.droidtech.novel.ui.contract.MainContract;
import com.droidtech.novel.ui.fragment.RecommendFragment;
import com.droidtech.novel.ui.fragment.TopCategoryListFragment;
import com.droidtech.novel.ui.fragment.TopRankFragment;
import com.droidtech.novel.ui.presenter.MainActivityPresenter;
import com.droidtech.novel.utils.SharedPreferencesUtil;
import com.droidtech.novel.utils.ToastUtils;
import com.droidtech.novel.view.GenderPopupWindow;
import com.droidtech.novel.view.RVPIndicator;

import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * https://github.com/JustWayward/BookReader
 */
public class MainActivity extends BaseActivity implements MainContract.View {

    private static final String TAG = "fragment_mainActivity";
    private static final String FRAGMENT_CURRENT = "fragment_current";

    @BindView(R2.id.indicator)
    RVPIndicator mIndicator;

    @Inject
    MainActivityPresenter mPresenter;

    // 退出时间
    private long currentBackPressedTime = 0;
    // 退出间隔
    private static final int BACK_PRESSED_INTERVAL = 2000;

    private GenderPopupWindow genderPopupWindow;
    private ArrayList<Fragment> mFragmentList;

    @Override
    public int getLayoutId() {
        return R.layout.book_activity_main;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setTitle(R.string.app_name);
        setTitle("");
    }

    @Override
    public void initDatas(boolean savedInstanceState) {
        startService(new Intent(this, DownloadBookService.class));
        initView(savedInstanceState);
    }

    private void initView(boolean isSave) {
        try {
            if (mFragmentList == null) {
                mFragmentList = new ArrayList<>();
            }

            if (mFragmentList.size() > 0) {
                mFragmentList.clear();
            }

            mFragmentList.add(new RecommendFragment());
            mFragmentList.add(new TopRankFragment());
            mFragmentList.add(new TopCategoryListFragment());
            //mFragmentList.add(new FindFragment());

            int index = SharedPreferencesUtil.getInstance().getInt(FRAGMENT_CURRENT, 0);

            // 解决fragment重叠的问题
            if (isSave) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                for (int i = 0; i < mFragmentList.size(); i++) {
                    Fragment fragment = fm.findFragmentByTag(TAG + i);
                    if (fragment != null) {
                        mFragmentList.set(i, fragment);
                        if (i != index) {
                            ft.hide(fragment);
                        } else {
                            ft.show(fragment);
                        }
                    }
                }
                ft.commitAllowingStateLoss();
                fm.executePendingTransactions();
            } else {
                setTabSelection(0);
            }
        } catch (Exception e) {

        }
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index 每个tab页对应的下标。0表示消息，1表示联系人，2表示动态，3表示设置。
     */
    public void setTabSelection(int index) {
        SharedPreferencesUtil.getInstance().putInt(FRAGMENT_CURRENT, index);

        FragmentManager fragmentManager = getSupportFragmentManager();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        for (int i = 0; i < mFragmentList.size(); i++) {
            if (i != index) {
                transaction.hide(mFragmentList.get(i));
            }
        }

        String tag = TAG + index;
        Fragment mFragment = fragmentManager.findFragmentByTag(tag);
        if (mFragment == null) {
            mFragment = mFragmentList.get(index);
            transaction.add(R.id.tabcontent, mFragment, tag);
        }
        transaction.show(mFragment);
        transaction.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }

    @OnClick({R2.id.home_tab_main, R2.id.home_tab_rank, R2.id.home_tab_classfiy, R2.id.home_tab_setting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R2.id.home_tab_main:
                setTabSelection(0);
                break;

            case R2.id.home_tab_rank:
                setTabSelection(1);
                break;

            case R2.id.home_tab_classfiy:
                setTabSelection(2);
                break;

            case R2.id.home_tab_setting:
                setTabSelection(3);
                break;

            default:
                break;
        }
    }

    @Override
    public void configViews() {
        mPresenter.attachView(this);
        mIndicator.postDelayed(new Runnable() {
            @Override
            public void run() {
                showChooseSexPopupWindow();
            }
        }, 500);
    }

    public void showChooseSexPopupWindow() {
        if (genderPopupWindow == null) {
            genderPopupWindow = new GenderPopupWindow(MainActivity.this);
        }
        if (!SettingManager.getInstance().isUserChooseSex()
                && !genderPopupWindow.isShowing()) {
            genderPopupWindow.showAtLocation(mCommonToolbar, Gravity.CENTER, 0, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R2.id.action_search:
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                break;
            case R2.id.action_login:
                /*if (popupWindow == null) {
                    popupWindow = new LoginPopupWindow(this);
                }
                popupWindow.showAtLocation(mCommonToolbar, Gravity.CENTER, 0, 0);*/
                break;
            case R2.id.action_my_message:
                /*if (popupWindow == null) {
                    popupWindow = new LoginPopupWindow(this);
                }
                popupWindow.showAtLocation(mCommonToolbar, Gravity.CENTER, 0, 0);*/
                break;
            case R2.id.action_sync_bookshelf:
                showDialog();
                mPresenter.syncBookShelf();
               /* if (popupWindow == null) {
                    popupWindow = new LoginPopupWindow(this);
                    popupWindow.setLoginTypeListener(this);
                }
                popupWindow.showAtLocation(mCommonToolbar, Gravity.CENTER, 0, 0);*/
                break;
            case R2.id.action_scan_local_book:
                ScanLocalBookActivity.startActivity(this);
                break;
            case R2.id.action_wifi_book:
                WifiBookActivity.startActivity(this);
                break;
            case R2.id.action_feedback:
                FeedbackActivity.startActivity(this);
                break;
            case R2.id.action_night_mode:
                if (SharedPreferencesUtil.getInstance().getBoolean(Constant.ISNIGHT, false)) {
                    SharedPreferencesUtil.getInstance().putBoolean(Constant.ISNIGHT, false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    SharedPreferencesUtil.getInstance().putBoolean(Constant.ISNIGHT, true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                recreate();
                break;
            case R2.id.action_settings:
                SettingActivity.startActivity(this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
                currentBackPressedTime = System.currentTimeMillis();
                ToastUtils.showToast(getString(R.string.exit_tips));
                return true;
            } else {
                finish(); // 退出
            }
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 显示item中的图片；
     *
     * @param view
     * @param menu
     * @return
     */
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
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
    protected void onDestroy() {
        super.onDestroy();
        DownloadBookService.cancel();
        stopService(new Intent(this, DownloadBookService.class));
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }
}