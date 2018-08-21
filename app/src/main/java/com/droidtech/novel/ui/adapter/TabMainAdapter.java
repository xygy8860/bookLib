package com.droidtech.novel.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by 孙勤伟 on 2016/10/22.
 */
public class TabMainAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> list = new ArrayList<>();
    ArrayList<String> title = new ArrayList<>();

    public TabMainAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * 添加fragment数据 和标题数据
     *
     * @param list
     * @param title
     */
    public void setData(ArrayList<Fragment> list, ArrayList<String> title) {
        this.list = list;
        this.title = title;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title.get(position);
    }
}
