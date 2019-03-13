package com.wubeibei.rightdoor.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.wubeibei.rightdoor.R;

import java.util.ArrayList;

public class RouteProgress extends View {
    private static final String TAG = "RouteProgress";
    // 路线名称
    private ArrayList<String> RouteChnName;
    private Activity activity;

    // 进度条的高与长(不含箭头)
    private float ProgressHeight = 4.0f;
    private float ProgressWidth = 900.0f;
    // 箭头的高与长
    // 最高的那一边
    private float ArrowsHeight = 9.0f;
    private float ArrowsWidth = 100.0f;

    // 目前所在的站点(从0开始)
    private int NowStation = 2;

    // 每个站点在View里的起始横坐标
    private float[] EveryStationX;

    // 两个站点的长度；
    private float IntervalLength;

    // 两个站点之间已经走过进度(百分比)
    private float PassByProgressBetweenTwoSites = 0;

    // 圆片的半径
    private float radius = 20;

    // 两端富裕的长度
    private float RichLength = 100;

    // 进度条和文字的偏移高度
    private float offsetHeight = 5;

    // 字体大小
    private int TextSize = 34;

    // 是否粗体
    private boolean TextBold = true;

    // 走过的路径的颜色
    private int PassByColor =  Color.parseColor("#cc999999");

    // 未走过路径的颜色
    private int NonPassByColor = Color.parseColor("#ffffff");

    // 走过的文字的颜色
    private int PassByTextColor = Color.parseColor("#cc999999");

    // 未走过文字的颜色
    private int NonPassByTextColor = Color.parseColor("#ffffff");

    // 字体的高和宽
    private float TextHeight = 0;

