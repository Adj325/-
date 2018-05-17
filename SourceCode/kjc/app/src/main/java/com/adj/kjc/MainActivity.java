package com.adj.kjc;

import java.io.File;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.os.Bundle;
import android.widget.Toast;
import android.app.Activity;
import android.widget.Switch;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.widget.TextView;
import android.widget.ImageView;
import android.provider.MediaStore;
import android.widget.CompoundButton;
import android.graphics.BitmapFactory;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import static android.Manifest.permission;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{
    // REQUEST_CODE
    private static final int ACTIVITY_CODE_IMAGE = 1;
    private static final int PERMISSION_CODE_READ = 0X325;
    private static final int PERMISSION_CODE_WRITE = 0X326;

    // 创建目录
    static {
        File fileDir = new File(Data.getOperateDataPath());
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
    }

    // 显示toast
    public void toast(String meg, int time){
        Toast.makeText(this, meg, time).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{

            super.onCreate(savedInstanceState);

            // 设置布局
            setContentView(R.layout.activity_main);

            // 设置开关
            setSwitch();

            // 申请权限
            getPermission();

            // 加载已设置的照片
            File file = new File(Data.getReplaceImgPath());
            if(file.exists())
                showImage();
            else
                toast("你还没有设置任何照片呢!", 0);

            toast("有些人, 是不能被禁锢的!", 0);
            toast("有些事, 这辈子都不可能做的!", 1);
        }catch (Exception e){
            Tool.kjcLog(e.toString());
        }
    }

    // 读取配置信息, 设置开关状态
    public void setSwitch(){
        // 读取配置信息, 设置开关状态
        Data.updateStatus();

        // hook状态
        final Switch hookSwitch = findViewById(R.id.HookSwitch);
        hookSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            TextView _TextView = findViewById(R.id.HookTextView);
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(Data.setHook(isChecked)){
                    if(isChecked)
                        _TextView.setBackgroundColor(android.graphics.Color.parseColor("#5500ff00"));
                    else
                        _TextView.setBackgroundColor(android.graphics.Color.parseColor("#CCCCCC"));
                } else {
                    toast("无法更新配置!", 0);
                }
            }
        });
        hookSwitch.setChecked(Data.isHook());


        // toast状态
            final Switch toastSwitch = findViewById(R.id.ToastSwitch);
        toastSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            TextView _TextView = findViewById(R.id.ToastTextView);
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(Data.setToast(isChecked)){
                    if(isChecked)
                        _TextView.setBackgroundColor(android.graphics.Color.parseColor("#5500ff00"));
                    else
                        _TextView.setBackgroundColor(android.graphics.Color.parseColor("#CCCCCC"));
                } else {
                    toast("无法更新配置!", 0);
                }
            }
        });
        toastSwitch.setChecked(Data.isToast());

        // XposedLog状态
        Switch xposedLogSwitch = findViewById(R.id.XposedLogSwitch);
        xposedLogSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            TextView _TextView = findViewById(R.id.XposedLogTextView);
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(Data.setLog(isChecked)){
                    if(isChecked)
                        _TextView.setBackgroundColor(android.graphics.Color.parseColor("#5500ff00"));
                    else
                        _TextView.setBackgroundColor(android.graphics.Color.parseColor("#CCCCCC"));
                } else {
                    toast("无法更新配置!", 0);
                }
            }
        });
        xposedLogSwitch.setChecked(Data.isXposedLog());

        // KjcLog状态
        Switch kjcLogSwitch = findViewById(R.id.KjcLogSwitch);
        kjcLogSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            TextView _TextView = findViewById(R.id.KjcLogTextView);
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(Data.setKjcLog(isChecked)){
                    if(isChecked)
                        _TextView.setBackgroundColor(android.graphics.Color.parseColor("#5500ff00"));
                    else
                        _TextView.setBackgroundColor(android.graphics.Color.parseColor("#CCCCCC"));
                } else {
                    toast("无法更新配置!", 0);
                }
            }
        });
        kjcLogSwitch.setChecked(Data.isKjcLog());

    }

    // 申请权限
    public void getPermission(){
        // 申请对STORAGE的读取及写入权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 读取权限
            // 权限有三种状态（1、允许  2、提示  3、禁止）
            int permissionStatus = ActivityCompat.checkSelfPermission(getApplication(), permission.READ_EXTERNAL_STORAGE);
            // 2、提示  3、禁止
            // 未申请, 提示申请
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                // 权限是禁止, 返回false
                // 权限是提示, 返回true
                boolean isForbidden = ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        permission.READ_EXTERNAL_STORAGE);
                // 针对提示， 申请权限
                if (isForbidden) {
                    toast("申请READ_EXTERNAL_STORAGE权限", 0);
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{permission.READ_EXTERNAL_STORAGE},PERMISSION_CODE_READ);
                }
            }

            // 读取权限
            // 权限有三种状态（1、允许  2、提示  3、禁止）
            permissionStatus = ActivityCompat.checkSelfPermission(getApplication(), permission.WRITE_EXTERNAL_STORAGE);
            // 2、提示  3、禁止
            // 未申请, 提示申请
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                // 权限是禁止, 返回false
                // 权限是提示, 返回true
                boolean isForbidden = ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        permission.WRITE_EXTERNAL_STORAGE);
                // 针对提示， 申请权限
                if (isForbidden) {
                    toast("申请WRITE_EXTERNAL_STORAGE权限", 0);
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{permission.WRITE_EXTERNAL_STORAGE},PERMISSION_CODE_WRITE);
                }
            }
        }
    }

    // 请求权限回调 - 程序请求申请权限后, 会调用此函数, 根据requestCode的不同, 作出不同的处理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE_READ:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    toast("申请读取权限失败!", 0);
                break;
            case PERMISSION_CODE_WRITE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    toast("申请写入权限失败!", 0);
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // 点击调用相册事件
    public void onClick(View v) {
        try{
            // 调用相册
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // 打开相册
            startActivityForResult(intent, ACTIVITY_CODE_IMAGE);
        }catch (Exception e) {
            toast("无法调用相册!", 0);
        }
    }

    // 活动启动回调 - 程序请求打开某活动后, 会调用此函数, 根据requestCode的不同, 作出不同的处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 获取图片路径
        if (requestCode == ACTIVITY_CODE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);

            if(imagePath.contains("replace.jpg")) {
                toast("文件路径不能含有 replace.jpg ", 0);
            } else {
                // 复制照片,并打印结果
                try{
                    Tool.copyFile(imagePath, Data.getScaleTempImgPath());
                    // 切割照片
                    Tool.scalePicture(Data.getScaleTempImgPath(), Data.getReplaceImgPath(), 450, 600);
                    // 改变图片更新时间
                    Data.setImgUpdatedTime(Tool.getTime());
                }catch (Exception e){
                    toast("图片复制失败!\r\n"+e.toString(), 0);
                }
            }
            // 更新照片
            showImage();
            c.close();
        } else {
            toast("你此次没有选取照片呢!", 0);
        }
    }

    // 加载图片
    private void showImage(){
        Bitmap bm = BitmapFactory.decodeFile(Data.getReplaceImgPath());
        ((ImageView)findViewById(R.id.image)).setImageBitmap(bm);
    }
}