package sakuratrak.schoolstorycollection;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public final class ImageListEditAdapter extends RecyclerView.Adapter<ImageListEditAdapter.Holder> {

    private ArrayList<ImageListEditDataContext> _dataContext;
    private boolean _showAddButton;
    private View.OnClickListener _addButtonClicked;
    private Context _context;


    public View.OnClickListener getAddButtonClicked() {
        return _addButtonClicked;
    }

    public void setAddButtonClicked(View.OnClickListener _addButtonClicked) {
        this._addButtonClicked = _addButtonClicked;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_image_list_edit, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder viewHolder, int i) {
        if(i >= _dataContext.size()) { //add button
            ViewGroup.LayoutParams layoutParams = viewHolder._root.getLayoutParams();
            viewHolder._root.setLayoutParams(layoutParams);
            viewHolder._image.setImageResource(R.drawable.ic_add_gray_24dp);
            viewHolder._image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            viewHolder._root.setOnClickListener(_addButtonClicked);
        }else { //image
            viewHolder._image.setImageURI(_dataContext.get(i).imgSrc);
            viewHolder._image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewHolder._root.setOnClickListener(_dataContext.get(i).imageClicked);
        }
    }

    @Override
    public int getItemCount() {
        if(_showAddButton)
            return _dataContext.size() + 1;
        return _dataContext.size();
    }

    public ImageListEditAdapter(Context context,ArrayList<ImageListEditDataContext> dataContext, boolean showAddButton) {
        _context = context;
        _dataContext = dataContext;
        _showAddButton = showAddButton;
    }

    public ArrayList<ImageListEditDataContext> get_dataContext() {
        return _dataContext;
    }

    public void set_dataContext(ArrayList<ImageListEditDataContext> _dataContext) {
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

    public static final class Holder extends RecyclerView.ViewHolder {

        View _root;
        ImageView _image;

        protected Holder(@NonNull View rootView) {
            super(rootView);
            _root = rootView;
            _image = rootView.findViewById(R.id.image);
        }
    }

    public static final class ImageListEditDataContext {
        public View.OnClickListener imageClicked;
        public Uri imgSrc;
    }
}
