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
package com.droidtech.novel;

import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;

import com.droidtech.novel.base.Constant;
import com.droidtech.novel.base.CrashHandler;
import com.droidtech.novel.bean.user.Login;
import com.droidtech.novel.component.AppComponent;
import com.droidtech.novel.component.DaggerAppComponent;
import com.droidtech.novel.module.AppModule;
import com.droidtech.novel.module.BookApiModule;
import com.droidtech.novel.utils.AppUtils;
import com.droidtech.novel.utils.DeviceUtils;
import com.droidtech.novel.utils.LogUtils;
import com.droidtech.novel.utils.SharedPreferencesUtil;
import com.droidtech.novel.utils.VarUtils;

/**
 * @author yuyh.
 * @date 2016/8/3.
 */
public class BaseApplication {

    private AppComponent appComponent;

    private static Context context;
    private static BaseApplication mInstance = null;
    public static int versioncode = 10000;

    public BaseApplication(Context ctx) {
        context = ctx;
        mInstance = this;

        initPrefs();
        init();
        initCompoent();
        AppUtils.init(ctx);
        CrashHandler.getInstance().init(ctx);
        initNightMode();
    }

    /**
     * 初始化配置
     */
    private void init() {

        // 统计账户数量
        String user = SharedPreferencesUtil.getInstance().getString("user");
        if (TextUtils.isEmpty(user)) {
            VarUtils.isFirst = true;
            user = DeviceUtils.getIMEI(context);
            if (TextUtils.isEmpty(user)) {
                user = System.currentTimeMillis() + "" + (int) (Math.random() * 1000);
            }
            // 标识用户
            SharedPreferencesUtil.getInstance().putString("user", user);

            Login.UserBean userBean = new Login.UserBean();
            userBean._id = user;
            userBean.versionCode = 10000;
            userBean.versionName = "1.0";
            SharedPreferencesUtil.getInstance().putObject("userBean", userBean);
        } else {
            VarUtils.isFirst = false;
        }
    }

    public static BaseApplication getInstance() {
        return mInstance;
    }

    private void initCompoent() {
        appComponent = DaggerAppComponent.builder()
                .bookApiModule(new BookApiModule())
                .appModule(new AppModule(context))
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    /**
     * 初始化SharedPreference
     */
    protected void initPrefs() {
        SharedPreferencesUtil.init(context, "book_preference", Context.MODE_MULTI_PROCESS);
    }

    protected void initNightMode() {
        boolean isNight = SharedPreferencesUtil.getInstance().getBoolean(Constant.ISNIGHT, false);
        LogUtils.d("isNight=" + isNight);
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static Context getApplication() {
        return context;
    }
}
