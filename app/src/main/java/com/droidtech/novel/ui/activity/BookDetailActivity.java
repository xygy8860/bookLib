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
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.droidtech.novel.R;
import com.droidtech.novel.R2;
import com.droidtech.novel.base.BaseActivity;
import com.droidtech.novel.base.Constant;
import com.droidtech.novel.bean.BookDetail;
import com.droidtech.novel.bean.BookLists;
import com.droidtech.novel.bean.HotReview;
import com.droidtech.novel.bean.Recommend;
import com.droidtech.novel.bean.RecommendBookList;
import com.droidtech.novel.bean.support.RefreshCollectionIconEvent;
import com.droidtech.novel.common.OnRvItemClickListener;
import com.droidtech.novel.component.AppComponent;
import com.droidtech.novel.component.DaggerBookComponent;
import com.droidtech.novel.manager.CollectionsManager;
import com.droidtech.novel.ui.adapter.HotReviewAdapter;
import com.droidtech.novel.ui.adapter.RecommendBookListAdapter;
import com.droidtech.novel.ui.contract.BookDetailContract;
import com.droidtech.novel.ui.presenter.BookDetailPresenter;
import com.droidtech.novel.utils.FormatUtils;
import com.droidtech.novel.utils.ToastUtils;
import com.droidtech.novel.view.DrawableCenterButton;
import com.droidtech.novel.view.TagColor;
import com.droidtech.novel.view.TagGroup;
import com.yuyh.easyadapter.glide.GlideRoundTransform;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by lfh on 2016/8/6.
 */
public class BookDetailActivity extends BaseActivity implements BookDetailContract.View, OnRvItemClickListener<Object> {

    public static String INTENT_BOOK_ID = "bookId";

    public static void startActivity(Context context, String bookId) {
        context.startActivity(new Intent(context, BookDetailActivity.class)
                .putExtra(INTENT_BOOK_ID, bookId));
    }

    @BindView(R2.id.ivBookCover)
    ImageView mIvBookCover;
    @BindView(R2.id.tvBookListTitle)
    TextView mTvBookTitle;
    @BindView(R2.id.tvBookListAuthor)
    TextView mTvAuthor;
    @BindView(R2.id.tvCatgory)
    TextView mTvCatgory;
    @BindView(R2.id.tvWordCount)
    TextView mTvWordCount;
    @BindView(R2.id.tvLatelyUpdate)
    TextView mTvLatelyUpdate;
    @BindView(R2.id.btnRead)
    DrawableCenterButton mBtnRead;
    @BindView(R2.id.btnJoinCollection)
    DrawableCenterButton mBtnJoinCollection;
    @BindView(R2.id.tvLatelyFollower)
    TextView mTvLatelyFollower;
    @BindView(R2.id.tvRetentionRatio)
    TextView mTvRetentionRatio;
    @BindView(R2.id.tvSerializeWordCount)
    TextView mTvSerializeWordCount;
    @BindView(R2.id.tag_group)
    TagGroup mTagGroup;
    @BindView(R2.id.tvlongIntro)
    TextView mTvlongIntro;
    @BindView(R2.id.tvMoreReview)
    TextView mTvMoreReview;
    @BindView(R2.id.rvHotReview)
    RecyclerView mRvHotReview;
    @BindView(R2.id.rlCommunity)
    RelativeLayout mRlCommunity;
    @BindView(R2.id.tvCommunity)
    TextView mTvCommunity;
    @BindView(R2.id.tvHelpfulYes)
    TextView mTvPostCount;
    @BindView(R2.id.tvRecommendBookList)
    TextView mTvRecommendBookList;

    @BindView(R2.id.rvRecommendBoookList)
    RecyclerView mRvRecommendBoookList;

    @Inject
    BookDetailPresenter mPresenter;

    private List<String> tagList = new ArrayList<>();
    private int times = 0;

    private HotReviewAdapter mHotReviewAdapter;
    private List<HotReview.Reviews> mHotReviewList = new ArrayList<>();
    private RecommendBookListAdapter mRecommendBookListAdapter;
    private List<RecommendBookList.RecommendBook> mRecommendBookList = new ArrayList<>();
    private String bookId;

