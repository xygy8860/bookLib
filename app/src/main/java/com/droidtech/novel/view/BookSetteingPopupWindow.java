package com.droidtech.novel.view;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.droidtech.novel.R;
import com.droidtech.novel.ui.activity.ScanLocalBookActivity;
import com.droidtech.novel.ui.activity.SettingActivity;
import com.droidtech.novel.utils.VarUtils;

/**
 * Created by xxx on 2018/8/20.
 */

public class BookSetteingPopupWindow extends PopupWindow {

    private View mContentView;

    private TextView mScanTxt;
    private TextView mNightTxt;
    private TextView mSettingTxt;

    public BookSetteingPopupWindow(final Activity activity) {
        super(activity);

        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mContentView = LayoutInflater.from(activity).inflate(R.layout.book_popup_setting, null);
        setContentView(mContentView);
        setFocusable(true);
        setOutsideTouchable(true);
        setTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        mScanTxt = (TextView) mContentView.findViewById(R.id.book_action_scan_local_book);
        mNightTxt = (TextView) mContentView.findViewById(R.id.book_action_night_mode);
        mSettingTxt = (TextView) mContentView.findViewById(R.id.book_action_settings);

        mScanTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanLocalBookActivity.startActivity(activity);
            }
        });

        mNightTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (SharedPreferencesUtil.getInstance().getBoolean(Constant.ISNIGHT, false)) {
                    SharedPreferencesUtil.getInstance().putBoolean(Constant.ISNIGHT, false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    SharedPreferencesUtil.getInstance().putBoolean(Constant.ISNIGHT, true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                activity.recreate();*/

                if (VarUtils.listener != null) {
                    VarUtils.listener.downApk(activity);
                }

                dismiss();
            }
        });

        mSettingTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.startActivity(activity);

                dismiss();
            }
        });
    }
}
