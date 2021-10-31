package cn.edu.bistu.cs.mydiary.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import cn.edu.bistu.cs.mydiary.R;

/**
 * @author hp
 */
public class AuthorInfoActivity extends AppCompatActivity {
    private String aName;
    private String sexual;
    private String dTitle;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private EditText authorName;
    private RadioGroup sex;
    private EditText diaryDefaultTitle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("个人信息");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_info);

        init();
        showAuthorInfo();
    }

    /**
     * 初始化控件
     */
    public void init(){
        sharedPreferences = getSharedPreferences("author_data", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        authorName = findViewById(R.id.author_name);
        sex = findViewById(R.id.sex_radio);
        diaryDefaultTitle = findViewById(R.id.diary_default_title);
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveAuthorInfo();
    }

    /**
     * 显示当前个人信息
     * 从SharedPreference中读取
     */
    private void showAuthorInfo(){
        RadioButton maleRadio = sex.findViewById(R.id.male);
//        RadioButton femaleRadio = sex.findViewById(R.id.female);
        aName = sharedPreferences.getString("author_name", "佚名");
        sexual = sharedPreferences.getString("sex", "男");
        dTitle = sharedPreferences.getString("default_title", "无标题日记");

        authorName.setText(aName);
        maleRadio.setChecked(true);
        diaryDefaultTitle.setText(dTitle);


    }

    /**
     * 保存个人信息
     * 将当前输入的信息保存到SharedPreference中
     */
    private void saveAuthorInfo(){
        aName = authorName.getText().toString();
        sexual = ((RadioButton)findViewById(sex.getCheckedRadioButtonId())).getText().toString();
        // Log.e(AuthorInfoActivity.ACTIVITY_SERVICE, sexual);
        dTitle = diaryDefaultTitle.getText().toString();

        editor.putString("author_name", aName);
        editor.putString("sex", sexual);
        editor.putString("default_title", dTitle);
        editor.apply();
    }


}