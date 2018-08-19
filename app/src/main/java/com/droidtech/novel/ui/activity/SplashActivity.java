package com.droidtech.novel.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.droidtech.novel.BaseApplication;
import com.droidtech.novel.R;
import com.droidtech.novel.utils.SharedPreferencesUtil;
import com.droidtech.novel.utils.VarUtils;

/**
 * 登陆界面
 *
 * @author Administrator
 */
public class SplashActivity extends AppCompatActivity {

    private RelativeLayout splashLayout;
    TextView mJumpBtn;
    public boolean canJump = false;
    private boolean isNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kaiping);
        mJumpBtn = (TextView) findViewById(R.id.jump_btn);
        splashLayout = (RelativeLayout) findViewById(R.id.splashview);

        // 统计账户数量
        String user = SharedPreferencesUtil.getInstance().getString("user", null);
        if (user == null) {
            String time = System.currentTimeMillis() + "" + (int) (Math.random() * 1000);
            // 标识用户
            SharedPreferencesUtil.getInstance().putString("user", time);
        } else {
            VarUtils.isFirst = false;
        }

        if (SharedPreferencesUtil.getInstance().getInt("versionCode", 0) == 0) {
            SharedPreferencesUtil.getInstance().putInt("versionCode", BaseApplication.versioncode);
        }

        splash();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            next();
        }
        canJump = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (splashLayout != null) {
            splashLayout.removeAllViews();
            splashLayout = null;
        }
    }

    private void next() {
        if (canJump) {
            if (!isNext) {
                isNext = true;
                openActivity(MainActivity.class);
                this.finish();
            }
        } else {
            canJump = true;
        }
    }

    private void splash() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                openActivity(MainActivity.class);
                finish();
            }
        }).start();
    }

    // 防止用户返回键退出APP
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void openActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}
