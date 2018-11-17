package sakuratrak.schoolstorycollection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import sakuratrak.schoolstorycollection.core.AppMaster;
import sakuratrak.schoolstorycollection.core.AppSettingsMaster;
import sakuratrak.schoolstorycollection.core.DbManager;
import sakuratrak.schoolstorycollection.core.QuestionInfo;

import static android.app.Activity.RESULT_OK;

public final class MainActivityWorkBookFragment extends Fragment {

    public static final String TAG = "workbookFragment";

    private ConstraintLayout _root;
    private RecyclerView _itemList;
    private FloatingActionButton _addItemBtn;
    private Runnable _notifyToUpdate;
    private QuestionItemAdapter _mainAdapter;

    private int resultAddQuestion = MainActivity.RESULT_ADD_QUESTION;

    public Runnable getNotifyToUpdate() {
        return _notifyToUpdate;
    }

    public void setNotifyToUpdate(Runnable _notifyToUpdate) {
        this._notifyToUpdate = _notifyToUpdate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _root = (ConstraintLayout) inflater.inflate(R.layout.fragment_main_activity_workbook,container,false);
        _itemList = _root.findViewById(R.id.itemList);
        _addItemBtn = _root.findViewById(R.id.addItemBtn);

        _addItemBtn.setOnClickListener(v -> {
            //navigate to add activity
            CommonAlerts.AskQuestionType(getParent(), (dialogInterface, i) -> {
                Intent intent = new Intent(getParent(),QuestionEditActivity.class);
                intent.putExtra(QuestionEditActivity.EXTRA_QUESTION_TYPE_ID,i);
                intent.putExtra(QuestionEditActivity.EXTRA_SUBJECT,getParent().getCurrentSubject());
                getActivity().startActivityForResult(intent,resultAddQuestion);
            }, null);
        });

        _itemList.setLayoutManager(new LinearLayoutManager(getParent(), LinearLayoutManager.VERTICAL, false));
        _mainAdapter = new QuestionItemAdapter(new ArrayList<>());
        _itemList.setAdapter(_mainAdapter);
        refreshList();
        return _root;
    }

    private MainActivity getParent() {
        return (MainActivity) getActivity();
    }

    public void refreshList() {
        Log.d(TAG, "refreshList: go");
        ArrayList<QuestionItemAdapter.DataContext>  context = _mainAdapter.get_dataContext();
        context.clear();
        QuestionInfo.QuestionInfoDaoManager mgr = new QuestionInfo.QuestionInfoDaoManager(DbManager.getDefaultHelper(getContext()).getQuestionInfos());
        List<QuestionInfo> infos;
        try {
            infos = mgr.FindAllWithSubject(getParent().getCurrentSubject());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        for(QuestionInfo info : infos){
            QuestionItemAdapter.DataContext item = new QuestionItemAdapter.DataContext();
            item.title = info.getTitle();
            item.unitInfo = info.getUnit() != null ? info.getUnit().getName() : getString(R.string.emptyUnit);
            SimpleDateFormat format = new SimpleDateFormat("yy.mm.dd");
            item.authorTime = format.format(info.getAuthorTime());
            String imgId = info.getQuestionImage().get(0);
            File previewImgFile = new File(AppMaster.getLocalThumbCacheDir(getContext()),imgId);
            if(!previewImgFile.exists()){
                //create preview img file
                File fullSizeImg = new File(AppSettingsMaster.getWorkBookImageDir(getContext()),imgId);
                Bitmap thump = AndroidHelper.getThumbImg(fullSizeImg,500);
                try {
                    AndroidHelper.saveBitmap2File(previewImgFile,thump,Bitmap.CompressFormat.JPEG,90);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            item.imgUri = Uri.fromFile(previewImgFile);
            context.add(item);
        }
        _mainAdapter.set_dataContext(context);
    }

    public void myOnActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == resultAddQuestion){
            if(resultCode == RESULT_OK)
                refreshList();
        }
    }
}
