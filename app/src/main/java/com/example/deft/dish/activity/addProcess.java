package com.example.deft.dish.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.example.deft.dish.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dell on 2016/7/10.
 */
public class AddProcess extends AppCompatActivity {
    private ImageButton ib_photo;
    private Button bt_sure;
    private Button bt_back;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private File tempFile = new File(Environment.getExternalStorageDirectory(),getPhotoFileName());
    private Intent passIntent;
    private EditText et;
    private Bitmap tempPhoto=null;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        passIntent=this.getIntent();
        setContentView(R.layout.activity_addprocess);
        ib_photo=(ImageButton)findViewById(R.id.imageButton);
        bt_sure=(Button)findViewById(R.id.button2);
        ib_photo.setOnLongClickListener(new ibpListener());
        bt_sure.setOnClickListener(new btsListener());
        et=(EditText)findViewById(R.id.editText2);
        bt_back=(Button)findViewById(R.id.button);
        bt_back.setOnClickListener(new btbListener());
    }
    class btbListener implements View.OnClickListener {
        @Override
        public void onClick(View v)
        {
            AddProcess.this.setResult(Activity.RESULT_CANCELED, passIntent);
            AddProcess.this.finish();
        }
    }

    class btsListener implements View.OnClickListener {
        @Override
        public void onClick(View v)
        {
         if(tempPhoto!=null) {
             Bundle bundle = passIntent.getExtras();
             bundle.putParcelable("bitmap",tempPhoto);
             bundle.putString("des", et.getText().toString());
             passIntent.putExtras(bundle);

             AddProcess.this.setResult(Activity.RESULT_OK, passIntent);
             AddProcess.this.finish();
         }
        }
    }

    class ibpListener implements View.OnLongClickListener{
        public boolean onLongClick(View view)
        {
            showDialog();
            return true;
        }
    }

    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("设置图片")
                .setPositiveButton("拍照", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
// TODO Auto-generated method stub
                        dialog.dismiss();
// 调用系统的拍照功能
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
// 指定调用相机拍照后照片的储存路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(tempFile));
                        startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                    }
                })
                .setNegativeButton("相册", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
// TODO Auto-generated method stub
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                "image/*");
                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                    }
                }).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
// TODO Auto-generated method stub
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO://当选择拍照时调用
                startPhotoZoom(Uri.fromFile(tempFile), 150);
                break;

            case PHOTO_REQUEST_GALLERY://当选择从本地获取图片时
//做非空判断，当我们觉得不满意想重新剪裁的时候便不会报异常，下同
                if (data != null)
                    startPhotoZoom(data.getData(), 150);
                break;

            case PHOTO_REQUEST_CUT://返回的结果
                if (data != null)
                    setPicToView(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

        private String getPhotoFileName() {
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
            return dateFormat.format(date) + ".jpg";
        }

        private void startPhotoZoom(Uri uri, int size) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
// crop为true是设置在开启的intent中设置显示的view可以剪裁
            intent.putExtra("crop", "true");

// aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);

// outputX,outputY 是剪裁图片的宽高
            intent.putExtra("outputX", size);
            intent.putExtra("outputY", size);
            intent.putExtra("return-data", true);

            startActivityForResult(intent, PHOTO_REQUEST_CUT);
        }
     //将进行剪裁后的图片显示到UI界面上
        @SuppressWarnings("deprecation")
        private void setPicToView(Intent picdata) {
            Bundle bundle = picdata.getExtras();
            if (bundle != null) {
                Bitmap photo = bundle.getParcelable("data");
                Drawable drawable = new BitmapDrawable(photo);
                ib_photo.setBackgroundDrawable(drawable);
                tempPhoto=photo;
            }
        }
}
