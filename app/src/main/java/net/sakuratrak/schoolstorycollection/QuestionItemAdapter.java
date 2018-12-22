package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hv090 on 18/9/29.
 */

public class QuestionItemAdapter extends RecyclerView.Adapter<QuestionItemAdapter.Holder> {

    private ArrayList<DataContext> _dataContext;

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_question, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        DataContext current =  _dataContext.get(position);
        holder.title.setText(current.title);
        holder.valAuthorTime.setText(current.authorTime);
        holder.valUnit.setText(current.unitInfo);
        holder.previewImgContent.setImageURI(current.imgUri);
        holder.valDifficulty.setRating(current.difficulty);
        holder.valFavourite.setImageResource(current.favourite ? R.drawable.ic_favorite_pink_24dp : R.drawable.ic_favorite_border_black_24dp);
        View.OnClickListener listener = v -> {
            if(current.detailClicked != null) {
                current.detailClicked.onClick(holder.previewImgBorder);
            }
        };
        holder.previewImgBorder.setOnClickListener(listener);
        holder.btnDetail.setOnClickListener(listener);
        holder.btnQuiz.setOnClickListener(current.quizClicked);
    }

    @Override
    public int getItemCount() {
        return _dataContext.size();
    }

    public QuestionItemAdapter(ArrayList<DataContext> mDataSet) {
        _dataContext = mDataSet;
    }

    public ArrayList<DataContext> get_dataContext() {
        return _dataContext;
    }

    public void set_dataContext(ArrayList<DataContext> _dataContext) {
        this._dataContext = _dataContext;
        notifyDataSetChanged();
    }

    public final static class Holder extends RecyclerView.ViewHolder {
        private View _root;
        public TextView title;
        public ImageView previewImgContent;
        public FrameLayout previewImgBorder;
        public TextView valAuthorTime;
        public TextView valUnit;
        public MaterialButton btnQuiz;
        public MaterialButton btnDetail;
        public RatingBar valDifficulty;
        public ImageView valFavourite;
        

        public Holder(View rootView) {
            super(rootView);
            _root = rootView;
            previewImgContent = _root.findViewById(R.id.previewImgContent);
            previewImgBorder = _root.findViewById(R.id.previewImgBorder);
            title = _root.findViewById(R.id.title);
            valAuthorTime = _root.findViewById(R.id.valAuthorTime);
            valUnit = _root.findViewById(R.id.valUnit);
            btnQuiz = _root.findViewById(R.id.btnQuiz);
            btnDetail = _root.findViewById(R.id.btnDetail);
            valDifficulty = _root.findViewById(R.id.difficulty);
            valFavourite = _root.findViewById(R.id.favourite);
        }
    }


    public static class DataContext {
        public String title;
        public String authorTime;
        public String unitInfo;
        public Uri imgUri;
        public float difficulty;
        public boolean favourite;
        public View.OnClickListener detailClicked;
        public View.OnClickListener quizClicked;

        public DataContext(String title, String authorTime, String unitInfo, Uri imgUri,float difficulty,boolean favourite, View.OnClickListener detailClicked, View.OnClickListener quizClicked) {
            this.title = title;
            this.authorTime = authorTime;
            this.unitInfo = unitInfo;
            this.imgUri = imgUri;
            this.detailClicked = detailClicked;
            this.quizClicked = quizClicked;
            this.difficulty = difficulty;
            this.favourite = favourite;
        }

        public DataContext(String title, String authorTime, String unitInfo, Uri imgUri,float difficulty,boolean favourite) {
            this.title = title;
            this.authorTime = authorTime;
            this.unitInfo = unitInfo;
            this.imgUri = imgUri;
            this.difficulty = difficulty;
            this.favourite = favourite;
        }

        public DataContext() {
        }

    }
    
    public static class SimpleAdapter extends ArrayAdapter<DataContext>{

        public SimpleAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }

        public SimpleAdapter(@NonNull Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
        }

        public SimpleAdapter(@NonNull Context context, int resource, @NonNull DataContext[] objects) {
            super(context, resource, objects);
        }

        public SimpleAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull DataContext[] objects) {
            super(context, resource, textViewResourceId, objects);
        }

        public SimpleAdapter(@NonNull Context context, int resource, @NonNull List<DataContext> objects) {
            super(context, resource, objects);
        }

        public SimpleAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<DataContext> objects) {
            super(context, resource, textViewResourceId, objects);
        }


        
        
        
    }


}
