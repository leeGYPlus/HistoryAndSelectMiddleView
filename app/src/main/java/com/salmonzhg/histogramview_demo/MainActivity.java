package com.salmonzhg.histogramview_demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.salmonzhg.histogramview_demo.utils.DateUtils;
import com.salmonzhg.histogramview_demo.views.HistogramView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SalmonZhg";
    private HistogramView mHistogram;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mHistogram = (HistogramView) findViewById(R.id.histogram);

        Button buttonPlay = (Button) findViewById(R.id.button_play);

        final HistogramView.HistogramEntity[] data = genRandomMonthData();

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHistogram.setData(data);
            }
        });

        mHistogram.setSelectListener(new HistogramView.OnSelectListener() {
            @Override
            public void onSelected(int index) {
                showToast(index + " selected" + "\nvalue: " + data[index].count);
            }
        });

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
