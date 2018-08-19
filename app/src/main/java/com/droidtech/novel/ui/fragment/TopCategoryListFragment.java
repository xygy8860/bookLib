package com.droidtech.novel.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.droidtech.novel.R;
import com.droidtech.novel.R2;
import com.droidtech.novel.base.BaseFragment;
import com.droidtech.novel.base.Constant;
import com.droidtech.novel.bean.CategoryList;
import com.droidtech.novel.common.OnRvItemClickListener;
import com.droidtech.novel.component.AppComponent;
import com.droidtech.novel.component.DaggerFindComponent;
import com.droidtech.novel.ui.activity.FragmentActivity;
import com.droidtech.novel.ui.activity.SubCategoryListActivity;
import com.droidtech.novel.ui.adapter.TopCategoryListAdapter;
import com.droidtech.novel.ui.contract.TopCategoryListContract;
import com.droidtech.novel.ui.presenter.TopCategoryListPresenter;
import com.droidtech.novel.view.SupportGridItemDecoration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by cdsunqinwei@jd.com on 2017/7/19.
 */

public class TopCategoryListFragment extends BaseFragment implements TopCategoryListContract.View {

    @BindView(R2.id.rvMaleCategory)
    RecyclerView mRvMaleCategory;
    @BindView(R2.id.rvFemaleCategory)
    RecyclerView mRvFeMaleCategory;

    @Inject
    TopCategoryListPresenter mPresenter;

    private TopCategoryListAdapter mMaleCategoryListAdapter;
    private TopCategoryListAdapter mFemaleCategoryListAdapter;
    private List<CategoryList.MaleBean> mMaleCategoryList = new ArrayList<>();
    private List<CategoryList.MaleBean> mFemaleCategoryList = new ArrayList<>();


    public static void startFragmentActivity(Context context) {
        Intent intent = new Intent(context, FragmentActivity.class);
        intent.putExtra("title", "分类");
        intent.putExtra("tag", TopCategoryListFragment.class.getName());
        context.startActivity(intent);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_top_category_list;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerFindComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void showCategoryList(CategoryList data) {
        mMaleCategoryList.clear();
        mFemaleCategoryList.clear();
        mMaleCategoryList.addAll(data.male);
        mFemaleCategoryList.addAll(data.female);
        mMaleCategoryListAdapter.notifyDataSetChanged();
        mFemaleCategoryListAdapter.notifyDataSetChanged();
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {
        gone(getView().findViewById(R.id.toolbar_layout));
    }

    @Override
    public void configViews() {
        showDialog();
        mRvMaleCategory.setHasFixedSize(true);
        mRvMaleCategory.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRvMaleCategory.addItemDecoration(new SupportGridItemDecoration(getActivity()));
        mRvFeMaleCategory.setHasFixedSize(true);
        mRvFeMaleCategory.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRvFeMaleCategory.addItemDecoration(new SupportGridItemDecoration(getActivity()));
        mMaleCategoryListAdapter = new TopCategoryListAdapter(mContext, mMaleCategoryList, new ClickListener(Constant.Gender.MALE));
        mFemaleCategoryListAdapter = new TopCategoryListAdapter(mContext, mFemaleCategoryList, new ClickListener(Constant.Gender.FEMALE));
        mRvMaleCategory.setAdapter(mMaleCategoryListAdapter);
        mRvFeMaleCategory.setAdapter(mFemaleCategoryListAdapter);

        mPresenter.attachView(this);
        mPresenter.getCategoryList();
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {
        dismissDialog();
    }

    class ClickListener implements OnRvItemClickListener<CategoryList.MaleBean> {

        private String gender;

        public ClickListener(@Constant.Gender String gender) {
            this.gender = gender;
        }

        @Override
        public void onItemClick(View view, int position, CategoryList.MaleBean data) {
            SubCategoryListActivity.startActivity(mContext, data.name, gender);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }
}
