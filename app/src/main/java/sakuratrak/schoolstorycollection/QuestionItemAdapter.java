package sakuratrak.schoolstorycollection;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hv090 on 18/9/29.
 */

public class QuestionItemAdapter extends RecyclerView.Adapter<QuestionItemAdapter.Holder> {

    private ArrayList<QuestionItemDataContext> _dataContext;

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_questionlistdisplay, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        QuestionItemDataContext current =  _dataContext.get(position);
        holder.title.setText(current.title);
        holder.valAuthorTime.setText(current.authorTime);
        holder.valUnit.setText(current.unitInfo);
    }

    @Override
    public int getItemCount() {
        return _dataContext.size();
    }

    public QuestionItemAdapter(ArrayList<QuestionItemDataContext> mDataSet) {
        _dataContext = mDataSet;
    }


    public static class Holder extends RecyclerView.ViewHolder {
        private View _root;
        protected TextView title;
        protected ImageView previewImg;
        protected TextView valAuthorTime;
        protected TextView valUnit;

        protected Holder(View rootView) {
            super(rootView);
            _root = rootView;
            previewImg = _root.findViewById(R.id.previewImg);
            title = _root.findViewById(R.id.title);
            valAuthorTime = _root.findViewById(R.id.valAuthorTime);
            valUnit = _root.findViewById(R.id.valUnit);
        }
    }


    public static class QuestionItemDataContext {
        protected String title;
        protected String authorTime;
        protected String unitInfo;
        protected Uri imgUri;
        protected View.OnClickListener detailClicked;
        protected View.OnClickListener quizClicked;

        public QuestionItemDataContext(String title, String authorTime, String unitInfo, Uri imgUri, View.OnClickListener detailClicked, View.OnClickListener quizClicked) {
            this.title = title;
            this.authorTime = authorTime;
            this.unitInfo = unitInfo;
            this.imgUri = imgUri;
            this.detailClicked = detailClicked;
            this.quizClicked = quizClicked;
        }

        public QuestionItemDataContext(String title, String authorTime, String unitInfo, Uri imgUri) {
            this.title = title;
            this.authorTime = authorTime;
            this.unitInfo = unitInfo;
            this.imgUri = imgUri;
        }

        public QuestionItemDataContext() {
        }
    }
}
