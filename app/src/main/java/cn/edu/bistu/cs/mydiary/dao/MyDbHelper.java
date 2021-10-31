package cn.edu.bistu.cs.mydiary.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import androidx.annotation.Nullable;
import cn.edu.bistu.cs.mydiary.idao.IDbActions;
import cn.edu.bistu.cs.mydiary.model.Diary;

/**
 * @author hp
 */
public class MyDbHelper extends SQLiteOpenHelper implements IDbActions {
    public static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String CREATE_TIME = "create_time";
    public static final String AUTHOR = "author";
    public static final String CONTENT = "content";
    public static final String PHOTO_PATH = "photo_path";


    private final Context context;
    private SQLiteDatabase db;
    private ContentValues contentValues;

    static String CREATE_TABLE = "create table diary(" +
            "_id integer primary key autoincrement," +
            TITLE + " text," +
            CONTENT + " text," +
            CREATE_TIME + " integer," +
            AUTHOR + " text," +
            PHOTO_PATH + " text" +
            ");";
    static String DROP_TABLE = "drop table if exists diary";


    public MyDbHelper(@Nullable Context context, @Nullable String name,
                      @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DROP_TABLE);
        sqLiteDatabase.execSQL(CREATE_TABLE);
        Toast.makeText(context,"创建数据库成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Toast.makeText(context, "更新数据库成功",Toast.LENGTH_SHORT).show();
    }

    /**
     * 删除所有日记
     */
    @Override
    public void deleteAllDiary() {
        db = getWritableDatabase();
        db.execSQL(DROP_TABLE);
        db.execSQL(CREATE_TABLE);
    }

    /**
     * 删除指定的日记
     * @param id 要删除的日记号
     */
    @Override
    public void deleteDiary(int id) {
        db = getWritableDatabase();
        db.delete("diary", "_id=?", new String[]{String.valueOf(id)});
    }

    /**
     * 创建日记
     * @param title 新建日记时的标题
     * @param author 新建日记时的作者
     */
    @Override
    public void createDiary(String title, String author) {
        db = getWritableDatabase();
        contentValues = new ContentValues();

        contentValues.put(TITLE, title);
        contentValues.put(CONTENT, "");
        contentValues.put(CREATE_TIME, new java.util.Date().getTime());
        contentValues.put(AUTHOR, author);

        db.insert("diary", null, contentValues);
        contentValues.clear();
    }

    /**
     * 更新日记
     * @param diary 修改后的日记对象
     */
    @Override
    public void updateDiary(Diary diary) {
        db = getWritableDatabase();
        contentValues = new ContentValues();

        if(diary.getTitle() != null){
            contentValues.put(TITLE, diary.getTitle());
        }
        if(diary.getContent() != null){
            contentValues.put(CONTENT, diary.getContent());
        }
        if(diary.getPhotoPath() != null){
            contentValues.put(PHOTO_PATH, diary.getPhotoPath());
        }

        db.update("diary", contentValues, "_id=?",
                new String[]{String.valueOf(diary.getId())});
        contentValues.clear();

    }

    /**
     * 获取数据库的光标
     * @return 数据库的光标
     */
    @Override
    public Cursor getDiaryListCursor() {
        db = getWritableDatabase();
        return db.query("diary", null, null,
                null, null, null, null, null);
    }
}
