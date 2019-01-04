package net.sakuratrak.schoolstorycollection;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.button.MaterialButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import net.sakuratrak.schoolstorycollection.core.IListedDataProvidable;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by hv090 on 18/9/29.
 */

public abstract class QuestionItemAdapter extends RecyclerView.Adapter {

    protected IListedDataProvidable<DataContext> _dataContext;

    public IListedDataProvidable<DataContext> get_dataContext(){
        return _dataContext;
    }

    public void set_dataContext(IListedDataProvidable<DataContext> _dataContext) {
        this._dataContext = _dataContext;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return _dataContext.count();
    }

    public QuestionItemAdapter(IListedDataProvidable<DataContext> _dataContext) {
        this._dataContext = _dataContext;
    }

    public QuestionItemAdapter() {
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
        public View.OnLongClickListener showMenuClicked;

        public DataContext(String title, String authorTime, String unitInfo, Uri imgUri, float difficulty, boolean favourite, View.OnClickListener detailClicked, View.OnClickListener quizClicked, View.OnLongClickListener showMenuClicked) {
            this.title = title;
            this.authorTime = authorTime;
            this.unitInfo = unitInfo;
            this.imgUri = imgUri;
            this.detailClicked = detailClicked;
            this.quizClicked = quizClicked;
            this.difficulty = difficulty;
            this.favourite = favourite;
            this.showMenuClicked = showMenuClicked;
        }

        public DataContext(String title, String authorTime, String unitInfo, Uri imgUri, float difficulty, boolean favourite) {
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


    public static final class FullQuestionItemAdapter extends QuestionItemAdapter {

        public FullQuestionItemAdapter(IListedDataProvidable<DataContext> _dataContext) {
            super(_dataContext);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_question, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vHolder, int position) {
            Holder holder = (Holder) vHolder;
            DataContext current = _dataContext.get(position);
            holder.title.setText(current.title);
            holder.valAuthorTime.setText(current.authorTime);
            holder.valUnit.setText(current.unitInfo);
            holder.previewImgContent.setImageURI(current.imgUri);
            holder.valDifficulty.setRating(current.difficulty);
            holder.valFavourite.setImageResource(current.favourite ? R.drawable.ic_favorite_pink_24dp : R.drawable.ic_favorite_border_black_24dp);
            View.OnClickListener listener = v -> {
                if (current.detailClicked != null) {
                    current.detailClicked.onClick(holder.previewImgBorder);
                }
            };
            holder.previewImgBorder.setOnClickListener(listener);
            holder.previewImgBorder.setOnLongClickListener(current.showMenuClicked);
            holder.btnDetail.setOnClickListener(listener);
            holder.btnQuiz.setOnClickListener(current.quizClicked);
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

    }

    public static final class SimpleQuestionItemAdapter extends QuestionItemAdapter {

        public SimpleQuestionItemAdapter(IListedDataProvidable<DataContext> _dataContext) {
            super(_dataContext);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_simple_question, viewGroup, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            Holder holder = (Holder) viewHolder;
            QuestionItemAdapter.DataContext data = _dataContext.get(i);
            holder._textTitle.setText(data.title);
            holder._difficulty.setRating(data.difficulty);
            holder._previewImg.setImageURI(data.imgUri);
            holder._favourite.setImageResource(data.favourite ? R.drawable.ic_favorite_pink_24dp : R.drawable.ic_favorite_border_black_24dp);
            holder._rootView.setOnClickListener(data.detailClicked);
            holder._rootView.setOnLongClickListener(data.showMenuClicked);
        }

        public static class Holder extends RecyclerView.ViewHolder {

            public ConstraintLayout _rootView;
            public ImageView _previewImg;
            public TextView _textTitle;
            public MaterialRatingBar _difficulty;
            public ImageView _favourite;


            public Holder(@NonNull View itemView) {
                super(itemView);
                _rootView = itemView.findViewById(R.id.rootView);
                _previewImg = itemView.findViewById(R.id.previewImg);
                _difficulty = itemView.findViewById(R.id.difficulty);
                _textTitle = itemView.findViewById(R.id.textTitle);
                _favourite = itemView.findViewById(R.id.favourite);
            }
        }
    }
}