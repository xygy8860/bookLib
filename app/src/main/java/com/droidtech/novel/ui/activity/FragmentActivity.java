package com.droidtech.novel.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.droidtech.novel.R;
import com.droidtech.novel.base.BaseActivity;
import com.droidtech.novel.component.AppComponent;
import com.droidtech.novel.ui.fragment.TopRankFragment;

/**
 * Created by  on 2017/7/19.
 */

public class FragmentActivity extends BaseActivity{


    private Fragment mFragment;

    @Override
    public int getLayoutId() {
        return R.layout.book_activity_fragment;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void initToolBar() {
        String title = getIntent().getStringExtra("title");
        if (TextUtils.isEmpty(title)) {
            mCommonToolbar.setTitle("排行榜");
        } else {
            mCommonToolbar.setTitle(title);
        }

        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas(boolean falg) {
        String tag = getIntent().getStringExtra("tag");
        if (!TextUtils.isEmpty(tag)) {
            mFragment = Fragment.instantiate(this, tag, getIntent().getExtras());
        } else {
            mFragment = new TopRankFragment();
        }

        if (mFragment != null) {
            FragmentManager ft = getSupportFragmentManager();
            FragmentTransaction tr = ft.beginTransaction();
            tr.replace(R.id.fragment, mFragment);
            tr.commitAllowingStateLoss();
            ft.executePendingTransactions();
        }
    }

    @Override
    public void configViews() {

    }
}
