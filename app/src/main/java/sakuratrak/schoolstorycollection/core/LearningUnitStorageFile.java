package sakuratrak.schoolstorycollection.core;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public final class LearningUnitStorageFile implements Serializable {
    public static final String INTERNAL_FILE_NAME = "units.db";
    static LearningUnitStorageFile _default;

    HashMap<LearningSubject,ArrayList<LearningUnitInfo>> _values;

    public LearningUnitStorageFile(){
        _values = new HashMap<>();
    }


    public static LearningUnitStorageFile getDefault() {
        return _default;
    }

    public static void setDefault(LearningUnitStorageFile value){
        _default = value;
    }


    public ArrayList<LearningUnitInfo> getUnits(LearningSubject subject){
        if(_values.containsKey(subject)){
            return _values.get(subject);
        }else{
            return null;
        }
    }

    public void setUnits(LearningSubject subject,ArrayList<LearningUnitInfo> obj){
        _values.put(subject,obj);
    }

    public HashMap<LearningSubject,ArrayList<LearningUnitInfo>> getValue(){
        return _values;
    }

    public static LearningUnitStorageFile readFromInternalStorage(@NonNull Activity activity) {
        String[] fileList = activity.fileList();
        if(Arrays.binarySearch(fileList, INTERNAL_FILE_NAME) < 0)
            return null;
        try {
            try (FileInputStream fis = activity.openFileInput(INTERNAL_FILE_NAME); ObjectInputStream ois = new ObjectInputStream(fis)) {
                return (LearningUnitStorageFile) ois.readObject();
            }
        }catch (IOException | ClassNotFoundException io){
            return null;
        }

    }

    public void saveToInternalStorage(@NonNull Activity activity) throws IOException {
        FileOutputStream fos = activity.openFileOutput(INTERNAL_FILE_NAME,Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.close();
        fos.close();
    }

//    public static LearningUnitStorageFile readFromFile(Uri location){
//
//    }

//    public void saveToFile(Uri location){
//
//    }

    public static boolean defaultLoaded(){
        return _default != null;
    }


}
