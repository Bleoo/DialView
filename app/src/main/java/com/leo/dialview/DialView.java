package com.leo.dialview;

/**
 * Created by Administrator on 2016/11/12.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DialView extends View {

    /**
     * 表盘图
     */
    private Bitmap mBottomBitmap;

    /**
     * 指针图
     */
    private Bitmap mTopBitmap;

    /**
     * 最小数值
     */
    private int mMinValue;

    /**
     * 最大数值
     */
    private int mMaxValue;

    /**
     * 最小角度
     */
    private int mMinAngle;

    /**
     * 最大角度
     */
    private int mMaxAngle;

    /**
     * 当前角度
     */
    private int mCurrentAngle;

    /**
     * 每角度多少数值（数值/角度）
     */
    private float mV_A;

    /**
     * 角度初始偏移量
     */
    private int mOffsetAngle;

    /**
     * 表盘中心点X
     */
    private int mCenterX;

    /**
     * 表盘中心点Y
     */
    private int mCenterY;

    private OnDialChangeListener mOnDialChangeListener;

    public DialView(Context context) {
        this(context, null);
    }

    public DialView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getDataByXML(context, attrs, defStyle);
        initData();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = mesureWidth(widthMeasureSpec);
        int height = mesureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * 获取XML属性数据
     */
    private void getDataByXML(Context context, AttributeSet attrs, int defStyle) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DialView, defStyle, 0);
        int count = typedArray.length();
        for (int i = 0; i < count; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.DialView_bottom_picture:
                    mBottomBitmap = ((BitmapDrawable) typedArray.getDrawable(attr)).getBitmap();
                    break;
                case R.styleable.DialView_top_picture:
                    mTopBitmap = ((BitmapDrawable) typedArray.getDrawable(attr)).getBitmap();
                    break;
                case R.styleable.DialView_min_value:
                    mMinValue = typedArray.getInt(attr, 0);
                    break;
                case R.styleable.DialView_max_value:
                    mMaxValue = typedArray.getInt(attr, 100);
                    break;
                case R.styleable.DialView_min_angle:
                    mMinAngle = typedArray.getInt(attr, 30);
                    break;
                case R.styleable.DialView_max_angle:
                    mMaxAngle = typedArray.getInt(attr, 330);
                    break;
            }
        }
        mCurrentAngle = mMinAngle;
    }

    /**
     * 计算必要数值
     */
    private void initData() {
        mV_A = (mMaxValue - mMinValue) * 1.00f / (mMaxAngle - mMinAngle);
        mOffsetAngle = (360 - mMaxAngle + mMinAngle) / 2;
    }

    /**
     * 计算控件宽度
     */
    private int mesureWidth(int widthMeasureSpec) {
        int width = 0;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        } else {
            width = mBottomBitmap.getWidth() + getPaddingLeft() + getPaddingRight();
            if (mode == MeasureSpec.AT_MOST) {
                width = Math.min(width, size);
            }
        }
        return width;
    }

    /**
     * 计算控件高度
     */
    private int mesureHeight(int heightMeasureSpec) {
        int height = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else {
            height = mBottomBitmap.getHeight() + getPaddingTop() + getPaddingBottom();
            if (mode == MeasureSpec.AT_MOST) {
                height = Math.min(height, size);
            }
        }
        return height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPicture(canvas);
    }

    /**
     * 绘制表盘
     */
    private void drawPicture(Canvas canvas) {
        mCenterX = getMeasuredWidth() / 2;
        mCenterY = getMeasuredHeight() / 2;
        int drawLeft = mCenterX - (mBottomBitmap.getWidth() / 2);
        int drawTop = mCenterX - (mBottomBitmap.getHeight() / 2);
        canvas.drawBitmap(mBottomBitmap, drawLeft, drawTop, null);

        drawLeft = mCenterX - (mTopBitmap.getWidth() / 2);
        drawTop = mCenterY - (mTopBitmap.getHeight() / 2);
        Matrix matrix = new Matrix();
        matrix.postTranslate(drawLeft, drawTop);
        matrix.postRotate(mCurrentAngle, mCenterX, mCenterY);
        canvas.drawBitmap(mTopBitmap, matrix, null);

        if (mOnDialChangeListener != null) {
            mOnDialChangeListener.onValueChanged(this, getValue(), mCurrentAngle);
        }
    }

    /**
     * 设置指针角度
     */
    public void setAngle(int angle) {
        if (angle <= mMinAngle) {
            mCurrentAngle = mMinAngle;
        } else if (angle >= mMaxAngle) {
            mCurrentAngle = mMaxAngle;
        } else {
            mCurrentAngle = angle;
        }
        invalidate();
    }

    /**
     * 设置表盘数值
     */
    public void setValue(int value) {
        int angle = (int) (value / mV_A) + mOffsetAngle;
        setAngle(angle);
    }

    /**
     * 获取表盘数值
     */
    public int getValue() {
        return (int) ((mCurrentAngle - mOffsetAngle) * mV_A);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                updataAngle(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                updataAngle(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                updataAngle(event.getX(), event.getY());
                break;
        }
        return true;
    }

    /**
     * 更新指针角度
     */
    private void updataAngle(float x, float y) {
        float tanValue = Math.abs(mCenterX - x) / Math.abs(mCenterY - y);
        double tempAngle = Math.atan(tanValue) / Math.PI * 180;
        int angle = (int) Math.round(tempAngle);
        if (x <= mCenterX && y >= mCenterY) {
            //不需要处理
        } else if (x <= mCenterX && y < mCenterY) {
            angle = 180 - angle;
        } else if (x > mCenterX && y < mCenterY) {
            angle += 180;
        } else if (x > mCenterX && y > mCenterY) {
            angle = 360 - angle;
        }
        if (angle >= mMinAngle && angle <= mMaxAngle) {
            mCurrentAngle = angle;
            invalidate();
        }
    }

    /**
     * 设置表盘变化的监听
     */
    public void setOnDialChangeListener(OnDialChangeListener onDialChangeListener) {
        mOnDialChangeListener = onDialChangeListener;
    }

    /**
     * 监听表盘变化
     */
    public interface OnDialChangeListener {
        void onValueChanged(DialView dialView, int value, int angle);
    }

}

