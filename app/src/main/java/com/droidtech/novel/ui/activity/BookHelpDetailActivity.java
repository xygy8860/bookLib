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
package com.droidtech.novel.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.droidtech.novel.R;
import com.droidtech.novel.R2;
import com.droidtech.novel.base.BaseRVActivity;
import com.droidtech.novel.base.Constant;
import com.droidtech.novel.bean.BookHelp;
import com.droidtech.novel.bean.CommentList;
import com.droidtech.novel.common.OnRvItemClickListener;
import com.droidtech.novel.component.AppComponent;
import com.droidtech.novel.component.DaggerCommunityComponent;
import com.droidtech.novel.ui.adapter.BestCommentListAdapter;
import com.droidtech.novel.ui.contract.BookHelpDetailContract;
import com.droidtech.novel.ui.easyadapter.CommentListAdapter;
import com.droidtech.novel.ui.presenter.BookHelpDetailPresenter;
import com.droidtech.novel.utils.FormatUtils;
import com.droidtech.novel.view.BookContentTextView;
import com.droidtech.novel.view.SupportDividerItemDecoration;
import com.droidtech.novel.view.recyclerview.adapter.RecyclerArrayAdapter;
import com.yuyh.easyadapter.glide.GlideCircleTransform;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 书荒互助区详情
 */
public class BookHelpDetailActivity extends BaseRVActivity<CommentList.CommentsBean> implements BookHelpDetailContract.View, OnRvItemClickListener<CommentList.CommentsBean> {

    private static final String INTENT_ID = "id";

    public static void startActivity(Context context, String id) {
        context.startActivity(new Intent(context, BookHelpDetailActivity.class)
                .putExtra(INTENT_ID, id));
    }

    private String id;
    private List<CommentList.CommentsBean> mBestCommentList = new ArrayList<>();
    private BestCommentListAdapter mBestCommentListAdapter;
    private HeaderViewHolder headerViewHolder;

    @Inject
    BookHelpDetailPresenter mPresenter;

    @Override
    public void showError() {

    }

    @Override
    public void complete() {

    }

    static class HeaderViewHolder {
        @BindView(R2.id.ivBookCover)
        ImageView ivAvatar;
        @BindView(R2.id.tvBookTitle)
        TextView tvNickName;
        @BindView(R2.id.tvTime)
        TextView tvTime;
        @BindView(R2.id.tvTitle)
        TextView tvTitle;
        @BindView(R2.id.tvContent)
        BookContentTextView tvContent;
        @BindView(R2.id.tvBestComments)
        TextView tvBestComments;
        @BindView(R2.id.rvBestComments)
        RecyclerView rvBestComments;
        @BindView(R2.id.tvCommentCount)
        TextView tvCommentCount;

        public HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);   //view绑定
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.book_activity_community_book_discussion_detail;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerCommunityComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setTitle("书荒互助区详情");
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas(boolean savedInstanceState) {
        id = getIntent().getStringExtra(INTENT_ID);

        mPresenter.attachView(this);
        mPresenter.getBookHelpDetail(id);
        mPresenter.getBestComments(id);
        mPresenter.getBookHelpComments(id, start, limit);
    }

    @Override
    public void configViews() {
        initAdapter(CommentListAdapter.class, false, true);

        mAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                View headerView = LayoutInflater.from(BookHelpDetailActivity.this).inflate(R.layout.book_header_view_book_discussion_detail, parent, false);
                return headerView;
            }

            @Override
            public void onBindView(View headerView) {
                headerViewHolder = new HeaderViewHolder(headerView);
            }
        });

    }

    @Override
    public void showBookHelpDetail(BookHelp data) {
        Glide.with(mContext).load(Constant.IMG_BASE_URL + data.help.author.avatar)
                .placeholder(R.drawable.avatar_default)
                .transform(new GlideCircleTransform(mContext))
                .into(headerViewHolder.ivAvatar);

        headerViewHolder.tvNickName.setText(data.help.author.nickname);
        headerViewHolder.tvTime.setText(FormatUtils.getDescriptionTimeFromDateString(data.help.created));
        headerViewHolder.tvTitle.setText(data.help.title);
        headerViewHolder.tvContent.setText(data.help.content);
        headerViewHolder.tvCommentCount.setText(String.format(mContext.getString(R.string.comment_comment_count), data.help.commentCount));
    }

    @Override
    public void showBestComments(CommentList list) {
        if (list.comments.isEmpty()) {
            gone(headerViewHolder.tvBestComments, headerViewHolder.rvBestComments);
        } else {
            mBestCommentList.addAll(list.comments);
            headerViewHolder.rvBestComments.setHasFixedSize(true);
            headerViewHolder.rvBestComments.setLayoutManager(new LinearLayoutManager(this));
            headerViewHolder.rvBestComments.addItemDecoration(new SupportDividerItemDecoration(mContext, LinearLayoutManager.VERTICAL, true));
            mBestCommentListAdapter = new BestCommentListAdapter(mContext, mBestCommentList);
            mBestCommentListAdapter.setOnItemClickListener(this);
            headerViewHolder.rvBestComments.setAdapter(mBestCommentListAdapter);
            visible(headerViewHolder.tvBestComments, headerViewHolder.rvBestComments);
        }
    }

    @Override
    public void showBookHelpComments(CommentList list) {
        mAdapter.addAll(list.comments);
        start = start + list.comments.size();
    }

    @Override
    public void onLoadMore() {
        super.onLoadMore();
        mPresenter.getBookHelpComments(id, start, limit);
    }

    @Override
    public void onItemClick(View view, int position, CommentList.CommentsBean data) {

    }

    @Override
    public void onItemClick(int position) {
        CommentList.CommentsBean data = mAdapter.getItem(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }
}
