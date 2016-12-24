package com.zsg.heartanim;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by zsg on 2016/12/22.
 */
public class AnimView extends View {
    public AnimView(Context context) {
        this(context, null);
    }

    public AnimView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initPath();
        initPaint();
        initAnim();

    }

    Path mPath;
    Path mPointPath;
    Paint mHeartPaint;
    Paint mPointPaint;
    ValueAnimator mAnimator;        //动画

    float mAllLength;
    float mSingleLength = 300;

    PathMeasure mPathMeasure;


    int mWidth;
    int mHeight;


    float temp = 0;

    private void initAnim() {
        mAnimator = ValueAnimator.ofFloat(0, mAllLength);
        mAnimator.setDuration(1000);
        // 减速插值器
      //  mAnimator.setInterpolator(new DecelerateInterpolator());
        //  mAnimator.ofFloat(0,mPathMeasure.getLength());
        Log.e("ggg", "总长度：" + mAllLength);

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float start = (float) mAnimator.getAnimatedValue();
                float realStart = 0;
                float end = 0;
                mPathMeasure.setPath(mPath, false);
                float mTemp = 0;
                //得到起始点在当前mPathMeasure之间
                // Log.e("ggg","执行0");
                while (mPathMeasure.getLength() != 0) {
                    if (start >= mTemp && start <= mTemp + mPathMeasure.getLength()) {
                        //计算起始点在当前mPathMeasure之间
                        break;
                    } else {
                        mTemp += mPathMeasure.getLength();
                        mPathMeasure.nextContour();
                    }
                }
                //Log.e("ggg", "执行1");
                mPointPath.reset();
                realStart = start - mTemp;
                //Log.e("ggg", start + "  " + mTemp + "  " + realStart + " " + mAllLength);
                if (mSingleLength <= mPathMeasure.getLength() - realStart) {
                    //显示的线段在当前线段中
                    end = realStart + mSingleLength;
                    mPathMeasure.getSegment(realStart, end, mPointPath, true);
                    // Log.e("ggg","执行2");
                } else {
                    if (start + mSingleLength > mAllLength) {
                        //比总长度还长 就要把当前起始位置到总位置终点的线段都要显示出来
                        end = mPathMeasure.getLength();
                        mPathMeasure.getSegment(realStart, end, mPointPath, true);
                        mPathMeasure.nextContour();
                        while (mPathMeasure.getLength() != 0) {
                            mPathMeasure.getSegment(0, mPathMeasure.getLength(), mPointPath, true);

                            mPathMeasure.nextContour();
                        }
                        end = mPathMeasure.getLength();
                        mPathMeasure.getSegment(realStart, end, mPointPath, true);
                    } else {
                        end = mPathMeasure.getLength();
                        mPathMeasure.getSegment(realStart, end, mPointPath, true);
                        end = mSingleLength - (end - realStart);
                        //Log.e("ggg","执行4");
                        mPathMeasure.nextContour();
                        while (mPathMeasure.getLength() != 0) {

                            if (end < mPathMeasure.getLength()) {
                                mPathMeasure.getSegment(0, end, mPointPath, true);
                                break;
                            } else {
                                mPathMeasure.getSegment(0, mPathMeasure.getLength(), mPointPath, true);
                                end = end - mPathMeasure.getLength();
                            }
                            mPathMeasure.nextContour();
                        }
                        //Log.e("ggg","执行5");

                    }
                }


                //PathMeasure.nextContour()移动到下一个线段  开始时 若没有操作PathMeasure  则移动到第一条


                //true：再次截取，起始点为0时，还是原path的起始点。
                //false：再次截取，起始点为0时，为上次截取的终点。
                //从 原始path中取出一段 放入目的path中（添加），并不会删除目的path中以前的数据
                //start end  是当前PathMeasure指向线段的开始和起始位置

                postInvalidate();

            }
        });

        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setRepeatCount(-1);

        mAnimator.start();
    }

    private void initPaint() {
        mHeartPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHeartPaint.setStyle(Paint.Style.STROKE);
        mHeartPaint.setStrokeWidth(3);
        mHeartPaint.setTextSize(5);
        mHeartPaint.setColor(Color.GRAY);

        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setStyle(Paint.Style.STROKE);
        mPointPaint.setStrokeWidth(3);
        mPointPaint.setColor(Color.WHITE);

    }


    private void initPath() {
        mPath = PathParserUtils.getPathFromArrayFloatList(StoreHousePath.getPath("I LOVE YOU"));

        mPointPath = new Path();

        mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mPath, false);

        while (mPathMeasure.getLength() != 0) {
            //计算总长度
            mAllLength += mPathMeasure.getLength();
            mPathMeasure.nextContour();
        }


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(10,50);
        canvas.drawPath(mPath, mHeartPaint);
        canvas.drawPath(mPointPath, mPointPaint);
    }


}
