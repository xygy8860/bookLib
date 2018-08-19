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
package com.droidtech.novel.component;

import com.droidtech.novel.ui.activity.MainActivity;
import com.droidtech.novel.ui.activity.SettingActivity;
import com.droidtech.novel.ui.activity.WifiBookActivity;
import com.droidtech.novel.ui.fragment.LocalReaderFragment;
import com.droidtech.novel.ui.fragment.RecommendFragment;
import com.droidtech.novel.ui.fragment.TopRankFragment;

import dagger.Component;

@Component(dependencies = AppComponent.class)
public interface MainComponent {
    MainActivity inject(MainActivity activity);

    LocalReaderFragment inject(LocalReaderFragment fragment);

    RecommendFragment inject(RecommendFragment fragment);

    TopRankFragment inject(TopRankFragment fragment);

    SettingActivity inject(SettingActivity activity);
    WifiBookActivity inject(WifiBookActivity activity);
}