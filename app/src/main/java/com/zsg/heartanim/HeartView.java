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
 * Created by zsg on 2016/12/20.
 */
public class HeartView extends View{
    Path mPath;
    Path mPointPath;
    Paint mHeartPaint;
    Paint mPointPaint;
    ValueAnimator mAnimator;        //动画

    PathMeasure mPathMeasure;



    int mWidth;
    int mHeight;

    //贝塞尔曲线坐标
    private static final int PATH_WIDTH = 4;
    // 起始点
    private static final int[] START_POINT = new int[] {
            300, 270
    };
    // 爱心下端点
    private static final int[] BOTTOM_POINT = new int[] {
            300, 400
    };
    // 左侧控制点
    private static final int[] LEFT_CONTROL_POINT = new int[] {
            450, 200
    };
    // 右侧控制点
    private static final int[] RIGHT_CONTROL_POINT = new int[] {
            150, 200
    };



    public HeartView(Context context) {
        this(context,null);
    }

    public HeartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initPath();
        initPaint();
        initAnim();
    }

    private void initAnim() {
        mAnimator=ValueAnimator.ofFloat(0,mPathMeasure.getLength());
        mAnimator.setDuration(5000);
        // 减速插值器
        mAnimator.setInterpolator(new DecelerateInterpolator());
      //  mAnimator.ofFloat(0,mPathMeasure.getLength());
       // Log.e("xxxxx",mPathMeasure.getLength()+"");

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float start= (float) mAnimator.getAnimatedValue();
                float end=start+(float)( mPathMeasure.getLength()/10);
                if(end>mPathMeasure.getLength())
                    end= mPathMeasure.getLength();

             //   Log.e("xxxxx"," "+start+" "+end);
                mPointPath.reset();
                //从 原始path中取出一段 放入目的path中（添加），并不会删除目的path中以前的数据
                mPathMeasure.getSegment(start,end,mPointPath,true);
                postInvalidate();

            }
        });
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.setRepeatCount(-1);
        mAnimator.start();
    }

    private void initPaint() {
        mHeartPaint= new Paint(Paint.ANTI_ALIAS_FLAG);
        mHeartPaint.setStyle(Paint.Style.STROKE);
        mHeartPaint.setStrokeWidth(PATH_WIDTH);
        mHeartPaint.setColor(Color.RED);

        mPointPaint= new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setStyle(Paint.Style.STROKE);
        mPointPaint.setStrokeWidth(PATH_WIDTH);
        mPointPaint.setColor(Color.YELLOW);

    }



    private void initPath() {
        mPath=new Path();
        mPointPath=new Path();

        mPath.moveTo(START_POINT[0], START_POINT[1]);
        mPath.quadTo(RIGHT_CONTROL_POINT[0], RIGHT_CONTROL_POINT[1], BOTTOM_POINT[0],
                BOTTOM_POINT[1]);
        mPath.quadTo(LEFT_CONTROL_POINT[0], LEFT_CONTROL_POINT[1], START_POINT[0], START_POINT[1]);

        mPathMeasure=new PathMeasure();
        //forceClosed 就是Path最终是否需要闭合，如果为True的话，则不管关联的Path是否是闭合的，都会被闭合 PathMeasure的计算就会包含最后一段闭合的路径
        mPathMeasure.setPath(mPath,true);

    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth=MeasureSpec.getSize(widthMeasureSpec);
        mHeight=MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(mPath,mHeartPaint);
        canvas.drawPath(mPointPath,mPointPaint);
    }


}
