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
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;
import net.sakuratrak.schoolstorycollection.core.QuestionType;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by hv090 on 18/9/29.
 */

public abstract class QuestionItemAdapter extends RecyclerView.Adapter {

    protected IListedDataProvidable<DataContext> _dataContext;

    protected QuestionItemAdapter(IListedDataProvidable<DataContext> _dataContext) {
        this._dataContext = _dataContext;
    }

    protected QuestionItemAdapter() {
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
        protected String title;
        protected String authorTime;
        protected String unitInfo;
        protected Uri imgUri;
        protected float difficulty;
        protected int reviewRatio;
        protected boolean favourite;
        protected QuestionType type;
        protected boolean hidden;
        protected View.OnClickListener detailClicked;
        protected View.OnClickListener quizClicked;
        protected View.OnLongClickListener showMenuClicked;
        protected boolean checkAble = true;
        public boolean checked = false;
        protected CompoundButton.OnCheckedChangeListener onCheckChanged;
        public QuestionInfo db;


        public DataContext(String title, String authorTime, String unitInfo, Uri imgUri, float difficulty, boolean favourite, QuestionType type, boolean hidden, int reviewRatio, View.OnClickListener detailClicked, View.OnClickListener quizClicked, View.OnLongClickListener showMenuClicked, CompoundButton.OnCheckedChangeListener onCheckChanged) {
            this.title = title;
            this.authorTime = authorTime;
            this.unitInfo = unitInfo;
            this.imgUri = imgUri;
            this.type = type;
            this.reviewRatio = reviewRatio;
            this.detailClicked = detailClicked;
            this.quizClicked = quizClicked;
            this.difficulty = difficulty;
            this.favourite = favourite;
            this.showMenuClicked = showMenuClicked;
            this.onCheckChanged = onCheckChanged;
            this.hidden = hidden;
        }

        public DataContext(String title, String authorTime, String unitInfo, Uri imgUri, float difficulty, boolean favourite, QuestionType type, boolean hidden) {
            this.title = title;
            this.authorTime = authorTime;
            this.unitInfo = unitInfo;
            this.imgUri = imgUri;
            this.difficulty = difficulty;
            this.favourite = favourite;
            this.type = type;
            this.hidden = hidden;
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
            holder.title.getPaint().setStrikeThruText(current.hidden);
            switch (current.type) {
                case SINGLE_CHOICE:
                    holder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_filter_1_black_24dp, 0, 0, 0);
                    break;
                case MULTIPLY_CHOICE:
                    holder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_filter_2_black_24dp, 0, 0, 0);
                    break;
                case TYPEABLE_BLANK:
                    holder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_keyboard_black_24dp, 0, 0, 0);
                    break;
                case BLANK:
                    holder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_edit_black_24dp, 0, 0, 0);
                    break;
                case ANSWER:
                    holder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_done_black_24dp, 0, 0, 0);
                    break;
            }
            holder.valAuthorTime.setText(current.authorTime);
            holder.valUnit.setText(current.unitInfo);
            holder.previewImgContent.setImageURI(current.imgUri);
            holder.valDifficulty.setRating(current.difficulty);
            holder._valReviewRatio.setText(current.reviewRatio == -1 ? "---" : String.format(Locale.US, "%d%%", current.reviewRatio));
            holder._valReviewRatio.setTextColor(UiHelper.getReviewColor(holder._valReviewRatio.getResources(), current.reviewRatio));
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

        protected final static class Holder extends RecyclerView.ViewHolder {
            protected final TextView title;
            protected final ImageView previewImgContent;
            protected final FrameLayout previewImgBorder;
            protected final TextView valAuthorTime;
            protected final TextView valUnit;
            protected final MaterialButton btnQuiz;
            protected final MaterialButton btnDetail;
            protected final RatingBar valDifficulty;
            protected final ImageView valFavourite;
            protected final View _root;
            protected final TextView _valReviewRatio;


            protected Holder(View rootView) {
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
                _valReviewRatio = _root.findViewById(R.id.valReviewRatio);
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
            QuestionItemAdapter.DataContext current = _dataContext.get(i);
            holder._textTitle.setText(current.title);
            holder._textTitle.getPaint().setStrikeThruText(current.hidden);
            switch (current.type) {
                case SINGLE_CHOICE:
                    holder._textTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_filter_1_black_24dp, 0, 0, 0);
                    break;
                case MULTIPLY_CHOICE:
                    holder._textTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_filter_2_black_24dp, 0, 0, 0);
                    break;
                case TYPEABLE_BLANK:
                    holder._textTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_keyboard_black_24dp, 0, 0, 0);
                    break;
                case BLANK:
                    holder._textTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_edit_black_24dp, 0, 0, 0);
                    break;
                case ANSWER:
                    holder._textTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_done_black_24dp, 0, 0, 0);
                    break;
            }
            holder._difficulty.setRating(current.difficulty);
            holder._previewImg.setImageURI(current.imgUri);
            holder._favourite.setImageResource(current.favourite ? R.drawable.ic_favorite_pink_24dp : R.drawable.ic_favorite_border_black_24dp);
            holder._valReviewRatio.setText(current.reviewRatio == -1 ? "---" : String.format(Locale.US, "%d%%", current.reviewRatio));
            holder._valReviewRatio.setTextColor(UiHelper.getReviewColor(holder._valReviewRatio.getResources(), current.reviewRatio));
            holder._rootView.setOnClickListener(current.detailClicked);
            holder._rootView.setOnLongClickListener(current.showMenuClicked);
            holder._multiCheckbox.setVisibility(current.checkAble ? View.VISIBLE : View.INVISIBLE);
            holder._multiCheckbox.setChecked(current.checked);
            holder._multiCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                current.checked = isChecked;
                if (current.onCheckChanged != null)
                    current.onCheckChanged.onCheckedChanged(buttonView, isChecked);
            });
        }

        protected static class Holder extends RecyclerView.ViewHolder {

            protected final ConstraintLayout _rootView;
            protected final ImageView _previewImg;
            protected final TextView _textTitle;
            protected final MaterialRatingBar _difficulty;
            protected final ImageView _favourite;
            protected final CheckBox _multiCheckbox;
            private final TextView _valReviewRatio;


            protected Holder(@NonNull View itemView) {
                super(itemView);
                _rootView = itemView.findViewById(R.id.rootView);
                _previewImg = itemView.findViewById(R.id.previewImg);
                _difficulty = itemView.findViewById(R.id.difficulty);
                _textTitle = itemView.findViewById(R.id.textTitle);
                _favourite = itemView.findViewById(R.id.favourite);
                _multiCheckbox = itemView.findViewById(R.id.multiCheckbox);
                _valReviewRatio = _rootView.findViewById(R.id.valReviewRatio);
            }
        }
    }
}