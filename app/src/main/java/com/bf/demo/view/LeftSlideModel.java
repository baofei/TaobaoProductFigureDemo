package com.bf.demo.view;

import android.content.Context;
import android.view.View;

/**
 * Created by baofei on 2017/3/13.
 */

public abstract class LeftSlideModel {
    /**
     * 返回布局的 ID
     *
     * @return
     */
    public abstract int getLayoutViewId();

    /**
     * 释放执行左滑
     * @param x
     * @return
     */
    public abstract boolean isLeftSlide(int x);

    public abstract void onCreateView(Context context, View view);
}
