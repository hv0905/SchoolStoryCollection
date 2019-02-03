package net.sakuratrak.schoolstorycollection;

import android.content.res.ColorStateList;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.sakuratrak.schoolstorycollection.ExerciseLogAdapter.Holder;
import net.sakuratrak.schoolstorycollection.R.id;
import net.sakuratrak.schoolstorycollection.R.layout;
import net.sakuratrak.schoolstorycollection.core.IListedDataProvidable;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class ExerciseLogAdapter extends Adapter<Holder> {

    private IListedDataProvidable<DataContext> _context;

    public ExerciseLogAdapter(IListedDataProvidable<DataContext> _context) {
        this._context = _context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout.adapter_exercise_log, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        DataContext current = _context.get(position);
        holder._questionId.setText(String.valueOf(current.Id));
        holder._title.setText(current.Name);
        holder._unit.setText(current.Unit);
        holder._scoreProgress.setProgressTintList(
                ColorStateList.valueOf(
                        UiHelper.getWarnColorByScore(
                                holder._scoreProgress.getResources()
                                , current.Score)));
        holder._scoreProgressVal.setText(String.valueOf(current.Score));
        holder._rootView.setOnClickListener(current.onClick);
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            holder._scoreProgress.setProgress(current.Score, false);
        } else {
            holder._scoreProgress.setProgress(current.Score);
        }

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

    public static final class Holder extends ViewHolder {

        public final ConstraintLayout _rootView;
        public final TextView _questionId;
        public final TextView _title;
        public final TextView _unit;
        public final TextView _scoreProgressVal;
        public final ProgressBar _scoreProgress;

        public Holder(@NonNull View itemView) {
            super(itemView);
            _rootView = itemView.findViewById(id.rootView);
            _questionId = itemView.findViewById(id.questionId);
            _title = itemView.findViewById(id.title);
            _unit = itemView.findViewById(id.unit);
            _scoreProgress = itemView.findViewById(id.scoreProgress);
            _scoreProgressVal = itemView.findViewById(id.scoreProgressVal);
        }
    }

    public static final class DataContext {
        public int Id;
        public String Name;
        public String Unit;
        public int Score;
        public OnClickListener onClick;

        public DataContext(int id, String name, String unit, int score) {
            Id = id;
            Name = name;
            Unit = unit;
            Score = score;
        }

        public DataContext(int id, String name, String unit, int score, OnClickListener onClick) {
            Id = id;
            Name = name;
            Unit = unit;
            Score = score;
            this.onClick = onClick;
        }

        public DataContext() {
        }
    }
}
