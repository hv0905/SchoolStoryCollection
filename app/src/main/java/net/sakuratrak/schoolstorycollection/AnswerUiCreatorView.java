package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import net.sakuratrak.schoolstorycollection.core.Answer;

public abstract class AnswerUiCreatorView extends FrameLayout {

    protected OnUpdateEventHandler _onUpdate;

    public AnswerUiCreatorView(Context context) {
        super(context);
    }

    public AnswerUiCreatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnswerUiCreatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AnswerUiCreatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public abstract Answer getAnswer();

    public abstract void setAnswer(Answer value);

    public abstract boolean hasAnswer();

    public void setOnUpdateEventHandler(OnUpdateEventHandler val){
        _onUpdate = val;
    }

    protected void toggleOnUpdate(){
        if(_onUpdate != null){
            _onUpdate.onUpdate(this);
        }
    }


    @FunctionalInterface
    public interface OnUpdateEventHandler{
        void onUpdate(AnswerUiCreatorView sender);
    }

}
