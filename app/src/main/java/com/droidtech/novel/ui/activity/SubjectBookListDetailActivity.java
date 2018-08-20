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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.droidtech.novel.R;
import com.droidtech.novel.R2;
import com.droidtech.novel.base.BaseRVActivity;
import com.droidtech.novel.base.Constant;
import com.droidtech.novel.bean.BookListDetail;
import com.droidtech.novel.bean.BookLists;
import com.droidtech.novel.component.AppComponent;
import com.droidtech.novel.component.DaggerFindComponent;
import com.droidtech.novel.manager.CacheManager;
import com.droidtech.novel.ui.contract.SubjectBookListDetailContract;
import com.droidtech.novel.ui.easyadapter.SubjectBookListDetailBooksAdapter;
import com.droidtech.novel.ui.presenter.SubjectBookListDetailPresenter;
import com.droidtech.novel.view.recyclerview.adapter.RecyclerArrayAdapter;
import com.yuyh.easyadapter.glide.GlideCircleTransform;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 书单详情
 */
public class SubjectBookListDetailActivity extends BaseRVActivity<BookListDetail.BookListBean.BooksBean> implements SubjectBookListDetailContract.View {

    private HeaderViewHolder headerViewHolder;

    static class HeaderViewHolder {
        @BindView(R2.id.tvBookListTitle)
        TextView tvBookListTitle;
        @BindView(R2.id.tvBookListDesc)
        TextView tvBookListDesc;
        @BindView(R2.id.ivAuthorAvatar)
        ImageView ivAuthorAvatar;
        @BindView(R2.id.tvBookListAuthor)
        TextView tvBookListAuthor;
        @BindView(R2.id.btnShare)
        TextView btnShare;

        public HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private List<BookListDetail.BookListBean.BooksBean> mAllBooks = new ArrayList<>();

    private int start = 0;
    private int limit = 20;

    @Inject
    SubjectBookListDetailPresenter mPresenter;

    public static final String INTENT_BEAN = "bookListsBean";

    private BookLists.BookListsBean bookListsBean;

    public static void startActivity(Context context, BookLists.BookListsBean bookListsBean) {
        context.startActivity(new Intent(context, SubjectBookListDetailActivity.class)
                .putExtra(INTENT_BEAN, bookListsBean));
    }

    @Override
    public int getLayoutId() {
        return R.layout.book_activity_subject_book_list_detail;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerFindComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setTitle(R.string.subject_book_list_detail);
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas(boolean savedInstanceState) {
        bookListsBean = (BookLists.BookListsBean) getIntent().getSerializableExtra(INTENT_BEAN);
    }

    @Override
    public void configViews() {
        initAdapter(SubjectBookListDetailBooksAdapter.class, false, true);
        mRecyclerView.removeAllItemDecoration();
        mAdapter.addHeader(new RecyclerArrayAdapter.ItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                View headerView = LayoutInflater.from(mContext).inflate(R.layout.book_header_view_book_list_detail, parent, false);
                return headerView;
            }

            @Override
            public void onBindView(View headerView) {
                headerViewHolder = new HeaderViewHolder(headerView);
            }
        });

        mPresenter.attachView(this);
        mPresenter.getBookListDetail(bookListsBean._id);
    }

    @Override
    public void showBookListDetail(BookListDetail data) {
        headerViewHolder.tvBookListTitle.setText(data.getBookList().getTitle());
        headerViewHolder.tvBookListDesc.setText(data.getBookList().getDesc());
        headerViewHolder.tvBookListAuthor.setText(data.getBookList().getAuthor().getNickname());

        Glide.with(mContext)
                .load(Constant.IMG_BASE_URL + data.getBookList().getAuthor().getAvatar())
                .placeholder(R.drawable.avatar_default)
                .transform(new GlideCircleTransform(mContext))
                .into(headerViewHolder.ivAuthorAvatar);

        List<BookListDetail.BookListBean.BooksBean> list = data.getBookList().getBooks();
        mAllBooks.clear();
        mAllBooks.addAll(list);
        mAdapter.clear();
        loadNextPage();
    }

    private void loadNextPage() {
        if (start < mAllBooks.size()) {
            if (mAllBooks.size() - start > limit) {
                mAdapter.addAll(mAllBooks.subList(start, start + limit));
            } else {
                mAdapter.addAll(mAllBooks.subList(start, mAllBooks.size()));
            }
            start += limit;
        } else {
            mAdapter.addAll(new ArrayList<BookListDetail.BookListBean.BooksBean>());
        }
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void onItemClick(int position) {
        BookDetailActivity.startActivity(this, mAdapter.getItem(position).getBook().get_id());
    }

    @Override
    public void onRefresh() {
        mPresenter.getBookListDetail(bookListsBean._id);
    }

    @Override
    public void onLoadMore() {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadNextPage();
            }
        }, 500);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subject_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R2.id.menu_collect) {
            CacheManager.getInstance().addCollection(bookListsBean);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }
}
