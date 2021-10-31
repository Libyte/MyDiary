package cn.edu.bistu.cs.mydiary.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import cn.edu.bistu.cs.mydiary.R;
import cn.edu.bistu.cs.mydiary.dao.MyDbHelper;

/**
 * @author hp
 */
@SuppressLint("Range")
public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private ListView showDiary;
    private MyDbHelper db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("My Diary");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new MyDbHelper(this, "test.db", null, 1);
        db.getWritableDatabase();
        

        Button createDiary = findViewById(R.id.create_diary);

        createDiary.setOnClickListener(view -> {
            sharedPreferences = getSharedPreferences("author_data", MODE_PRIVATE);
            String title = sharedPreferences.getString("default_title", "无标题日记");
            String authorName = sharedPreferences.getString("author_name", "佚名");
            db.createDiary(title, authorName);
            Intent intent = new Intent(MainActivity.this, DiaryActivity.class);
            intent.putExtra("new_diary", 1);
            startActivity(intent);
        });

        showDiary = findViewById(R.id.show_diary);

        showDiary.setOnItemClickListener((adapterView, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, DiaryActivity.class);
            intent.putExtra("diary_position", position);
            startActivity(intent);
        });

        showDiary.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder deleteDialog = new AlertDialog.Builder(MainActivity.this);
            deleteDialog.setTitle("确定删除这篇日记？");

            deleteDialog.setPositiveButton("确定", (dialog, which) -> {
                Cursor cursor = db.getDiaryListCursor();
                cursor.moveToPosition(position);
                int id1 = cursor.getInt(cursor.getColumnIndex(MyDbHelper.ID));
                db.deleteDiary(id1);
                Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                refresh();
            });

            deleteDialog.setNegativeButton("取消", null);
            deleteDialog.create().show();
            refresh();
            return true;
        });
    }

    @Override
    protected void onResume() {
        refresh();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "编辑个人信息");
        menu.add(0, 1, 0, "删除全部日记");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case 0:
                Intent intent = new Intent(MainActivity.this, AuthorInfoActivity.class);
                startActivity(intent);
                break;
            case 1:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("确定删除所有日记？");
                builder.setPositiveButton("确定", (dialog, which) -> {
                    db.deleteAllDiary();
                    refresh();
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 刷新显示
     * 进入界面时加载
     * 每次有关日记的操作后都重新加载
     */
    public void refresh(){
        Cursor c = db.getDiaryListCursor();
        String[] from = new String[]{MyDbHelper.TITLE,
                MyDbHelper.AUTHOR, MyDbHelper.CREATE_TIME, MyDbHelper.CONTENT};
        int[] to = new int[]{R.id.diary_item_title,
                R.id.diary_item_author, R.id.diary_item_create_time, R.id.diary_item_content};

        SimpleCursorAdapter.ViewBinder viewBinder = (view, cursor, columnIndex) -> {
            TextView textView = (TextView) view;
            if(cursor.getColumnIndex(MyDbHelper.TITLE) == columnIndex){
                String title = cursor.getString(columnIndex);
                textView.setText(title);
                textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                return true;
            }
            if(cursor.getColumnIndex(MyDbHelper.AUTHOR) == columnIndex){
                String author = cursor.getString(columnIndex);
                textView.setText(author);

                return true;
            }
            if(cursor.getColumnIndex(MyDbHelper.CREATE_TIME) == columnIndex){
                long createTime = cursor.getLong(columnIndex);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                String strTime = simpleDateFormat.format(new Date(createTime));
                textView.setText(strTime);

                return true;
            }
            if(cursor.getColumnIndex(MyDbHelper.CONTENT) == columnIndex){
                String content = cursor.getString(columnIndex);
                textView.setText(content);

                return true;
            }

            return false;
        };

        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.diary_list_item, c, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        simpleCursorAdapter.setViewBinder(viewBinder);
        showDiary = findViewById(R.id.show_diary);
        showDiary.setAdapter(simpleCursorAdapter);
    }


}