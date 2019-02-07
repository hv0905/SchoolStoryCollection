package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieDataSet.ValuePosition;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;

import net.sakuratrak.schoolstorycollection.R.color;

import java.text.SimpleDateFormat;
import java.util.Locale;

public final class UiHelper {

    public static final SimpleDateFormat defaultFormat = new SimpleDateFormat("yy.MM.dd", Locale.US);
    public static final SimpleDateFormat defaultFormatWithTime = new SimpleDateFormat("yy.MM.dd hh:mm", Locale.US);

    public static void applyAppearanceForPieDataSet(Context context, PieDataSet dataSet) {
        applyAppearanceForPieDataSet(context, dataSet,true);
    }

        public static void applyAppearanceForPieDataSet(Context context, PieDataSet dataSet,boolean drawVal) {
        dataSet.setColors(getFlatUiColors(context));
        if(drawVal) {
            dataSet.setDrawValues(true);
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setValueLinePart1OffsetPercentage(80f);//数据连接线距图形片内部边界的距离，为百分数
            dataSet.setValueLinePart1Length(0.3f);
            dataSet.setValueLinePart2Length(0.4f);
            dataSet.setValueLineColor(Color.BLACK);//设置连接线的颜色
            dataSet.setYValuePosition(ValuePosition.OUTSIDE_SLICE);
        }else{
            dataSet.setDrawValues(false);
        }

        dataSet.setSliceSpace(3f);//设置饼块之间的间隔
        dataSet.setSelectionShift(5f);//设置饼块选中时偏离饼图中心的距离
    }

    public static void applyAppearanceForBarDataSet(Context context, BarDataSet dataSet) {
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setColors(getFlatUiColors(context));
        dataSet.setValueTextSize(16);
        dataSet.setValueFormatter(new DefaultValueFormatter(0));
    }

    public static void applyAppearanceForPie(Context context, PieChart pie) {
        pie.setRotationEnabled(false);
        pie.setCenterTextSize(16);
        pie.setCenterTextColor(context.getResources().getColor(color.colorAccent));
        pie.setNoDataTextColor(context.getResources().getColor(color.colorAccent));
        pie.getLegend().setEnabled(false);
        Description dsc = new Description();
        dsc.setText("");
        pie.setDescription(dsc);
    }

    public static void applyAppearanceForBar(Context context, BarChart bar) {
        bar.setMaxVisibleValueCount(100);
        bar.setPinchZoom(false);
        bar.setDrawGridBackground(false);
        bar.setDragEnabled(false);
        bar.setScaleEnabled(false);
        Description dsc = new Description();
        dsc.setText("");
        bar.setDescription(dsc);
        bar.getLegend().setEnabled(false);

        XAxis xAxis = bar.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        YAxis leftAxis = bar.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(false);
        leftAxis.setTextSize(16);
        leftAxis.setAxisMinimum(0f);
        YAxis rightAxis = bar.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawAxisLine(false);
    }

    public static void applyAppearanceForLine(Context context, LineChart chart) {
        chart.setMaxVisibleValueCount(100);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.getLegend().setEnabled(false);
        Description dsc = new Description();
        dsc.setText("");
        chart.setDescription(dsc);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(5);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(false);
        leftAxis.setTextSize(16);
        leftAxis.setAxisMinimum(0f);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawAxisLine(false);
    }

    public static void applyAppearanceForLineDataSet(Context context, LineDataSet dataSet) {
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setCubicIntensity(0.2f);
        dataSet.setDrawFilled(true);
        dataSet.setDrawCircles(true);
        dataSet.setLineWidth(1.8f);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleColor(context.getResources().getColor(color.colorPrimary));
        dataSet.setHighLightColor(context.getResources().getColor(color.transparent));
        dataSet.setColor(context.getResources().getColor(color.colorPrimary));
        dataSet.setFillColor(context.getResources().getColor(color.colorPrimary));
        dataSet.setFillAlpha(100);
        dataSet.setDrawHorizontalHighlightIndicator(false);
    }

    public static int getFlatUiColor(Context context, int id) {

        switch (id) {
            case 0:
                return context.getResources().getColor(color.flat1);
            case 1:
                return context.getResources().getColor(color.flat2);
            case 2:
                return context.getResources().getColor(color.flat3);
            case 3:
                return context.getResources().getColor(color.flat4);
            case 4:
                return context.getResources().getColor(color.flat5);
            case 5:
                return context.getResources().getColor(color.flat6);
            case 6:
                return context.getResources().getColor(color.flat7);
            case 7:
                return context.getResources().getColor(color.flat8);
            case 8:
                return context.getResources().getColor(color.flat9);
            default:
                throw new IllegalArgumentException("id");
        }
    }

    public static int[] getFlatUiColors(Context context) {
        return new int[]{
                context.getResources().getColor(color.flat1),
                context.getResources().getColor(color.flat2),
                context.getResources().getColor(color.flat3),
                context.getResources().getColor(color.flat4),
                context.getResources().getColor(color.flat5),
                context.getResources().getColor(color.flat6),
                context.getResources().getColor(color.flat7),
                context.getResources().getColor(color.flat8),
                context.getResources().getColor(color.flat9)};
    }

    public static int getWarnColorByScore(Resources res, int score) {
        int scoreLevel = score / 25;
        int uiColor;
        switch (scoreLevel) {
            case 0://0-25
                uiColor = res.getColor(color.flat8);
                break;
            case 1://25-50
                uiColor = res.getColor(color.flat2);
                break;
            case 2://50-75
                uiColor = res.getColor(color.flat2);
                break;
            case 3://75-100
                uiColor = res.getColor(color.flat5);
                break;
            default://100+
                uiColor = res.getColor(color.flat7);
                break;
        }
        return uiColor;
    }

    public static int getReviewColor(Resources res, ReviewRatio ratio) {
        switch (ratio) {
            case NICE:
                return res.getColor(R.color.flat5);
            case MID:
                return res.getColor(R.color.flat7);
            case BAD:
                return res.getColor(R.color.flat8);
            case UNKNOWN:
                return res.getColor(R.color.black);
        }
        throw new IllegalArgumentException();
    }

    public static int getReviewColor(Resources res, int stat) {
        return getReviewColor(res, ReviewRatio.getByRatio(stat));
    }


}
