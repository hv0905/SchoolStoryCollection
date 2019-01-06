package net.sakuratrak.schoolstorycollection;

import android.os.Build;
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

public class ExerciseLogAdapter extends RecyclerView.Adapter<ExerciseLogAdapter.Holder> {

    private IListedDataProvidable<DataContext> _context;

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_exercise_log, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        DataContext current = _context.get(position);
        holder._questionId.setText(String.valueOf(current.Id));
        holder._title.setText(current.Name);
        holder._unit.setText(current.Unit);
        holder._scoreProgressVal.setText(String.valueOf(current.Score));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder._scoreProgress.setProgress(current.Score,true);
        }else {
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

    public ExerciseLogAdapter(IListedDataProvidable<DataContext> _context) {
        this._context = _context;
    }

    public static final class Holder extends RecyclerView.ViewHolder {

        public ConstraintLayout _rootView;
        public TextView _questionId;
        public TextView _title;
        public TextView _unit;
        public TextView _scoreProgressVal;
        public ProgressBar _scoreProgress;

        public Holder(@NonNull View itemView) {
            super(itemView);
            _rootView = itemView.findViewById(R.id.rootView);
            _questionId = itemView.findViewById(R.id.questionId);
            _title = itemView.findViewById(R.id.title);
            _unit = itemView.findViewById(R.id.unit);
            _scoreProgress = itemView.findViewById(R.id.scoreProgress);
            _scoreProgressVal = itemView.findViewById(R.id.scoreProgressVal);
        }
    }

    public static final class DataContext {
        public int Id;
        public String Name;
        public String Unit;
        public int Score;

        public DataContext(int id, String name, String unit, int score) {
            Id = id;
            Name = name;
            Unit = unit;
            Score = score;
        }

        public DataContext() {
        }
    }
}