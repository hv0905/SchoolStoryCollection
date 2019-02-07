package net.sakuratrak.schoolstorycollection;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.sakuratrak.schoolstorycollection.core.IListedDataProvidable;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public final class ExerciseLogGroupAdapter extends RecyclerView.Adapter<ExerciseLogGroupAdapter.Holder> {

    private IListedDataProvidable<DataContext> _context;

    public ExerciseLogGroupAdapter(IListedDataProvidable<DataContext> _context) {
        this._context = _context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_exercise_log_group, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        DataContext context = _context.get(position);
        holder._textQuestionCount.setText(String.format(Locale.US, "%d道题目", context.questionCount));
        holder._textTitle.setText(context.title);
        holder._textHappenTime.setText(context.happenTime);
        holder._scoreProgressVal.setText(String.valueOf(context.score));
        holder._scoreProgress.setProgress(context.score);
        holder._scoreProgress.setProgressTintList(
                ColorStateList.valueOf(
                        UiHelper.getWarnColorByScore(holder._scoreProgress.getResources(), context.score)));
        holder._rootView.setOnClickListener(context.onClick);
        holder._rootView.setOnLongClickListener(context.onLongClick);

    }

    @Override
    public int getItemCount() {
        return _context.count();
    }

    public IListedDataProvidable<DataContext> get_context() {
        return _context;
    }

    public void set_context(IListedDataProvidable<DataContext> _context) {
        this._context = _context;
    }

    public static class Holder extends RecyclerView.ViewHolder {

        protected final ConstraintLayout _rootView;
        protected final TextView _textTitle;
        protected final TextView _textQuestionCount;
        protected final TextView _textHappenTime;
        protected final TextView _scoreProgressVal;
        protected final ProgressBar _scoreProgress;


        protected Holder(@NonNull View itemView) {
            super(itemView);
            _rootView = itemView.findViewById(R.id.rootView);
            _textTitle = itemView.findViewById(R.id.textTitle);
            _textQuestionCount = itemView.findViewById(R.id.textQuestionCount);
            _textHappenTime = itemView.findViewById(R.id.textHappenTime);
            _scoreProgressVal = itemView.findViewById(R.id.scoreProgressVal);
            _scoreProgress = itemView.findViewById(R.id.scoreProgress);
        }
    }

    public static class DataContext {
        protected String title;
        protected String happenTime;
        protected int questionCount;
        protected int score;
        protected View.OnClickListener onClick;
        protected View.OnLongClickListener onLongClick;

        public DataContext(String title, String happenTime, int questionCount, int score, View.OnClickListener onClick,View.OnLongClickListener onLongClick) {
            this.title = title;
            this.happenTime = happenTime;
            this.questionCount = questionCount;
            this.score = score;
            this.onClick = onClick;
            this.onLongClick = onLongClick;
        }

        public DataContext(String title, String happenTime, int questionCount, int score) {
            this.title = title;
            this.happenTime = happenTime;
            this.questionCount = questionCount;
            this.score = score;
        }

        public DataContext() {

        }
    }


}
