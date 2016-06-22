package com.salmonzhg.histogramview_demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.salmonzhg.histogramview_demo.utils.DateUtils;
import com.salmonzhg.histogramview_demo.utils.StepConvertUtil;
import com.salmonzhg.histogramview_demo.views.HistogramView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SalmonZhg";
    private RadioGroup mRadioGroup;
    private HistogramView mHistogram;
    private TextView mTextDate, mTextStep, mTextDistance, mTextCalories;
    private RadioButton mRadioButtonWeek, mRadioButtonMonth;
    private Toast mToast;
    private HistogramView.HistogramEntity[] mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHistogram = (HistogramView) findViewById(R.id.histogram);
        mRadioGroup = (RadioGroup) findViewById(R.id.time_radio_group);
        mTextDate = (TextView) findViewById(R.id.text_date);
        mTextStep = (TextView) findViewById(R.id.text_step);
        mTextDistance = (TextView) findViewById(R.id.text_distance);
        mTextCalories = (TextView) findViewById(R.id.text_calories);
        mRadioButtonWeek = ((RadioButton) findViewById(R.id.radio_week_button));
        mRadioButtonMonth = ((RadioButton) findViewById(R.id.radio_month_button));

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mRadioButtonMonth.setClickable(false);
                mRadioButtonWeek.setClickable(false);
                switch (checkedId) {
                    case R.id.radio_week_button:
                        mHistogram.setData(mData = genRandomWeekData());
                        break;
                    case R.id.radio_month_button:
                        mHistogram.setData(mData = genRandomMonthData());
                        break;
                }
            }
        });

        mHistogram.setSelectListener(new HistogramView.OnSelectListener() {
            @Override
            public void onSelected(int index) {
                showDetail(mData[index]);
            }
        });

        mHistogram.setAnimationListener(new HistogramView.AnimationListener() {
            @Override
            public void onAnimationDone() {
                mRadioButtonMonth.setClickable(true);
                mRadioButtonWeek.setClickable(true);
                mHistogram.setCheck(mData.length-1);
            }
        });

        mHistogram.post(new Runnable() {
            @Override
            public void run() {
                ((RadioButton) findViewById(R.id.radio_week_button)).setChecked(true);
            }
        });
    }

    private void showDetail(HistogramView.HistogramEntity data) {
        mTextDate.setText(data.time);
        mTextStep.setText(String.valueOf(data.count));
        mTextDistance.setText(StepConvertUtil.stepToDiatance(StepConvertUtil.MALE,
                StepConvertUtil.DEFAULT_TALL, data.count)+"");
        mTextCalories.setText(StepConvertUtil.stepToCalories(StepConvertUtil.DEFAULT_TALL,
                StepConvertUtil.DEFAULT_WEIGHT, data.count)+"");
    }

    private void showToast(String s) {
        if (mToast == null) {
            mToast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(s);
        mToast.show();
    }

    private HistogramView.HistogramEntity[] genRandomWeekData() {
        HistogramView.HistogramEntity[] result = new HistogramView.HistogramEntity[7];
        for (int i = 0; i < result.length; i++) {

            int num = (int) (3000 + 2000 * Math.random());
            HistogramView.HistogramEntity e = new HistogramView.HistogramEntity(
                    DateUtils.intToWeek(i), num);
            result[i] = e;
        }
        return result;
    }

    private HistogramView.HistogramEntity[] genRandomMonthData() {
        int[] days = daysArrayIn28();
        HistogramView.HistogramEntity[] result = new HistogramView.HistogramEntity[28];
        for (int i = 0; i < result.length; i++) {
            int num = (int) (2000 + 3000 * Math.random());
            HistogramView.HistogramEntity e = new HistogramView.HistogramEntity(
                    String.valueOf(days[i]), num);
            result[i] = e;
        }
        return result;
    }

    private int[] daysArrayIn28() {
        int[] days = new int[28];
        int index = 0;
        for (int i = DateUtils.dateBefore28Days(); i <= DateUtils.dateInLastDayInLastMonth(); i++) {
            days[index] = i;
            index++;
        }
        for (int i = 1; i <= DateUtils.dateToday(); i++) {
            days[index] = i;
            index++;
        }

        return days;
    }
}