    public RouteProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RouteProgress(Context context) {
        this(context, null);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public RouteProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //获得我们所定义的自定义样式属性
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RouteProgress, defStyle, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.RouteProgress_ProgressHeight:
                    ProgressHeight = a.getDimension(attr, 5.0f);
                    break;
                case R.styleable.RouteProgress_ProgressWidth:
                    ProgressWidth = a.getDimension(attr, 1820.0f);
                    break;
                case R.styleable.RouteProgress_ArrowsHeight:
                    ArrowsHeight = a.getDimension(attr, 7.0f);
                    break;
                case R.styleable.RouteProgress_ArrowsWidth:
                    ArrowsWidth = a.getDimension(attr, 67.0f);
                    break;
                case R.styleable.RouteProgress_PassByColor:
                    PassByColor = a.getColor(attr, Color.parseColor("#999999"));
                    break;
                case R.styleable.RouteProgress_NonPassByColor:
                    NonPassByColor = a.getColor(attr, Color.parseColor("#ffffff"));
                    break;
                case R.styleable.RouteProgress_PassByTextColor:
                    PassByTextColor = a.getColor(attr, Color.parseColor("#999999"));
                    break;
                case R.styleable.RouteProgress_NonPassByTextColor:
                    NonPassByTextColor = a.getColor(attr, Color.parseColor("#ffffff"));
                    break;
                case R.styleable.RouteProgress_TextSize:
                    // 默认设置为38sp，TypeValue也可以把sp转化为px
                    TextSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 38, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.RouteProgress_TextBold:
                    // 默认设置为38sp，TypeValue也可以把sp转化为px
                    TextBold = a.getBoolean(attr, true);
                    break;
                case R.styleable.RouteProgress_radius:
                    radius = a.getDimension(attr, 22.0f);
                    break;
                case R.styleable.RouteProgress_RichLength:
                    RichLength = a.getDimension(attr, 80.0f);
                    break;
                case R.styleable.RouteProgress_offsetHeight:
                    offsetHeight = a.getDimension(attr, 30.0f);
                    break;
            }

        }
        a.recycle();
    }

    private void init() {
        if (RouteChnName == null ||  RouteChnName.size() <= 0)
            return;
        IntervalLength = (ProgressWidth - RichLength * 2) / (RouteChnName.size() - 1);
        EveryStationX = new float[RouteChnName.size()];
        EveryStationX[0] = RichLength;
        for (int i = 1; i < RouteChnName.size(); i++)
            EveryStationX[i] = EveryStationX[i - 1] + IntervalLength;
    }

    public void setRouteChnName(ArrayList<String> routeChnName) {
        RouteChnName = routeChnName;
        PassByProgressBetweenTwoSites = 0.0f;
        NowStation = 0;
        postInvalidate();
    }

    public void setPassByProgressBetweenTwoSites(float passByProgressBetweenTwoSites) {
        PassByProgressBetweenTwoSites = passByProgressBetweenTwoSites;
        postInvalidate();
    }

    public void setNowStation(final int nowStation) {
        synchronized (this) {
            if(nowStation == NowStation)
                return;
            if(nowStation >= 0 && nowStation < RouteChnName.size()) {
//                if(nowStation > NowStation) {
//                    final ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
//                    animator.setDuration(1000);
//                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                        @Override
//                        public void onAnimationUpdate(ValueAnimator animation) {
//                            Float value = (Float) animation.getAnimatedValue();
//                            setPassByProgressBetweenTwoSites(value);
//                            if (value == 1.0f) {
//                                NowStation = nowStation;
//                                setPassByProgressBetweenTwoSites(0f);
//                            }
//                            postInvalidate();
//                        }
//                    });
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            animator.start();
//                        }
//                    });
//                }
//                else {
//                    final ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
//                    animator.setDuration(1000);
//                    NowStation = nowStation;
//                    setPassByProgressBetweenTwoSites(1.0f);
//                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                        @Override
//                        public void onAnimationUpdate(ValueAnimator animation) {
//                            Float value = (Float) animation.getAnimatedValue();
//                            setPassByProgressBetweenTwoSites(value);
////                            if (value == 0f) {
////                                NowStation = nowStation;
////                                setPassByProgressBetweenTwoSites(0f);
////                            }
//                            postInvalidate();
//                        }
//                    });
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            animator.start();
//                        }
//                    });
//                }
                NowStation = nowStation;
            }
        }
        postInvalidate();
    }

    public int getNowStation() {
        return NowStation;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        // width
        if (widthMode == MeasureSpec.EXACTLY)
            ProgressWidth = widthSize - ArrowsWidth;
//        } else {
//            mPaint.setTextSize(mTitleTextSize);
//            mPaint.getTextBounds(mTitle, 0, mTitle.length(), mBounds);
//            float textWidth = mBounds.width();
//            int desired = (int) (getPaddingLeft() + textWidth + getPaddingRight());
//            width = desired;
//        }
//
//        // height
//        if (heightMode == MeasureSpec.EXACTLY)
//        {
//            height = heightSize;
//        } else
//        {
//            mPaint.setTextSize(mTitleTextSize);
//            mPaint.getTextBounds(mTitle, 0, mTitle.length(), mBounds);
//            float textHeight = mBounds.height();
//            int desired = (int) (getPaddingTop() + textHeight + getPaddingBottom());
//            height = desired;
//        }
//        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(RouteChnName == null)
            return;
        init();
        @SuppressLint("DrawAllocation") Path path = new Path();
        @SuppressLint("DrawAllocation") Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.translate(0, radius - ProgressHeight / 2.0f);
        // 画未走过的部分包括箭头
        path.moveTo(EveryStationX[NowStation] + PassByProgressBetweenTwoSites * IntervalLength + radius, ArrowsHeight - ProgressHeight);
        path.lineTo(ProgressWidth, ArrowsHeight - ProgressHeight);
        path.lineTo(ProgressWidth, 0);
        path.lineTo(ProgressWidth + ArrowsWidth, ArrowsHeight);
        path.lineTo(EveryStationX[NowStation] + PassByProgressBetweenTwoSites * IntervalLength + radius, ArrowsHeight);
        path.close();
        paint.setColor(NonPassByColor);
        canvas.drawPath(path, paint);
        // 画已走过的部分
        path.reset();
        path.moveTo(0, ArrowsHeight - ProgressHeight);
        path.lineTo( EveryStationX[NowStation] + PassByProgressBetweenTwoSites * IntervalLength - radius, ArrowsHeight - ProgressHeight);
        path.lineTo(EveryStationX[NowStation] + PassByProgressBetweenTwoSites * IntervalLength - radius, ArrowsHeight);
        path.lineTo(0, ArrowsHeight);
        path.close();
        paint.setColor(PassByColor);
        canvas.drawPath(path, paint);
        // 画圆形标
        paint.setColor(PassByColor);
        canvas.drawCircle( EveryStationX[NowStation] + PassByProgressBetweenTwoSites * IntervalLength, ArrowsHeight - ProgressHeight / 2.0f, radius, paint);
        // 画文字
        paint.setTextSize(TextSize);
        paint.setFakeBoldText(TextBold);
        TextHeight = -paint.getFontMetrics().ascent + 3;
        for (int i = 0; i < RouteChnName.size(); i++) {
            char[] TextArray = RouteChnName.get(i).toCharArray();
            if (i <= NowStation)
                paint.setColor(PassByTextColor);
            else
                paint.setColor(NonPassByTextColor);
            float start_x = EveryStationX[i] - TextHeight / 2, start_y = radius + offsetHeight;
            float y = start_y;
            for(int j = 0; j < RouteChnName.get(i).length(); j++) {
                if(TextArray[j] == '(') {
                    start_y += ((j * TextHeight) - ((TextArray.length - j) * TextHeight)) / 2;
                    y = start_y;
                    start_x = EveryStationX[i] + TextHeight / 2;
                }
                y += TextHeight;
                if (TextArray[j] == '(')
                    canvas.drawText("︵", start_x, y, paint);
                else if (TextArray[j] == ')')
                    canvas.drawText("︶", start_x, y, paint);
                else
                    canvas.drawText(TextArray, j, 1, start_x, y, paint);
            }
        }
    }

    public float getFontHeight() {
        Paint pFont = new Paint();
        Rect rect = new Rect();
        pFont.getTextBounds(RouteChnName.get(0), 0, RouteChnName.get(0).length()-1, rect);
        return  rect.height() * 2.6f;
    }
}
