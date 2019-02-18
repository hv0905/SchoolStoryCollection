package me.kareluo.imaging;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import me.kareluo.imaging.core.IMGMode;
import me.kareluo.imaging.core.IMGText;
import me.kareluo.imaging.view.IMGColorGroup;
import me.kareluo.imaging.view.IMGView;

/**
 * Created by felix on 2017/12/5 下午3:08.
 */

abstract class IMGEditBaseActivity extends AppCompatActivity implements View.OnClickListener,
        IMGTextEditDialog.Callback, RadioGroup.OnCheckedChangeListener,
        DialogInterface.OnShowListener, DialogInterface.OnDismissListener {

    protected static final int OP_NORMAL = 0;
    protected static final int OP_CLIP = 1;
    private static final int OP_HIDE = -1;
    private static final int OP_SUB_DOODLE = 0;
    private static final int OP_SUB_MOSAIC = 1;
    private static final int OP_SUB_CONTRAST = 2;
    public static final String EXTRA_ADD_CONTRAST = "add_contrast";
    protected IMGView mImgView;
    private RadioGroup mModeGroup;
    private IMGColorGroup mColorGroup;
    private IMGTextEditDialog mTextDialog;
    private View mLayoutOpSub;
    private ViewSwitcher mOpSwitcher, mOpSubSwitcher;
    private LinearLayout _contrastBlock;
    private SeekBar _slideContrast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bitmap bitmap = getBitmap();
        if (bitmap != null) {
            setContentView(R.layout.image_edit_activity);
            initViews();
            mImgView.setImageBitmap(bitmap);
            int contrast = getIntent().getIntExtra(EXTRA_ADD_CONTRAST, 50);
            mImgView.mImage.set_contrastVal(contrast);
            _slideContrast.setProgress(contrast);
            int angle = getExifRotation();
            mImgView.mImage.rotate(angle);
            mImgView.mImage.setRotate(angle);
        } else finish();

    }

//    /**
//     * Hide system NavigationBar and StatusBar
//     */
//    public void hideNavigationBar()
//    {
//        final View decorView = getWindow().getDecorView();
//        decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
//            @Override
//            public void onSystemUiVisibilityChange(int visibility) {
//                Log.i("LOG","Menu Shown is this"+ visibility);
//                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
//
//            }
//        });
//    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus)
//        {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE
//            );
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        hideNavigationBar();
//        super.onResume();
//    }

    private void initViews() {
        mImgView = findViewById(R.id.image_canvas);
        mModeGroup = findViewById(R.id.rg_modes);

        mOpSwitcher = findViewById(R.id.vs_op);
        mOpSubSwitcher = findViewById(R.id.vs_op_sub);

        mColorGroup = findViewById(R.id.cg_colors);
        mColorGroup.setOnCheckedChangeListener(this);

        mLayoutOpSub = findViewById(R.id.layout_op_sub);
        _contrastBlock = findViewById(R.id.contrastBlock);
        _slideContrast = findViewById(R.id.slideContrast);

        _slideContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mImgView.mImage.set_contrastVal(progress);
                mImgView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.rb_doodle) {
            onModeClick(IMGMode.DOODLE);
        } else if (vid == R.id.btn_text) {
            onTextModeClick();
        } else if (vid == R.id.rb_contrast) {
            onModeClick(IMGMode.CONTRAST);
        } else if (vid == R.id.rb_mosaic) {
            onModeClick(IMGMode.MOSAIC);
        } else if (vid == R.id.btn_clip) {
            onModeClick(IMGMode.CLIP);
        } else if (vid == R.id.btn_undo) {
            onUndoClick();
        } else if (vid == R.id.tv_done) {
            onDoneClick();
        } else if (vid == R.id.tv_cancel) {
            onCancelClick();
        } else if (vid == R.id.ib_clip_cancel) {
            onCancelClipClick();
        } else if (vid == R.id.ib_clip_done) {
            onDoneClipClick();
        } else if (vid == R.id.tv_clip_reset) {
            onResetClipClick();
        } else if (vid == R.id.ib_clip_rotate) {
            onRotateClipClick();
        }
    }

    protected void updateModeUI() {
        IMGMode mode = mImgView.getMode();
        switch (mode) {
            case DOODLE:
                mModeGroup.check(R.id.rb_doodle);
                setOpSubDisplay(OP_SUB_DOODLE);
                break;
            case MOSAIC:
                mModeGroup.check(R.id.rb_mosaic);
                setOpSubDisplay(OP_SUB_MOSAIC);
                break;
            case NONE:
                mModeGroup.clearCheck();
                setOpSubDisplay(OP_HIDE);
                break;
            case CONTRAST:
                mModeGroup.check(R.id.rb_contrast);
                setOpSubDisplay(OP_SUB_CONTRAST);
                break;
        }
    }

    private void onTextModeClick() {
        if (mTextDialog == null) {
            mTextDialog = new IMGTextEditDialog(this, this);
            mTextDialog.setOnShowListener(this);
            mTextDialog.setOnDismissListener(this);
        }
        mTextDialog.show();
    }

    @Override
    public final void onCheckedChanged(RadioGroup group, int checkedId) {
        onColorChanged(mColorGroup.getCheckColor());
    }

    protected void setOpDisplay(int op) {
        if (op >= 0) {
            mOpSwitcher.setDisplayedChild(op);
        }
    }

    private void setOpSubDisplay(int opSub) {
        if (opSub < 0) {
            mLayoutOpSub.setVisibility(View.GONE);
            _contrastBlock.setVisibility(View.GONE);
        } else {
            switch (opSub){
                case OP_SUB_CONTRAST:
                    _contrastBlock.setVisibility(View.VISIBLE);
                    mLayoutOpSub.setVisibility(View.GONE);
                    break;
                case OP_SUB_DOODLE:
                case OP_SUB_MOSAIC:
                    mOpSubSwitcher.setDisplayedChild(opSub);
                    _contrastBlock.setVisibility(View.GONE);
                    mLayoutOpSub.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        mOpSwitcher.setVisibility(View.GONE);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mOpSwitcher.setVisibility(View.VISIBLE);
    }

    protected abstract Bitmap getBitmap();

    protected abstract void onModeClick(IMGMode mode);

    protected abstract void onUndoClick();

    protected abstract void onCancelClick();

    protected abstract void onDoneClick();

    protected abstract void onCancelClipClick();

    protected abstract void onDoneClipClick();

    protected abstract void onResetClipClick();

    protected abstract void onRotateClipClick();

    protected abstract void onColorChanged(int checkedColor);

    public abstract int getExifRotation();

    @Override
    public abstract void onText(IMGText text);
}
