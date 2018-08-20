package com.techdroid.calculator.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.droidtech.novel.BaseApplication;
import com.droidtech.novel.common.OnChapterClickListener;
import com.droidtech.novel.utils.ToastUtils;
import com.droidtech.novel.utils.VarUtils;


public class MainActivity extends AppCompatActivity implements OnChapterClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_lib_activity_main);

        new BaseApplication(getApplication());
        VarUtils.listener = this;


        startActivity(new Intent(this, com.droidtech.novel.ui.activity.MainActivity.class));
    }

    @Override
    public void click(Activity activity) {
        ToastUtils.showToast("test");
    }
}
