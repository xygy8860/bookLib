package com.droidtech.novel.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.droidtech.novel.R;
import com.droidtech.novel.R2;
import com.droidtech.novel.base.BaseFragment;
import com.droidtech.novel.bean.RankingList;
import com.droidtech.novel.common.OnRvItemClickListener;
import com.droidtech.novel.component.AppComponent;
import com.droidtech.novel.component.DaggerMainComponent;
import com.droidtech.novel.ui.activity.FragmentActivity;
import com.droidtech.novel.ui.activity.SubOtherHomeRankActivity;
import com.droidtech.novel.ui.activity.SubRankActivity;
import com.droidtech.novel.ui.adapter.TopRankAdapter;
import com.droidtech.novel.ui.contract.TopRankContract;
import com.droidtech.novel.ui.presenter.TopRankPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by cdsunqinwei@jd.com on 2017/7/19.
 */

public class TopRankFragment extends BaseFragment implements TopRankContract.View {

    @BindView(R2.id.elvFeMale)
    ExpandableListView elvFeMale;
    @BindView(R2.id.elvMale)
    ExpandableListView elvMale;

    private List<RankingList.MaleBean> maleGroups = new ArrayList<>();
    private List<List<RankingList.MaleBean>> maleChilds = new ArrayList<>();
    private TopRankAdapter maleAdapter;

    private List<RankingList.MaleBean> femaleGroups = new ArrayList<>();
    private List<List<RankingList.MaleBean>> femaleChilds = new ArrayList<>();
    private TopRankAdapter femaleAdapter;

    public static void startFragmentActivity(Context context) {
        Intent intent = new Intent(context, FragmentActivity.class);
        context.startActivity(intent);
    }

    @Inject
    TopRankPresenter mPresenter;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_top_rank;
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {
        dismissDialog();
    }

    @Override
    public void showRankList(RankingList rankingList) {
        maleGroups.clear();
        femaleGroups.clear();
        updateMale(rankingList);
        updateFemale(rankingList);
    }

    private void updateMale(RankingList rankingList) {
        List<RankingList.MaleBean> list = rankingList.male;
        List<RankingList.MaleBean> collapse = new ArrayList<>();
        for (RankingList.MaleBean bean : list) {
            if (bean.collapse) { // 折叠
                collapse.add(bean);
            } else {
                maleGroups.add(bean);
                maleChilds.add(new ArrayList<RankingList.MaleBean>());
            }
        }
        if (collapse.size() > 0) {
            maleGroups.add(new RankingList.MaleBean("别人家的排行榜"));
            maleChilds.add(collapse);
        }
        maleAdapter.notifyDataSetChanged();
    }

    private void updateFemale(RankingList rankingList) {
        List<RankingList.MaleBean> list = rankingList.female;
        List<RankingList.MaleBean> collapse = new ArrayList<>();
        for (RankingList.MaleBean bean : list) {
            if (bean.collapse) { // 折叠
                collapse.add(bean);
            } else {
                femaleGroups.add(bean);
                femaleChilds.add(new ArrayList<RankingList.MaleBean>());
            }
        }
        if (collapse.size() > 0) {
            femaleGroups.add(new RankingList.MaleBean("别人家的排行榜"));
            femaleChilds.add(collapse);
        }
        femaleAdapter.notifyDataSetChanged();
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View layout = view.findViewById(R.id.toolbar_layout);
        layout.setVisibility(View.GONE);
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {
        maleAdapter = new TopRankAdapter(getActivity(), maleGroups, maleChilds);
        femaleAdapter = new TopRankAdapter(getActivity(), femaleGroups, femaleChilds);
        maleAdapter.setItemClickListener(new ClickListener());
        femaleAdapter.setItemClickListener(new ClickListener());
    }

    @Override
    public void configViews() {
        showDialog();
        elvMale.setAdapter(maleAdapter);
        elvFeMale.setAdapter(femaleAdapter);

        mPresenter.attachView(this);
        mPresenter.getRankList();
    }

    class ClickListener implements OnRvItemClickListener<RankingList.MaleBean> {

        @Override
        public void onItemClick(View view, int position, RankingList.MaleBean data) {
            if (data.monthRank == null) {
                SubOtherHomeRankActivity.startActivity(mContext, data._id, data.title);
            } else {
                SubRankActivity.startActivity(mContext, data._id, data.monthRank, data.totalRank, data.title);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        super.onDestroy();
    }
}
