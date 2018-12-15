package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieDataSet;

public final class UiHelper {

    public static void applyAppearanceForPieDataSet(Context context, PieDataSet dataSet) {
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setColors(getFlatUiColors(context));
        dataSet.setValueLinePart1OffsetPercentage(80f);//数据连接线距图形片内部边界的距离，为百分数
        dataSet.setValueLinePart1Length(0.3f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setValueLineColor(Color.BLACK);//设置连接线的颜色
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setSliceSpace(3f);//设置饼块之间的间隔
        dataSet.setSelectionShift(5f);//设置饼块选中时偏离饼图中心的距离

    }

    public static void applyAppearanceForPie(Context context,PieChart pie){
        pie.setRotationEnabled(false);
        pie.setCenterTextSize(16);
        pie.setCenterTextColor(context.getResources().getColor(R.color.colorAccent));
        pie.getLegend().setEnabled(false);
        Description dsc = new Description();
        dsc.setText("");
        pie.setDescription(dsc);
    }

    public static void applyAppearanceForBar(Context context, BarChart bar){

    }

    public static int getFlatUiColor(Context context,int id){

        switch (id){
            case 0:
                return context.getResources().getColor(R.color.flat1);
            case 1:
                return context.getResources().getColor(R.color.flat2);
            case 2:
                return context.getResources().getColor(R.color.flat3);
            case 3:
                return context.getResources().getColor(R.color.flat4);
            case 4:
                return context.getResources().getColor(R.color.flat5);
            case 5:
                return context.getResources().getColor(R.color.flat6);
            case 6:
                return context.getResources().getColor(R.color.flat7);
            case 7:
                return context.getResources().getColor(R.color.flat8);
            case 8:
                return context.getResources().getColor(R.color.flat9);
                default:
                    throw new IllegalArgumentException("id");
        }
    }

    public static int[] getFlatUiColors(Context context){
        return new int[] {
                context.getResources().getColor(R.color.flat1),
                context.getResources().getColor(R.color.flat2),
                context.getResources().getColor(R.color.flat3),
                context.getResources().getColor(R.color.flat4),
                context.getResources().getColor(R.color.flat5),
                context.getResources().getColor(R.color.flat6),
                context.getResources().getColor(R.color.flat7),
                context.getResources().getColor(R.color.flat8),
                context.getResources().getColor(R.color.flat9)};
    }
}
