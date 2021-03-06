package cn.edu.bistu.cs.mydiary.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import cn.edu.bistu.cs.mydiary.R;
import cn.edu.bistu.cs.mydiary.dao.MyDbHelper;
import cn.edu.bistu.cs.mydiary.model.Diary;

/**
 * @author hp
 */
public class DiaryActivity extends AppCompatActivity {
    private final static int RESULT_FOR_TAKING_PHOTO = 1;
    private final static int RESULT_FOR_SELECTING_PHOTO = 2;


    private int _id;
    private Intent intent;
    private MyDbHelper db;
    private EditText diaryContent;
    private ImageView diaryImage;

    private String title;
    private String picturePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        intent = getIntent();
        db = new MyDbHelper(this, "test.db", null, 1);
        diaryContent = findViewById(R.id.diary_content);
        diaryImage = findViewById(R.id.diary_picture);

    }

    @Override
    protected void onResume() {
        refresh();
        super.onResume();
    }

    @Override
    protected void onPause() {
        updateDiary();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "????????????");
        menu.add(0, 1, 0, "????????????");
        menu.add(0, 2, 0, "??????");
        menu.add(0, 3, 0, "??????????????????");
        menu.add(0, 4, 0, "????????????");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case 0:
                EditText editText = new EditText(this);
                editText.setText(getTitle());
                editText.setTextSize(20);
                editText.setMaxWidth(40);
                editText.setSingleLine(true);
                AlertDialog.Builder editAlertDialog = new AlertDialog.Builder(this);
                editAlertDialog.setTitle("??????????????????").setView(editText);
                editAlertDialog.setPositiveButton("??????", (dialog, which) -> {
                    this.title = editText.getText().toString();
                    updateDiary();
                    refresh();
                    Toast.makeText(DiaryActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                });
                editAlertDialog.show();
                break;
            case 1:
                db.deleteDiary(_id);
                Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case 2:
                takePicture();
                break;
            case 3:
                selectPicture();
                break;
            case 4:
                picturePath = "";
                updateDiary();
                showImage();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, RESULT_FOR_SELECTING_PHOTO);
        }
    }

    /**
     * ????????????????????????????????????
     * ??????????????????????????????????????????????????????
     */
    @SuppressLint("Range")
    public void refresh(){
        Cursor cursor = db.getDiaryListCursor();
        if(intent.getIntExtra("new_diary", 0) == 1){
            cursor.moveToLast();
        }
        else{
            cursor.moveToPosition(intent.getIntExtra("diary_position", 0));
        }

        TextView authorText = findViewById(R.id.diary_author);
        TextView createTimeText = findViewById(R.id.diary_create_time);
        _id = cursor.getInt(cursor.getColumnIndex(MyDbHelper.ID));
        title = cursor.getString(cursor.getColumnIndex(MyDbHelper.TITLE));
        String content = cursor.getString(cursor.getColumnIndex(MyDbHelper.CONTENT));
        picturePath = cursor.getString(cursor.getColumnIndex(MyDbHelper.PHOTO_PATH));
        String author = cursor.getString(cursor.getColumnIndex(MyDbHelper.AUTHOR));
        long createTime = cursor.getLong(cursor.getColumnIndex(MyDbHelper.CREATE_TIME));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);


        setTitle(title);
        Log.e(String.valueOf(DiaryActivity.this), author);
        authorText.setText(author);
        createTimeText.setText(simpleDateFormat.format(createTime));
        diaryContent.setText(content);
        showImage();


    }

    /**
     * ??????????????????????????????????????????
     */
    public void updateDiary(){
        Diary diary = new Diary();
        diary.setId(_id);
        diary.setTitle(title);
        diary.setContent(diaryContent.getText().toString());
        diary.setPhotoPath(picturePath);

        db.updateDiary(diary);

    }

    /**
     * ????????????
     * ???imageView???????????????
     */
    private void showImage() {
        if(picturePath != null && !"".equals(picturePath)){
            Log.e(String.valueOf(DiaryActivity.this), picturePath);
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            diaryImage.setImageBitmap(bitmap);
        }
        else{
            diaryImage.setImageBitmap(null);
        }
    }

    /**
     * ????????????
     * ?????????????????????????????????
     * ??????Intent????????????
     * ?????????????????????
     */
    private void selectPicture() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, RESULT_FOR_SELECTING_PHOTO);
        }
    }

    /**
     * ????????????
     * ??????File????????????
     * ?????????????????????Uri???
     * ??????Intent???????????????????????????
     * ?????????????????????
     */
    private void takePicture() {
        File picture = new File(getExternalFilesDir(null), _id+"_image.jpg");
        try{
            if(picture.exists()){
                picture.delete();
            }
            picture.createNewFile();
        }catch (Exception e){
            e.printStackTrace();
        }
        picturePath = picture.getPath();
        Uri uri = FileProvider.getUriForFile(this,
                "cn.edu.bistu.cs.mydiary.fileProvider", picture);

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, RESULT_FOR_TAKING_PHOTO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case RESULT_FOR_TAKING_PHOTO:
                if(resultCode == RESULT_OK){
                    updateDiary();
                    showImage();
                }
                break;
            case RESULT_FOR_SELECTING_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        assert data != null;
                        picturePath = getPicturePath(data);
                       //Log.e(String.valueOf(DiaryActivity.this), picturePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateDiary();
                    showImage();
                }

                break;
            default:
                break;
        }

    }

    /**
     * ??????????????????????????????????????????????????????
     * ????????????????????????????????????
     * @param data onActivityResult?????????Intent
     * @return ???????????????????????????
     * @throws IOException ??????????????????????????????
     */
    @SuppressLint("Range")
    private String getPicturePath(Intent data) throws IOException {
        String temp = null;
        Uri uri = data.getData();
        if("content".equalsIgnoreCase(uri.getScheme())){
            Cursor cursor = getContentResolver().query(uri,
                    null,null,null,null);
            if(cursor != null){
                if(cursor.moveToFirst()){
                    temp = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                }
                cursor.close();
            }
        }
        else{
            temp = uri.getPath();
        }
        //Log.e(String.valueOf(DiaryActivity.this), picturePath);
        Bitmap tempPicture = BitmapFactory.decodeFile(temp);
        File file = new File(getExternalFilesDir(null), _id+"_image.jpg");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        if(!tempPicture.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)){
            Toast.makeText(this, "??????????????????!", Toast.LENGTH_SHORT).show();
        }

        fileOutputStream.close();
        return file.getPath();
    }
}