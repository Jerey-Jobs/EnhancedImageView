package com.jerey.imageview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by Xiamin on 2017/3/31.
 */

public class EnhancedImageView extends ImageView
        implements ViewTreeObserver.OnGlobalLayoutListener
        , ScaleGestureDetector.OnScaleGestureListener
        , View.OnTouchListener {
    private static final String TAG = "EnhancedImageView";
    private static final boolean DEBUG = true;
    private boolean mInitOnce = false;
    //初始化缩放值
    private float mInitScale;
    //双击放大的值
    private float mMidScale;
    //放大最大值
    private float mMaxScale;
    //负责图片的平移缩放
    private Matrix mScaleMatrix;
    //为缩放而生的类，捕获缩放比例
    private ScaleGestureDetector mScaleGestureDetector;

    public EnhancedImageView(Context context) {
        this(context, null);
    }

    public EnhancedImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EnhancedImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleMatrix = new Matrix();
        //覆盖用户设置
        super.setScaleType(ScaleType.MATRIX);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    /**
     * 全局布局完成后会调用
     */
    @Override
    public void onGlobalLayout() {
        if (!mInitOnce) {
            //得到控件的宽和高
            int width = getWidth();
            int height = getHeight();

            //拿到图片的宽高
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();

            float scale = 1.0f;
            //若图片宽度大于控件宽度 高度小于空间高度
            if (drawableWidth > width && drawableHeight < height) {
                log("若图片宽度大于控件宽度 高度小于空间高度");
                scale = width * 1.0f / drawableWidth;
                //图片的高度大于控件高度 宽度小于控件宽度
            } else if (drawableHeight > height && drawableWidth < width) {
                log("图片的高度大于控件高度 宽度小于控件宽度");
                scale = height * 1.0f / drawableHeight;
            } else if (drawableWidth > width && drawableHeight > height) {
                log("都大于");
                scale = Math.min(width * 1.0f / drawableWidth, height * 1.0f / drawableHeight);
            } else if (drawableWidth < width && drawableHeight < height) {
                log("都小于");
                scale = Math.min(width * 1.0f / drawableWidth, height * 1.0f / drawableHeight);
            }
            mInitScale = scale;
            mMidScale = scale * 2;
            mMaxScale = scale * 4;

            //计算将图片移动至中间距离
            int dx = getWidth() / 2 - drawableWidth / 2;
            int dy = getHeight() / 2 - drawableHeight / 2;

            mScaleMatrix.postTranslate(dx, dy);
            //xy方向不变形，必须传一样的
            mScaleMatrix.postScale(mInitScale, mInitScale, width / 2, height / 2);
            setImageMatrix(mScaleMatrix);

            mInitOnce = true;
        }
    }

    /**
     * 获取当前图片的缩放值
     *
     * @return
     */
    public float getScale() {
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }


    /**
     * 为缩放而生的类：ScaleGestureDetector
     *
     * @param detector
     * @return
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();
        log("多点触控时候的缩放值: " + scaleFactor);
        if (getDrawable() == null) {
            return true;
        }

        //缩放范围的控制, 放大时需要小于最大，缩小时需要大于最小
        if ((scale < mMaxScale && scaleFactor > 1.0f) || (scale > mInitScale && scaleFactor < 1.0f)) {
            if (scale * scaleFactor < mInitScale) {
                scaleFactor = mInitScale / scale;
            }

            if (scale * scaleFactor > mMaxScale) {
                scaleFactor = mMaxScale / scale;
            }
            log("设置最终缩放值 " + scaleFactor);
            mScaleMatrix.postScale(scaleFactor, scaleFactor, getWidth() / 2, getHeight() / 2);
            setImageMatrix(mScaleMatrix);
        }

        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {


        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    private void log(String str) {
        if (DEBUG) {
            Log.i(TAG, str);
        }
    }

    /**
     * 为了让mScaleGestureDetector拿到手势
     *
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }
}
