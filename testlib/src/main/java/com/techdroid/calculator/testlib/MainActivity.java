package com.techdroid.calculator.testlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R2.id.hello)
    TextView hello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_lib_activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R2.id.hello)
    public void onClick() {
        Toast.makeText(this, "hello", Toast.LENGTH_LONG).show();
    }
}
