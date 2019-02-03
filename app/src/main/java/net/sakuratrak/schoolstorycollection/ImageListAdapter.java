package net.sakuratrak.schoolstorycollection;

import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import net.sakuratrak.schoolstorycollection.ImageListAdapter.Holder;
import net.sakuratrak.schoolstorycollection.R.drawable;
import net.sakuratrak.schoolstorycollection.R.id;
import net.sakuratrak.schoolstorycollection.R.layout;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public final class ImageListAdapter extends Adapter<Holder> {

    private ArrayList<DataContext> _dataContext;
    private boolean _showAddButton;
    private OnClickListener _addButtonClicked;
    private DisplayMetrics _dp;


    public ImageListAdapter(ArrayList<DataContext> dataContext, boolean showAddButton) {
        _dataContext = dataContext;
        _showAddButton = showAddButton;
    }

    public OnClickListener getAddButtonClicked() {
        return _addButtonClicked;
    }

    public void setAddButtonClicked(OnClickListener _addButtonClicked) {
        this._addButtonClicked = _addButtonClicked;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        _dp = viewGroup.getResources().getDisplayMetrics();
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(layout.adapter_image_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder viewHolder, int i) {
        if (i >= _dataContext.size()) { //add button
            viewHolder._image.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, _dp);
            viewHolder._image.setImageResource(drawable.ic_add_gray_24dp);
            viewHolder._image.setScaleType(ScaleType.FIT_CENTER);
            viewHolder._root.setOnClickListener(_addButtonClicked);
        } else { //image
            viewHolder._image.setImageURI(_dataContext.get(i).imgSrc);
            viewHolder._image.setScaleType(ScaleType.CENTER_CROP);
            viewHolder._image.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
            viewHolder._root.setOnClickListener(_dataContext.get(i).imageClicked);
        }
    }

    @Override
    public int getItemCount() {
        if (_showAddButton)
            return _dataContext.size() + 1;
        return _dataContext.size();
    }

    public ArrayList<DataContext> get_dataContext() {
        return _dataContext;
    }

    public void set_dataContext(ArrayList<DataContext> _dataContext) {
        this._dataContext = _dataContext;
        notifyDataSetChanged();
    }

    public boolean isShowAddButton() {
        return _showAddButton;
    }

    public void setShowAddButton(boolean _showAddButton) {
        this._showAddButton = _showAddButton;
        notifyDataSetChanged();
    }

    public static final class Holder extends ViewHolder {

        final View _root;
        final ImageView _image;

        protected Holder(@NonNull View rootView) {
            super(rootView);
            _root = rootView;
            _image = rootView.findViewById(id.image);
        }
    }

    public static final class DataContext {
        public OnClickListener imageClicked;
        public Uri imgSrc;
    }
}
