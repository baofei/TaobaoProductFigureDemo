package com.bf.demo.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bf.demo.R;


/**
 * 商品详情中的轮播图，增加左滑功能
 * Created by baofei on 2016/8/9.
 */
public class LeftSlideLayout extends LinearLayout {

    private static final int ANIMATION_DURATION = 500;
    private static final float DAMPING = 1f;//初始滑动阻尼

    private int mLastMotionX;

    private View contentView;

    private View mFootView;
    private TextView footPromptTV;
    private ImageView footPromptIV;

    private int mHeadViewWidth;
    private int mOffset = 50;

    private LeftSlideModel mLeftSlideModel;

    private OnReleaseLintener mOnReleaseLintener;

    private PullStateE mPullStateE = PullStateE.PULL_STATE_NONE;

    private boolean isEndToDetail = true;

    enum PullStateE {
        PULL_STATE_NONE,
        PULL_STATE_REFRESH
    }

    public interface OnReleaseLintener {
        void onRelease();
    }

    public void setOnReleaseLintener(OnReleaseLintener lintener) {
        this.mOnReleaseLintener = lintener;
    }

    public LeftSlideLayout(Context context) {
        this(context, null);
    }

    public LeftSlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public LeftSlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LeftSlideLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * init
     */
    private void init() {
        setOrientation(LinearLayout.HORIZONTAL);
    }


    /**
     * 顶部布局添加
     */
    public void attachModelView(LeftSlideModel model) {
        //添加轮播view
        mLeftSlideModel = model;
        contentView = LayoutInflater.from(getContext()).inflate(model.getLayoutViewId(), null);
        model.onCreateView(getContext(), contentView);
        if (contentView == null) {
            return;
        }
        DisplayMetrics dm = getResources().getDisplayMetrics();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dm.widthPixels, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(contentView, params);

        //右侧隐藏栏
        mFootView = LayoutInflater.from(getContext()).inflate(R.layout.foot_view, null);
        measureView(mFootView);
        mHeadViewWidth = mFootView.getMeasuredWidth();
        ViewGroup.LayoutParams footParams = new ViewGroup.LayoutParams(mHeadViewWidth,
                LayoutParams.MATCH_PARENT
        );

        footPromptTV = (TextView) mFootView.findViewById(R.id.promptTV);
        footPromptIV = (ImageView) mFootView.findViewById(R.id.footPromptIV);

        //是否滑动到最后一张，显示提示
        if (isEndToDetail) {
            addView(mFootView, footParams);
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int x = (int) e.getRawX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 首先拦截down事件,记录y坐标
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                return isLeftSlide(x);
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    //判断是否进行控件左滑
    private boolean isLeftSlide(int x) {
        boolean isRefreshViewScroll = false;
        if (mLeftSlideModel != null) {
            isRefreshViewScroll = mLeftSlideModel.isLeftSlide(x) && mLastMotionX - x > 0;
        }
        if (isRefreshViewScroll) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return isRefreshViewScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastMotionX;
                changingHeaderViewLeftMargin(deltaX);//根据滑动的距离改变margin
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: //进行释放操作
                if (mPullStateE == PullStateE.PULL_STATE_REFRESH) {
                    if (mOnReleaseLintener != null) {
                        mOnReleaseLintener.onRelease();
                    }
                }
                moveTo(0, ANIMATION_DURATION);
                break;
        }
        return true;
    }

    /**
     * 修改Header view top margin的值
     *
     * @param deltaX
     */
    private int changingHeaderViewLeftMargin(int deltaX) {
        if (contentView == null) {
            return 0;
        }
        // LogUtil.e("------------>>>deltaX:" + deltaX);
        LayoutParams params = (LayoutParams) contentView.getLayoutParams();
        //阻尼根据拖动的距离计算出，值会越来越小，阻力越来越大
        float damping = DAMPING - Math.abs(((float) params.leftMargin / (float) mHeadViewWidth) * 0.25f);
        damping = Math.max(damping, 0.1f);
        //根据阻尼值计算 leftMargin
        float newLeftMargin = (float) params.leftMargin + (float) deltaX * damping;
        newLeftMargin = Math.min(newLeftMargin, 0);
        params.leftMargin = (int) newLeftMargin;
        contentView.setLayoutParams(params);

        //根据距离判断隐藏栏相应的显示
        if (newLeftMargin < -mHeadViewWidth - mOffset) {
            if (mPullStateE != PullStateE.PULL_STATE_REFRESH) {
                footPromptTV.setText(R.string.product_photo_view_foot_release);
                ObjectAnimator mAnimatorRotate = ObjectAnimator.ofFloat(footPromptIV, "rotation", 0.0f, 180.0f);
                mAnimatorRotate.setDuration((int) (ANIMATION_DURATION * 0.5f));
                mAnimatorRotate.start();
                mPullStateE = PullStateE.PULL_STATE_REFRESH;
            }
            // footPromptIV.seta
        } else if (newLeftMargin > -mHeadViewWidth - mOffset) {
            if (mPullStateE != PullStateE.PULL_STATE_NONE) {
                footPromptTV.setText(R.string.product_photo_view_foot_left);
                ObjectAnimator mAnimatorRotate = ObjectAnimator.ofFloat(footPromptIV, "rotation", 180.0f, 360.0f);
                mAnimatorRotate.setDuration((int) (ANIMATION_DURATION * 0.5f));
                mAnimatorRotate.start();
                mPullStateE = PullStateE.PULL_STATE_NONE;
            }
        }
        invalidate();
        return params.leftMargin;
    }

    /**
     * 释放还原操作
     *
     * @param i        float
     * @param duration int
     */
    public void moveTo(final float i, int duration) {
        int leftMargin = getHeaderLeftMargin();
        ValueAnimator animator = ValueAnimator.ofFloat(leftMargin, i);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {


            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float temp = (Float) valueAnimator.getAnimatedValue();
                setHeaderLeftMargin((int) temp);
            }
        });
        animator.start();
    }

    /**
     * 设置header view 的topMargin的值
     *
     * @param leftMargin ，为0时，说明header view 刚好完全显示出来； 为-mHeaderViewHeight时，说明完全隐藏了
     */
    private void setHeaderLeftMargin(int leftMargin) {
        if (contentView == null) {
            return;
        }
        LayoutParams params = (LayoutParams) contentView.getLayoutParams();
        params.leftMargin = leftMargin;
        contentView.setLayoutParams(params);
        invalidate();
    }

    /**
     * 获取当前header view 的topMargin
     */
    private int getHeaderLeftMargin() {
        if (contentView == null) {
            return 0;
        }
        LayoutParams params = (LayoutParams) contentView.getLayoutParams();
        return params.leftMargin;
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childHeightSpec = ViewGroup.getChildMeasureSpec(0, 0, p.height);
        int lpWidth = p.width;
        int childWidthSpec;
        if (lpWidth > 0) {
            // MeasureSpec 封装的是父布局对子布局的布局要求
            childWidthSpec = MeasureSpec.makeMeasureSpec(lpWidth,
                    MeasureSpec.EXACTLY); //根据提供的大小值和模式创建一个测量值
        } else {
            childWidthSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }


}
