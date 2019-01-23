package net.sakuratrak.schoolstorycollection;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import net.sakuratrak.schoolstorycollection.core.IListedDataProvidable;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by hv090 on 18/9/29.
 */

public abstract class QuestionItemAdapter extends RecyclerView.Adapter {

    protected IListedDataProvidable<DataContext> _dataContext;

    public QuestionItemAdapter(IListedDataProvidable<DataContext> _dataContext) {
        this._dataContext = _dataContext;
    }

    public QuestionItemAdapter() {
    }

    public IListedDataProvidable<DataContext> get_dataContext() {
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
        public boolean checkAble = true;
        public boolean checked = false;
        public CompoundButton.OnCheckedChangeListener onCheckChanged;


        public DataContext(String title, String authorTime, String unitInfo, Uri imgUri, float difficulty, boolean favourite, View.OnClickListener detailClicked, View.OnClickListener quizClicked, View.OnLongClickListener showMenuClicked, CompoundButton.OnCheckedChangeListener onCheckChanged) {
            this.title = title;
            this.authorTime = authorTime;
            this.unitInfo = unitInfo;
            this.imgUri = imgUri;
            this.detailClicked = detailClicked;
            this.quizClicked = quizClicked;
            this.difficulty = difficulty;
            this.favourite = favourite;
            this.showMenuClicked = showMenuClicked;
            this.onCheckChanged = onCheckChanged;
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
            public final TextView title;
            public final ImageView previewImgContent;
            public final FrameLayout previewImgBorder;
            public final TextView valAuthorTime;
            public final TextView valUnit;
            public final MaterialButton btnQuiz;
            public final MaterialButton btnDetail;
            public final RatingBar valDifficulty;
            public final ImageView valFavourite;
            private final View _root;


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
            holder._multiCheckbox.setOnCheckedChangeListener(data.onCheckChanged);
        }

        public static class Holder extends RecyclerView.ViewHolder {

            public final ConstraintLayout _rootView;
            public final ImageView _previewImg;
            public final TextView _textTitle;
            public final MaterialRatingBar _difficulty;
            public final ImageView _favourite;
            public final CheckBox _multiCheckbox;


            public Holder(@NonNull View itemView) {
                super(itemView);
                _rootView = itemView.findViewById(R.id.rootView);
                _previewImg = itemView.findViewById(R.id.previewImg);
                _difficulty = itemView.findViewById(R.id.difficulty);
                _textTitle = itemView.findViewById(R.id.textTitle);
                _favourite = itemView.findViewById(R.id.favourite);
                _multiCheckbox = itemView.findViewById(R.id.multiCheckbox);
            }
        }
    }
}