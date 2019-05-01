package com.droidtech.novel.common;

import android.app.Activity;
import android.view.ViewGroup;

/**
 * 上一章和下一章的回调
 * Created by xxx on 2018/8/19.
 */

public interface OnChapterClickListener {

    void click(Activity activity);

    void start(Activity activity);

    void close(); // 关闭窗口

    void downApk(Activity activity);

    void bannerAd(ViewGroup adview, ViewGroup gdtLayou);

    void nativeAd1(ViewGroup layout);

    void nativeAd2(ViewGroup layout);
}
