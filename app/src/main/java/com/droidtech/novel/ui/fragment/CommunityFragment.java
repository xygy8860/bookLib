/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.droidtech.novel.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.droidtech.novel.R;
import com.droidtech.novel.R2;
import com.droidtech.novel.base.BaseFragment;
import com.droidtech.novel.bean.support.FindBean;
import com.droidtech.novel.common.OnRvItemClickListener;
import com.droidtech.novel.component.AppComponent;
import com.droidtech.novel.ui.activity.BookDiscussionActivity;
import com.droidtech.novel.ui.activity.BookHelpActivity;
import com.droidtech.novel.ui.activity.BookReviewActivity;
import com.droidtech.novel.ui.activity.GirlBookDiscussionActivity;
import com.droidtech.novel.ui.adapter.FindAdapter;
import com.droidtech.novel.view.SupportDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CommunityFragment extends BaseFragment implements OnRvItemClickListener<FindBean> {

    @BindView(R2.id.recyclerview)
    RecyclerView mRecyclerView;

    private FindAdapter mAdapter;
    private List<FindBean> mList = new ArrayList<>();

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_find;
    }

    @Override
    public void initDatas() {
        mList.clear();
        mList.add(new FindBean("综合讨论区", R.drawable.discuss_section));
        mList.add(new FindBean("书评区", R.drawable.comment_section));
        mList.add(new FindBean("书荒互助区", R.drawable.helper_section));
        mList.add(new FindBean("女生区", R.drawable.girl_section));
        mList.add(new FindBean("原创区", R.drawable.yuanchuang));
    }

    @Override
    public void configViews() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new SupportDividerItemDecoration(mContext, LinearLayoutManager.VERTICAL, true));

        mAdapter = new FindAdapter(mContext, mList, this);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void attachView() {

    }

    @Override
    public void onItemClick(View view, int position, FindBean data) {
        switch (position) {
            case 0:
                BookDiscussionActivity.startActivity(activity, true);
                break;
            case 1:
                BookReviewActivity.startActivity(activity);
                break;
            case 2:
                BookHelpActivity.startActivity(activity);
                break;
            case 3:
                GirlBookDiscussionActivity.startActivity(activity);
                break;
            case 4:
                BookDiscussionActivity.startActivity(activity, false);
                break;
            default:
                break;
        }
    }

}
