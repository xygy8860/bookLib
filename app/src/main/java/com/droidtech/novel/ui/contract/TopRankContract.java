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
package com.droidtech.novel.ui.contract;

import com.droidtech.novel.base.BaseContract;
import com.droidtech.novel.bean.RankingList;

/**
 * @author yuyh.
 * @date 16/9/1.
 */
public interface TopRankContract {

    interface View extends BaseContract.BaseView {
        void showRankList(RankingList rankingList);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {

        void getRankList();
    }

}
