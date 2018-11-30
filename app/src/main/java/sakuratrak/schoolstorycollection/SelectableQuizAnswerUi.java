package sakuratrakschoolstorycollection;

public class SelectableQuizAswerUi extends QuizAnswerUi {

    public AnswerUiCreatorView(Context context) {
        super(context);
        init();
    }

    public AnswerUiCreatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnswerUiCreatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AnswerUiCreatorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init(){
        LayoutInflacter.from(getContext()).inflate();
    }

}