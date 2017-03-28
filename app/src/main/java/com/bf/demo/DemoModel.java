package com.bf.demo;

import android.content.Context;
import android.view.View;

import com.bf.demo.view.LeftSlideModel;

/**
 * Created by baofei on 2017/3/13.
 */

public class DemoModel extends LeftSlideModel {

    @Override
    public int getLayoutViewId() {
        return R.layout.demo;
    }

    @Override
    public boolean isLeftSlide(int x) {
        return true;
    }

    @Override
    public void onCreateView(Context context, View view) {

    }

}
