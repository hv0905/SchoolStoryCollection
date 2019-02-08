package net.sakuratrak.schoolstorycollection.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import net.sakuratrak.schoolstorycollection.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShareHelper {
    private static final int TITLE_FONTSIZE = 50;
    private static final int TEXT_ANSWER_FONTSIZE = 40;
    private static final int ABOUT_FONTSIZE = 28;
    private static final int ELEMENT_MARGIN = 20;
    private static final int ELEMENT_MARGIN_HALF = 10;

    //NO OOM PLEASE NO NO NO!!!!!!!

    public static Bitmap generateShareBitmap(Context context, QuestionInfo question) {

        //
        // Step 1 : Measure
        //

        //分析最大宽度
        List<String> imgs = new ArrayList<>();
        imgs.addAll(Arrays.asList(question.getQuestionImage()));
        imgs.addAll(Arrays.asList(question.getAnalysisImage()));
        if (question.getAnswer() instanceof ImageAnswer) {
            imgs.addAll(((ImageAnswer) question.getAnswer()).Image);
        }
        int maxWidth = 0;
        int imgsSumHeight = 0;

        for (String img :
                imgs) {
            BitmapFactory.Options justInfo = new BitmapFactory.Options();
            justInfo.inJustDecodeBounds = true;
            File f = AppMaster.getImgFileDisplay(context, img);
            BitmapFactory.decodeFile(f.getAbsolutePath(), justInfo);
            if (maxWidth < justInfo.outWidth)
                maxWidth = justInfo.outWidth;
            //前后margin=5
            imgsSumHeight += justInfo.outHeight + ELEMENT_MARGIN;
        }


        Paint titlePaint = new Paint();
        titlePaint.setTextSize(TITLE_FONTSIZE);
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTypeface(Typeface.SANS_SERIF);

        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(2);

        Paint answerPaint = null;

        if (question.getAnswer() instanceof Answer.PlainTextAnswer) {
            answerPaint = new Paint();
            answerPaint.setTextSize(TEXT_ANSWER_FONTSIZE);
            answerPaint.setColor(context.getResources().getColor(R.color.colorAccent));
            answerPaint.setTypeface(Typeface.SANS_SERIF);

            String answerStr = question.getAnswer().toString();
            int width = (int) (answerPaint.measureText(answerStr) + 1);
            if (maxWidth < width)
                maxWidth = width;
        }

        int mainHeight = imgsSumHeight //图片宽度
                + TITLE_FONTSIZE + ELEMENT_MARGIN //题目的标题 + 间隙
                + TITLE_FONTSIZE + ELEMENT_MARGIN //答案的标题 + 间隙
                + (question.getAnalysisImage().length == 0 ? 0 : TITLE_FONTSIZE + ELEMENT_MARGIN)//解析 + 间隙
                + (question.getAnswer() instanceof Answer.PlainTextAnswer ? TEXT_ANSWER_FONTSIZE + ELEMENT_MARGIN : 0) //文字答案的间隙
                + ABOUT_FONTSIZE + ELEMENT_MARGIN + 20
                + ELEMENT_MARGIN_HALF;

        int mainWidth = maxWidth + ELEMENT_MARGIN;

        //
        // Step 2:Draw
        //

        Bitmap bm = Bitmap.createBitmap(mainWidth, mainHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bm);
        canvas.drawColor(Color.WHITE);
        float y = ELEMENT_MARGIN_HALF;
        //画'题目'
        canvas.drawText(context.getString(R.string.question), ELEMENT_MARGIN_HALF, y + TITLE_FONTSIZE, titlePaint);
        y += TITLE_FONTSIZE;
        canvas.drawLine(0, y + 1.5f, mainWidth, y + 1.5f, linePaint);
        y += ELEMENT_MARGIN;
        //开始画题目的图
        for (String img :
                question.getQuestionImage()) {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            Bitmap toDraw = BitmapFactory.decodeFile(AppMaster.getImgFileDisplay(context, img).getAbsolutePath(), opt);
            canvas.drawBitmap(toDraw, ELEMENT_MARGIN_HALF, y, linePaint);
            y += opt.outHeight + ELEMENT_MARGIN;
            toDraw.recycle();
        }

        canvas.drawText(context.getString(R.string.answer), ELEMENT_MARGIN_HALF, y + TITLE_FONTSIZE, titlePaint);
        y += TITLE_FONTSIZE;
        canvas.drawLine(0, y + 1.5f, mainWidth, y + 1.5f, linePaint);
        y += ELEMENT_MARGIN;

        if (question.getAnswer() instanceof ImageAnswer) {
            //imgAnswer
            for (String img :
                    ((ImageAnswer) question.getAnswer()).Image) {
                BitmapFactory.Options opt = new BitmapFactory.Options();
                Bitmap toDraw = BitmapFactory.decodeFile(AppMaster.getImgFileDisplay(context, img).getAbsolutePath(), opt);
                canvas.drawBitmap(toDraw, ELEMENT_MARGIN_HALF, y, linePaint);
                y += opt.outHeight + ELEMENT_MARGIN;
                toDraw.recycle();
            }
        } else {
            //textAnswer
            canvas.drawText(question.getAnswer().toString(), ELEMENT_MARGIN_HALF, y + TEXT_ANSWER_FONTSIZE, answerPaint);
            y += TITLE_FONTSIZE;
            y += ELEMENT_MARGIN;
        }

        if (question.getAnalysisImage().length != 0) {
            //要绘制解析
            canvas.drawText(context.getString(R.string.analysis), ELEMENT_MARGIN_HALF, y + TITLE_FONTSIZE, titlePaint);
            y += TITLE_FONTSIZE;
            canvas.drawLine(0, y + 1.5f, mainWidth, y + 1.5f, linePaint);
            y += ELEMENT_MARGIN;

            for (String img :
                    question.getAnalysisImage()) {
                BitmapFactory.Options opt = new BitmapFactory.Options();
                Bitmap toDraw = BitmapFactory.decodeFile(AppMaster.getImgFileDisplay(context, img).getAbsolutePath(), opt);
                canvas.drawBitmap(toDraw, ELEMENT_MARGIN_HALF, y, linePaint);
                y += opt.outHeight + ELEMENT_MARGIN;
                toDraw.recycle();
            }
        }

        //绘制by

        Paint byPaint = new Paint();
        byPaint.setTextSize(ABOUT_FONTSIZE);
        byPaint.setColor(context.getResources().getColor(R.color.flat2));
        byPaint.setTypeface(Typeface.SANS_SERIF);

        y += 20;

        canvas.drawText("Powered By 错题本Story SchoolStoryCollection", ELEMENT_MARGIN_HALF, y + ABOUT_FONTSIZE, byPaint);

        canvas.save();
        return bm;
    }
}
