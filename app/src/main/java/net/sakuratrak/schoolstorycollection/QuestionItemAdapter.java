package net.sakuratrak.schoolstorycollection;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import net.sakuratrak.schoolstorycollection.R.drawable;
import net.sakuratrak.schoolstorycollection.R.id;
import net.sakuratrak.schoolstorycollection.R.layout;
import net.sakuratrak.schoolstorycollection.core.IListedDataProvidable;
import net.sakuratrak.schoolstorycollection.core.QuestionInfo;
import net.sakuratrak.schoolstorycollection.core.QuestionType;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by hv090 on 18/9/29.
 */

public abstract class QuestionItemAdapter extends Adapter {

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
        public QuestionType type;
        public boolean hidden;
        public OnClickListener detailClicked;
        public OnClickListener quizClicked;
        public OnLongClickListener showMenuClicked;
        public boolean checkAble = true;
        public boolean checked = false;
        public OnCheckedChangeListener onCheckChanged;
        public QuestionInfo db;


        public DataContext(String title, String authorTime, String unitInfo, Uri imgUri, float difficulty, boolean favourite, QuestionType type,boolean hidden, OnClickListener detailClicked, OnClickListener quizClicked, OnLongClickListener showMenuClicked, OnCheckedChangeListener onCheckChanged) {
            this.title = title;
            this.authorTime = authorTime;
            this.unitInfo = unitInfo;
            this.imgUri = imgUri;
            this.type = type;
            this.detailClicked = detailClicked;
            this.quizClicked = quizClicked;
            this.difficulty = difficulty;
            this.favourite = favourite;
            this.showMenuClicked = showMenuClicked;
            this.onCheckChanged = onCheckChanged;
            this.hidden = hidden;
        }

        public DataContext(String title, String authorTime, String unitInfo, Uri imgUri, float difficulty, boolean favourite,QuestionType type,boolean hidden) {
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(layout.adapter_question, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder vHolder, int position) {
            Holder holder = (Holder) vHolder;
            DataContext current = _dataContext.get(position);
            holder.title.setText(current.title);
            holder.title.getPaint().setStrikeThruText(current.hidden);
            switch (current.type) {
                case SINGLE_CHOICE:
                    holder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_filter_1_black_24dp, 0, 0, 0);
                    break;
                case MULTIPLY_CHOICE:
                    holder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_filter_2_black_24dp, 0, 0, 0);
                    break;
                case TYPEABLE_BLANK:
                    holder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_keyboard_black_24dp, 0, 0, 0);
                    break;
                case BLANK:
                    holder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_edit_black_24dp, 0, 0, 0);
                    break;
                case ANSWER:
                    holder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_done_black_24dp, 0, 0, 0);
                    break;
            }
            holder.valAuthorTime.setText(current.authorTime);
            holder.valUnit.setText(current.unitInfo);
            holder.previewImgContent.setImageURI(current.imgUri);
            holder.valDifficulty.setRating(current.difficulty);
            holder.valFavourite.setImageResource(current.favourite ? drawable.ic_favorite_pink_24dp : drawable.ic_favorite_border_black_24dp);
            OnClickListener listener = v -> {
                if (current.detailClicked != null) {
                    current.detailClicked.onClick(holder.previewImgBorder);
                }
            };
            holder.previewImgBorder.setOnClickListener(listener);
            holder.previewImgBorder.setOnLongClickListener(current.showMenuClicked);
            holder.btnDetail.setOnClickListener(listener);
            holder.btnQuiz.setOnClickListener(current.quizClicked);

        }

        public final static class Holder extends ViewHolder {
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
                previewImgContent = _root.findViewById(id.previewImgContent);
                previewImgBorder = _root.findViewById(id.previewImgBorder);
                title = _root.findViewById(id.title);
                valAuthorTime = _root.findViewById(id.valAuthorTime);
                valUnit = _root.findViewById(id.valUnit);
                btnQuiz = _root.findViewById(id.btnQuiz);
                btnDetail = _root.findViewById(id.btnDetail);
                valDifficulty = _root.findViewById(id.difficulty);
                valFavourite = _root.findViewById(id.favourite);
            }
        }

    }

    public static final class SimpleQuestionItemAdapter extends QuestionItemAdapter {

        public SimpleQuestionItemAdapter(IListedDataProvidable<DataContext> _dataContext) {
            super(_dataContext);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(layout.adapter_simple_question, viewGroup, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            Holder holder = (Holder) viewHolder;
            DataContext data = _dataContext.get(i);
            holder._textTitle.setText(data.title);
            holder._textTitle.getPaint().setStrikeThruText(data.hidden);
            switch (data.type) {
                case SINGLE_CHOICE:
                    holder._textTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_filter_1_black_24dp, 0, 0, 0);
                    break;
                case MULTIPLY_CHOICE:
                    holder._textTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_filter_2_black_24dp, 0, 0, 0);
                    break;
                case TYPEABLE_BLANK:
                    holder._textTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_keyboard_black_24dp, 0, 0, 0);
                    break;
                case BLANK:
                    holder._textTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_edit_black_24dp, 0, 0, 0);
                    break;
                case ANSWER:
                    holder._textTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_done_black_24dp, 0, 0, 0);
                    break;
            }
            holder._difficulty.setRating(data.difficulty);
            holder._previewImg.setImageURI(data.imgUri);
            holder._favourite.setImageResource(data.favourite ? drawable.ic_favorite_pink_24dp : drawable.ic_favorite_border_black_24dp);
            holder._rootView.setOnClickListener(data.detailClicked);
            holder._rootView.setOnLongClickListener(data.showMenuClicked);
            holder._multiCheckbox.setVisibility(data.checkAble ? View.VISIBLE : View.INVISIBLE);
            holder._multiCheckbox.setChecked(data.checked);
            holder._multiCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                data.checked = isChecked;
                if (data.onCheckChanged != null)
                    data.onCheckChanged.onCheckedChanged(buttonView, isChecked);
            });
        }

        public static class Holder extends ViewHolder {

            public final ConstraintLayout _rootView;
            public final ImageView _previewImg;
            public final TextView _textTitle;
            public final MaterialRatingBar _difficulty;
            public final ImageView _favourite;
            public final CheckBox _multiCheckbox;


            public Holder(@NonNull View itemView) {
                super(itemView);
                _rootView = itemView.findViewById(id.rootView);
                _previewImg = itemView.findViewById(id.previewImg);
                _difficulty = itemView.findViewById(id.difficulty);
                _textTitle = itemView.findViewById(id.textTitle);
                _favourite = itemView.findViewById(id.favourite);
                _multiCheckbox = itemView.findViewById(id.multiCheckbox);
            }
        }
    }
}