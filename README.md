### 项目说明
 > 本项目是在 [Histogram](https://github.com/billy96322/HistogramView) 和 [橫滑列表 中间项变大第二种效果
](http://www.jianshu.com/p/019087a0a80d)的基础上综合而成，实现了柱状图的左右滑动、仿其他 app 添加了头和尾、
   选中屏幕中间 item 并展示相关详细数据。








### 浏览

![](https://github.com/billy96322/HistogramView/blob/master/screen_capture/step_count_demo.gif "计步demo")

### 特性

- 比例将根据传入数据的最大值自动计算
- 可以更改一屏最大显示行数
- 颜色字体大小等属性可以更改

### 用法

在布局文件中添加

```Java
<com.salmonzhg.histogramview_demo.views.HistogramView
    android:id="@+id/histogram"
    android:layout_width="match_parent"
    android:layout_height="150dp"
    app:date_text_color="@color/colorPrimary"
    app:date_text_size="14sp"
    app:histogram_color="@color/colorPrimaryDark" />
```
在控制器中添加

```Java

    HistogramView.HistogramEntity[] entities = new HistogramView.HistogramEntity[30];
    for (int i = 0; i < entities.length; i++) {
        String showInTimeLime = String.valueOf(i); //也可以是 "Mon","Tue","Thr"
        int count = (int) (Math.random()*10); // 任意整型 
        HistogramView.HistogramEntity e = new HistogramView.HistogramEntity(
            showInTimeLime, count);
        entities[i] = e;
    }

    mHistogram.setSelectListener(new HistogramView.OnSelectListener() {
        @Override
        public void onSelected(int index) {
            showToast(index + " selected" + "\nvalue: " + data[index].count);
        }
    });

    // 设置数据同时也会触发动画
    mHistogram.setData(entities);


```
