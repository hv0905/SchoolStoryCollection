package sakuratrak.schoolstorycollection;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public final class ImageListEditAdapter extends RecyclerView.Adapter {

    private ArrayList<ImageListEditDataContext> _dataContext;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_image_list_edit,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Holder hd = (Holder) viewHolder;
        hd._image.setImageURI(_dataContext.get(i).imgSrc);
        hd._root.setOnClickListener(_dataContext.get(i).imageClicked);
    }

    @Override
    public int getItemCount() {
        return _dataContext.size();
    }

    public ImageListEditAdapter(ArrayList<ImageListEditDataContext> dataContext){
        _dataContext = dataContext;
    }

    private static final class Holder extends RecyclerView.ViewHolder{

        View _root;
        ImageView _image;

        protected Holder(@NonNull View rootView) {
            super(rootView);
            _root = rootView;
            _image = rootView.findViewById(R.id.image);
        }
    }

    protected static final class ImageListEditDataContext {
        protected View.OnClickListener imageClicked;
        protected Uri imgSrc;
    }
}
