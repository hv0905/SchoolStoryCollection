package sakuratrak.schoolstorycollection;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hv090 on 18/9/29.
 */

public class QuestionItemAdapter extends RecyclerView.Adapter<QuestionItemAdapter.Holder> {

    private ArrayList<String> _mDataSet;
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_questionlistdisplay,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.title.setText(_mDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return _mDataSet.size();
    }

    public QuestionItemAdapter(ArrayList<String> mDataSet){
        _mDataSet = mDataSet;
    }



    public static class Holder extends RecyclerView.ViewHolder
    {
        private View _root;
        protected TextView title;

        protected Holder(View rootView){
            super(rootView);
            _root = rootView;
            title = _root.findViewById(R.id.layout_questionlistdisplay_title);
        }
    }
}
