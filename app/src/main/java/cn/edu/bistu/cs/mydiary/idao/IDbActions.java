package cn.edu.bistu.cs.mydiary.idao;

import android.database.Cursor;
import cn.edu.bistu.cs.mydiary.model.Diary;

/**
 * @author hp
 */
public interface IDbActions {
    /**
     * 删除所有日记
     */
    void deleteAllDiary();

    /**
     * 删除指定的日记
     * @param id 要删除的日记号
     */
    void deleteDiary(int id);

    /**
     * 创建日记
     * @param title 新建日记时的标题
     * @param author 新建日记时的作者
     */
    void createDiary(String title, String author);

    /**
     * 更新日记
     * @param diary 修改后的日记对象
     */
    void updateDiary(Diary diary);

    /**
     *  获取数据库的光标
     * @return 数据库的光标
     */
    Cursor getDiaryListCursor();




}
