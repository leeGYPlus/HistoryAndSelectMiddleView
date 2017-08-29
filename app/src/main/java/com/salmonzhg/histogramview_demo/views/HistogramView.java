package com.salmonzhg.histogramview_demo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.salmonzhg.histogramview_demo.R;
import com.salmonzhg.histogramview_demo.utils.DisplayUtil;

import java.util.Arrays;

/**
 * Created by Salmon on 2016/6/20 0020.
 */
public class HistogramView extends HorizontalScrollView {
    private static final int DEFAULT_COLUMN_PER_SCREEN = 7;
    private static final String[] DEFAULT_DATE_TEXT = new String[]{"一", "二", "三", "四", "五", "六", "日"};
    private static final int DEFAULT_COLOR = 0XFF3F51B5;
    private static final int DEFAULT_TEXT_SIZE = 14;
    private static final int PLAY = 0;
    private String[] mDefaultDateText = DEFAULT_DATE_TEXT;
    private int mColumnPerScreen = DEFAULT_COLUMN_PER_SCREEN;
    private int mColumnWid = 0;
    private int mDateTextColor = DEFAULT_COLOR;
    private int mHistogramColor = DEFAULT_COLOR;
    private int mDateTextSize = DEFAULT_TEXT_SIZE;
    private LinearLayout llHistogram;
    private LinearLayout llTime;
    private LinearLayout parent;
    private int mIndex = 0;
    private boolean isPlaying = false;
    private int mLastSelected = 0;
    private OnSelectListener mSelectListener;
    private OnClickListener mColumnListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setCheck(v.getId());
        }
    };
    private AnimationListener mAnimationListener;
    private Handler mPlayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PLAY:
                    if (mIndex >= llHistogram.getChildCount()) {
                        // 滑动到最右侧
                        fullScroll(FOCUS_FORWARD);
                        // 默认选择最右边的那个
                        ColumnView v = (ColumnView) llHistogram.getChildAt(llHistogram.getChildCount() - 1);
                        v.performClick();
                        isPlaying = false;
                        mIndex = 0;
                        if (mAnimationListener != null)
                            mAnimationListener.onAnimationDone();
                        break;
                    }
                    ColumnView v = (ColumnView) llHistogram.getChildAt(mIndex);
                    v.startAnim();
                    mIndex++;
                    sendEmptyMessageDelayed(PLAY, 50);
                    break;
            }
        }
    };
    private double mNum;

    public HistogramView(Context context) {
        super(context);

        init(context, null);
    }

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        Display display = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getWidth();
    }

    private void init(Context context, AttributeSet attrs) {
        halfScreenWidth = 1.0 * getScreenWidth(context) / 2;
        // 隐藏滑动条
        setHorizontalScrollBarEnabled(false);

        parent = new LinearLayout(context);
        parent.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        parent.setOrientation(LinearLayout.VERTICAL);
        addView(parent);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HistogramView);

            int columnsPer = a.getInteger(R.styleable.HistogramView_column_per_screen,
                    DEFAULT_COLUMN_PER_SCREEN);
            mDateTextColor = a.getColor(R.styleable.HistogramView_date_text_color, DEFAULT_COLOR);
            mHistogramColor = a.getColor(R.styleable.HistogramView_histogram_color, DEFAULT_COLOR);
            int textSizeSp = a.getDimensionPixelSize(R.styleable.HistogramView_date_text_size, -1);

            setColumnPerScreen(columnsPer);
            setDateTextColor(mDateTextColor);
            setDateTextSize(textSizeSp);

            a.recycle();
        }
    }

    /**
     * 滚动监听runnable
     */

    private int currentX = 0;//记录当前滚动的距离
    private Handler mHandler = new Handler();
    private int scrollDealy = 50;//滚动监听间隔
    int current = -1; //当前位于中间item的位置
    private onMiddleItemChangedListener middleItemChangedListener;
    double halfScreenWidth; //屏幕的一半宽度
    private Runnable scrollRunnable = new Runnable() {

        @Override
        public void run() {
            if (getScrollX() == currentX) {
                //滚动停止  取消监听线程
                mHandler.removeCallbacks(this);
                setMiddleItem();
                return;
            } else {
                //手指离开屏幕    view还在滚动的时候
                mHandler.postDelayed(this, scrollDealy);
            }
            currentX = getScrollX();
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //手指在上面移动的时候   取消滚动监听线程
                mHandler.removeCallbacks(scrollRunnable);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //手指移动的时候
                mHandler.post(scrollRunnable);
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void setMiddleItem() {
        if (parent != null) {
            int minWidth = -1;
            int lastCurrent = current;
            int minDivider = 0;

            //判断距离屏幕中间距离最短的item
            for (int i = 0; i < llHistogram.getChildCount(); i++) {
                Log.e("len", llHistogram.getChildCount() + "");
                int divider = (int) ((llHistogram.getChildAt(i).getX() + 1.0 * llHistogram.getChildAt(i).getWidth() / 2 - currentX) - halfScreenWidth);
                int absDivider = Math.abs(divider);
                if (minWidth < 0) {
                    minWidth = absDivider;
                } else if (minWidth > absDivider) {
                    minWidth = absDivider;
                    minDivider = divider;
                    current = i;
                }
            }
            //如果中间项变化，则恢复原状
            if (lastCurrent != current && lastCurrent >= 0) {
//                TextView lastMiddleView = (TextView) llHistogram.getChildAt(lastCurrent);
//                lastMiddleView.setTextSize(20);
//                lastMiddleView.setTextColor(Color.RED);
            }

            //滚动至中间位置
            scrollBy(minDivider + mColumnWid * 3 / 8, 0);
            Log.e("124", current + "--" + llHistogram.getChildCount());
            setCheck(llHistogram.getChildAt(current).getId());
            middleItemChangedListener.middleItemChanged(current);
        }
    }

    public void setData(HistogramEntity[] data) {
        if (isPlaying) {
            return;
        }
        if (data == null || data.length == 0) {
            return;
        }

        isPlaying = true;

        mColumnWid = getMeasuredWidth() / mColumnPerScreen;

        mLastSelected = 0;

        int max = maxInArray(data);

        llHistogram.removeAllViews();
        llTime.removeAllViews();

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(mColumnWid,
                ViewGroup.LayoutParams.WRAP_CONTENT);
//        param.leftMargin = mColumnWid;
        mNum = Math.ceil((halfScreenWidth - mColumnWid / 2) / mColumnWid) - 1;
        addDataHeaderOrFooter(llTime, param, mNum);
        for (int i = 0; i < data.length; i++) {
            TextView view = new TextView(getContext());
            view.setGravity(Gravity.CENTER);
            view.setTextSize(mDateTextSize);
            view.setTextColor(mDateTextColor);
            view.setLayoutParams(param);
            view.setText(data[i].time);
            view.setId(i);
            view.setOnClickListener(mColumnListener);
            llTime.addView(view);
        }
        addDataHeaderOrFooter(llTime, param, mNum);

        addHeaderOrFooter(llHistogram, param, mNum);
        Log.e("num", mNum + "");
        for (int i = 0; i < data.length; i++) {
            int d = 0;
            if (i < data.length) {
                d = data[i].count;
            }
            ColumnView view = new ColumnView(getContext());
            view.setLayoutParams(param);
            if (max != 0) {
                view.setRatio((float) d / (float) max);
//                if (d != 0)
                view.setShowText(String.valueOf(d));
            } else {
                view.setRatio(0);
                // 全部为0则不显示数字
                // view.setShowText(String.valueOf(0));
            }
            view.setId(i + (int) mNum);
            view.setColumnColor(mHistogramColor);
            view.setOnClickListener(mColumnListener);
            llHistogram.addView(view);
        }
        addHeaderOrFooter(llHistogram, param, mNum);
//        for (int i = 0; i < data.length; i++) {
//            TextView view = new TextView(getContext());
//            view.setGravity(Gravity.CENTER);
//            view.setTextSize(mDateTextSize);
//            view.setTextColor(mDateTextColor);
//            view.setLayoutParams(param);
//            view.setText(data[i].time);
//            view.setId(i);
//            view.setOnClickListener(mColumnListener);
//            llTime.addView(view);
//        }
        requestLayout();

        play();
    }

    private void addDataHeaderOrFooter(LinearLayout linearLayout, LinearLayout.LayoutParams param, double num) {
        for (int i = 0; i < num; i++) {
            TextView view = new TextView(getContext());
            view.setGravity(Gravity.CENTER);
            view.setTextSize(mDateTextSize);
            view.setTextColor(mDateTextColor);
            view.setLayoutParams(param);
            view.setId(i);
            view.setOnClickListener(mColumnListener);
            linearLayout.addView(view);
        }
    }


    private void addHeaderOrFooter(LinearLayout linearLayout, LinearLayout.LayoutParams param, double num) {
        for (int i = 0; i < num; i++) {
            ColumnView view = new ColumnView(getContext());
            view.setLayoutParams(param);
            view.setId(i);
            view.setColumnColor(mHistogramColor);
            view.setOnClickListener(mColumnListener);
            linearLayout.addView(view);
        }
    }

    private void play() {
        mPlayHandler.sendEmptyMessage(PLAY);
    }

    private void initHistogram() {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        param.weight = 1;
        llHistogram = new LinearLayout(getContext());
        llHistogram.setOrientation(LinearLayout.HORIZONTAL);
        llHistogram.setLayoutParams(param);
        parent.addView(llHistogram);
    }

    private void initTime() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp2px(5);
        llTime = new LinearLayout(getContext());
        llTime.setOrientation(LinearLayout.HORIZONTAL);
        llTime.setLayoutParams(params);
        parent.addView(llTime);
        LinearLayout.LayoutParams childParam = new LinearLayout.LayoutParams(mColumnWid,
                ViewGroup.LayoutParams.WRAP_CONTENT);
//        childParam.leftMargin = mColumnWid;
        for (int i = 0; i < mDefaultDateText.length; i++) {
            TextView view = new TextView(getContext());
            view.setGravity(Gravity.CENTER);
            view.setTextSize(mDateTextSize);
            view.setTextColor(mDateTextColor);
            view.setLayoutParams(childParam);
            view.setText(mDefaultDateText[i]);
            llTime.addView(view);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mColumnWid = w / mColumnPerScreen;
        initTime();
        initHistogram();

    }

    private int maxInArray(HistogramEntity[] array) {
        int[] temp = new int[array.length];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = array[i].count;
        }
        Arrays.sort(temp);
        return temp[temp.length - 1];
    }

    protected int dp2px(int dpValue) {
        if (!isInEditMode())
            return DisplayUtil.dip2px(getContext(), dpValue);
        else
            return 20;
    }

    protected int px2dp(int pxValue) {
        if (!isInEditMode())
            return DisplayUtil.px2dip(getContext(), pxValue);
        else
            return 20;
    }

    public void setDefaultDateTextArray(String[] defaultDateTextArray) {
        if (defaultDateTextArray == null || defaultDateTextArray.length == 0)
            return;
        mDefaultDateText = defaultDateTextArray;
    }

    public void setCheck(int position) {
        if (isPlaying || llHistogram == null)
            return;
        if (position < 0 || position > llHistogram.getChildCount())
            return;
        ColumnView columnOld = (ColumnView) llHistogram.getChildAt(mLastSelected);
        columnOld.setSelect(false);
        ColumnView columnNew = (ColumnView) llHistogram.getChildAt(position);
        columnNew.setSelect(true);
        mLastSelected = position;
        if (mSelectListener != null)
            mSelectListener.onSelected(position - (int) mNum);
    }

    public void setColumnPerScreen(int columnPerScreen) {
        if (columnPerScreen < 1 || columnPerScreen > 10) {
            return;
        }
        mColumnPerScreen = columnPerScreen;
    }

    public void setDateTextColor(int color) {
        mDateTextColor = color;
    }

    public void setDateTextSize(int size) {
        if (size < 0 || size > 20) {
            return;
        }
        mDateTextSize = size;
    }

    public void setSelectListener(OnSelectListener listener) {
        mSelectListener = listener;
    }

    public void setAnimationListener(AnimationListener listener) {
        mAnimationListener = listener;
    }

    public interface OnSelectListener {
        void onSelected(int index);
    }

    public interface AnimationListener {
        void onAnimationDone();
    }

    public static class HistogramEntity {
        public String time;
        public int count;

        public HistogramEntity() {
        }

        public HistogramEntity(String time, int count) {
            this.time = time;
            this.count = count;
        }
    }


    public void setMiddleItemChangedListener(onMiddleItemChangedListener middleItemChangedListener) {
        this.middleItemChangedListener = middleItemChangedListener;
    }

    /**
     * 回调，将中间项位置传递出去
     */
    public interface onMiddleItemChangedListener {
        public void middleItemChanged(int current);
    }
}