    private boolean collapseLongIntro = true;
    private Recommend.RecommendBooks recommendBooks;
    private boolean isJoinedCollections = false;

    @Override
    public int getLayoutId() {
        return R.layout.book_activity_book_detail;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerBookComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
        mCommonToolbar.setTitle(R.string.book_detail);
    }

    @Override
    public void initDatas(boolean savedInstanceState) {
        bookId = getIntent().getStringExtra(INTENT_BOOK_ID);
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        mRvHotReview.setHasFixedSize(true);
        mRvHotReview.setLayoutManager(new LinearLayoutManager(this));
        mHotReviewAdapter = new HotReviewAdapter(mContext, mHotReviewList, this);
        mRvHotReview.setAdapter(mHotReviewAdapter);

        mRvRecommendBoookList.setHasFixedSize(true);
        mRvRecommendBoookList.setLayoutManager(new LinearLayoutManager(this));
        mRecommendBookListAdapter = new RecommendBookListAdapter(mContext, mRecommendBookList, this);
        mRvRecommendBoookList.setAdapter(mRecommendBookListAdapter);

        mTagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                startActivity(new Intent(BookDetailActivity.this, BooksByTagActivity.class)
                        .putExtra("tag", tag));
            }
        });

        mPresenter.attachView(this);
        mPresenter.getBookDetail(bookId);
        mPresenter.getHotReview(bookId);
        mPresenter.getRecommendBookList(bookId, "3");
    }

    @Override
    public void showBookDetail(BookDetail data) {
        Glide.with(mContext)
                .load(Constant.IMG_BASE_URL + data.cover)
                .placeholder(R.drawable.cover_default)
                .transform(new GlideRoundTransform(mContext))
                .into(mIvBookCover);

        mTvBookTitle.setText(data.title);
        mTvAuthor.setText(String.format(getString(R.string.book_detail_author), data.author));
        mTvCatgory.setText(String.format(getString(R.string.book_detail_category), data.cat));
        mTvWordCount.setText(FormatUtils.formatWordCount(data.wordCount));
        mTvLatelyUpdate.setText(FormatUtils.getDescriptionTimeFromDateString(data.updated));
        mTvLatelyFollower.setText(String.valueOf(data.latelyFollower));
        mTvRetentionRatio.setText(TextUtils.isEmpty(data.retentionRatio) ?
                "-" : String.format(getString(R.string.book_detail_retention_ratio),
                data.retentionRatio));
        mTvSerializeWordCount.setText(data.serializeWordCount < 0 ? "-" :
                String.valueOf(data.serializeWordCount));

        tagList.clear();
        tagList.addAll(data.tags);
        times = 0;
        showHotWord();

        mTvlongIntro.setText(data.longIntro);
        mTvCommunity.setText(String.format(getString(R.string.book_detail_community), data.title));
        mTvPostCount.setText(String.format(getString(R.string.book_detail_post_count), data.postCount));

        recommendBooks = new Recommend.RecommendBooks();
        recommendBooks.title = data.title;
        recommendBooks._id = data._id;
        recommendBooks.cover = data.cover;
        recommendBooks.lastChapter = data.lastChapter;
        recommendBooks.updated = data.updated;

        refreshCollectionIcon();
    }

    /**
     * 刷新收藏图标
     */
    private void refreshCollectionIcon() {
        if (CollectionsManager.getInstance().isCollected(recommendBooks._id)) {
            initCollection(false);
        } else {
            initCollection(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void RefreshCollectionIcon(RefreshCollectionIconEvent event) {
        refreshCollectionIcon();
    }

    /**
     * 每次显示8个
     */
    private void showHotWord() {
        int start, end;
        if (times < tagList.size() && times + 8 <= tagList.size()) {
            start = times;
            end = times + 8;
        } else if (times < tagList.size() - 1 && times + 8 > tagList.size()) {
            start = times;
            end = tagList.size() - 1;
        } else {
            start = 0;
            end = tagList.size() > 8 ? 8 : tagList.size();
        }
        times = end;
        if (end - start > 0) {
            List<String> batch = tagList.subList(start, end);
            List<TagColor> colors = TagColor.getRandomColors(batch.size());
            mTagGroup.setTags(colors, (String[]) batch.toArray(new String[batch.size()]));
        }
    }

    @Override
    public void showHotReview(List<HotReview.Reviews> list) {
        mHotReviewList.clear();
        mHotReviewList.addAll(list);
        mHotReviewAdapter.notifyDataSetChanged();
    }

    @Override
    public void showRecommendBookList(List<RecommendBookList.RecommendBook> list) {
        if (!list.isEmpty()) {
            mTvRecommendBookList.setVisibility(View.VISIBLE);
            mRecommendBookList.clear();
            mRecommendBookList.addAll(list);
            mRecommendBookListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(View view, int position, Object data) {
        if (data instanceof HotReview.Reviews) {
            BookDiscussionDetailActivity.startActivity(this, ((HotReview.Reviews) data)._id);
        } else if (data instanceof RecommendBookList.RecommendBook) {
            RecommendBookList.RecommendBook recommendBook = (RecommendBookList.RecommendBook) data;

            BookLists bookLists = new BookLists();
            BookLists.BookListsBean bookListsBean = bookLists.new BookListsBean();
            bookListsBean._id = recommendBook.id;
            bookListsBean.author = recommendBook.author;
            bookListsBean.bookCount = recommendBook.bookCount;
            bookListsBean.collectorCount = recommendBook.collectorCount;
            bookListsBean.cover = recommendBook.cover;
            bookListsBean.desc = recommendBook.desc;
            bookListsBean.title = recommendBook.title;

            SubjectBookListDetailActivity.startActivity(this, bookListsBean);
        }
    }

    @OnClick(R2.id.btnJoinCollection)
    public void onClickJoinCollection() {
        if (!isJoinedCollections) {
            if (recommendBooks != null) {
                CollectionsManager.getInstance().add(recommendBooks);
                ToastUtils.showToast(String.format(getString(
                        R.string.book_detail_has_joined_the_book_shelf), recommendBooks.title));
                initCollection(false);
            }
        } else {
            CollectionsManager.getInstance().remove(recommendBooks._id);
            ToastUtils.showToast(String.format(getString(
                    R.string.book_detail_has_remove_the_book_shelf), recommendBooks.title));
            initCollection(true);
        }
    }

    private void initCollection(boolean coll) {
        if (coll) {
            mBtnJoinCollection.setText(R.string.book_detail_join_collection);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.book_detail_info_add_img);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mBtnJoinCollection.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.shape_common_btn_solid_normal));
            mBtnJoinCollection.setCompoundDrawables(drawable, null, null, null);
            mBtnJoinCollection.postInvalidate();
            isJoinedCollections = false;
        } else {
            mBtnJoinCollection.setText(R.string.book_detail_remove_collection);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.book_detail_info_del_img);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mBtnJoinCollection.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.btn_join_collection_pressed));
            mBtnJoinCollection.setCompoundDrawables(drawable, null, null, null);
            mBtnJoinCollection.postInvalidate();
            isJoinedCollections = true;
        }
    }

    @OnClick(R2.id.btnRead)
    public void onClickRead() {
        if (recommendBooks == null) return;
        ReadActivity2.startActivity(this, recommendBooks);
    }

    @OnClick(R2.id.tvBookListAuthor)
    public void searchByAuthor() {
        String author = mTvAuthor.getText().toString().replaceAll(" ", "");
        SearchByAuthorActivity.startActivity(this, author);
    }

    @OnClick(R2.id.tvlongIntro)
    public void collapseLongIntro() {
        if (collapseLongIntro) {
            mTvlongIntro.setMaxLines(20);
            collapseLongIntro = false;
        } else {
            mTvlongIntro.setMaxLines(4);
            collapseLongIntro = true;
        }
    }

    @OnClick(R2.id.tvMoreReview)
    public void onClickMoreReview() {
        BookDetailCommunityActivity.startActivity(this, bookId, mTvBookTitle.getText().toString(), 1);
    }

    @OnClick(R2.id.rlCommunity)
    public void onClickCommunity() {
        BookDetailCommunityActivity.startActivity(this, bookId, mTvBookTitle.getText().toString(), 0);
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }
}
