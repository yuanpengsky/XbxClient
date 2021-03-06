package com.xbx.client.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xbx.client.linsener.ImageLoaderConfigFactory;

import java.util.List;

/**
 * Created by EricYuan on 2016/3/29.
 */
public class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initDatas();
        initViews();
    }

    protected void initViews(){

    }

    protected void initDatas(){

    }

    @Override
    public void onClick(View v) {

    }

    // 关闭键盘输入框
    protected void colseKey(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
