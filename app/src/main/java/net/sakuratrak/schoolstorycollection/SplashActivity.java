package net.sakuratrak.schoolstorycollection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import net.sakuratrak.schoolstorycollection.core.AppSettingsMaster;

public class SplashActivity extends AppCompatActivity {

    public static final int PERMISSION_EXTERNAL_STORAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
        //需要检查External Storage 访问权限
        if(PermissionAdmin.get(this,Manifest.permission.WRITE_EXTERNAL_STORAGE,PERMISSION_EXTERNAL_STORAGE))
        {
            loadApp();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_EXTERNAL_STORAGE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //ok
                    loadApp();
                }else{
                    if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        new AlertDialog.Builder(this).setTitle("需要内部存储读写权限").setMessage("错题本Story的错题数据存储在手机内部存储空间中,所以需要内部存储空间权限.否则错题本Story无法正常工作.\n点击'确定'给Story酱分配权限吧~").setPositiveButton("确定", (dialog, which) -> {
                            if(PermissionAdmin.get(this,Manifest.permission.WRITE_EXTERNAL_STORAGE,PERMISSION_EXTERNAL_STORAGE))
                            {
                                loadApp();
                            }
                        }).show();

                    }else{
                        new AlertDialog.Builder(this).setTitle("权限获取失败").setMessage("抱歉,Story酱真的需要内部存储读取权限才能好好干活喵~\n请到设置中手动开启内部存储读写权限.").setPositiveButton("确定", (dialog, which) -> finish()).show();
                    }
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void loadApp(){
        new Handler().postDelayed(() -> {
            //注册错题通知
            if(AppSettingsMaster.getIfShowAlarm(this)){
                AlarmReceiver.setupAlarm(this,false);
            }
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}
