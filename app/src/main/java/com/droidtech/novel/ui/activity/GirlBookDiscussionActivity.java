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

import com.droidtech.novel.R;
import com.droidtech.novel.R2;
import com.droidtech.novel.base.BaseCommuniteActivity;
import com.droidtech.novel.component.AppComponent;
import com.droidtech.novel.view.SelectionLayout;

import java.util.List;

import butterknife.BindView;

/**
 * 女生区
 */
public class GirlBookDiscussionActivity extends BaseCommuniteActivity {

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, GirlBookDiscussionActivity.class));
    }

    @BindView(R2.id.slOverall)
    SelectionLayout slOverall;

    @Override
    public int getLayoutId() {
        return R.layout.book_activity_community_girl_book_discussion;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setTitle("女生区");
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas(boolean savedInstanceState) {
        super.initDatas(savedInstanceState);
    }

    @Override
    protected List<List<String>> getTabList() {
        return list1;
    }

    @Override
    public void configViews() {
    }
}
