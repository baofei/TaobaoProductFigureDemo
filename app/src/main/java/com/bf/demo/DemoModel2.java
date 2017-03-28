package com.bf.demo;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.bf.demo.view.LeftSlideModel;

import java.util.List;

/**
 * Created by baofei on 2017/3/13.
 */

public class DemoModel2 extends LeftSlideModel {
    //产品展示图
    private ViewPager viewPager;
    private ImageAdapter mImageAdapter;

    @Override
    public int getLayoutViewId() {
        return R.layout.demo2;
    }

    @Override
    public boolean isLeftSlide(int x) {
        return viewPager.getCurrentItem() == mImageAdapter.getCount() - 1;
    }

    @Override
    public void onCreateView(Context context, View view) {
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mImageAdapter = new ImageAdapter(context);
        viewPager.setAdapter(mImageAdapter);
        viewPager.addOnPageChangeListener(mImageAdapter);
        viewPager.setCurrentItem(0);
    }


    /**
     * 轮播图适配器
     */
    class ImageAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

        private Context mContext;

        public ImageAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = container.inflate(mContext, R.layout.demo, null);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
