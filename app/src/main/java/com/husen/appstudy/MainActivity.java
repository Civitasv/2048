package com.husen.appstudy;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.husen.application.MyApplication;
import com.husen.entity.User;
import com.husen.greendao.UserDao;
import com.husen.utils.MyUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder builder;
    public static TextView score;
    public static TextView maxScore;
    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置屏幕不可横屏
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        // 找到按钮控件
        ImageButton getInfo = findViewById(R.id.getInfo);
        View.OnClickListener btnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        };
        user = findUser();

        getInfo.setOnClickListener(btnClickListener);
        score = findViewById(R.id.score);
        maxScore = findViewById(R.id.maxScore);
        if (user != null) {
            maxScore.setText("最高分" + "\n" + user.getMaxScore() + "");
            score.setText("分数" + "\n" + user.getScore() + "");
            if(user.getArrayStr()!=null&&!"".equals(user.getArrayStr())){
                MyTestView.array = MyUtil.stringToArray(user.getArrayStr());
            }
        }
        restart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 重新开始事件
     */
    private void restart() {
        Button restart = findViewById(R.id.restart);
        View.OnClickListener btnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                // dialog
                builder = new AlertDialog.Builder(MainActivity.this)/*setIcon(R.mipmap.ic_launcher)*/.setTitle("重新开始")
                        .setMessage("是否重新开始？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Toast.makeText(UserInfoActivity.this, "确定按钮", Toast.LENGTH_LONG).show();
                                MyTestView.init(MyTestView.array);
                                if (user != null && ChessUtil.score > user.getMaxScore()) {
                                    ChessUtil.maxScore = ChessUtil.score;
                                    user.setMaxScore(ChessUtil.maxScore);
                                    getUserDao().update(user);
                                }
                                if (user != null)
                                    maxScore.setText("最高分" + "\n" + user.getMaxScore() + "");
                                else
                                    maxScore.setText("最高分" + "\n" + ChessUtil.maxScore + "");
                                ChessUtil.score = 0;
                                score.setText("分数" + "\n" + 0);
                            }
                        }).setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Toast.makeText(UserInfoActivity.this, "关闭按钮", Toast.LENGTH_LONG).show();
                                dialogInterface.dismiss();
                            }
                        });
                builder.create().show();

            }
        };
        restart.setOnClickListener(btnClickListener);

    }

    private User findUser() {
        List<User> user = getUserDao().loadAll();
        // 返回第一个
        if (user != null && user.size() > 0)
            return user.get(user.size() - 1);
        return null;
    }

    private UserDao getUserDao() {
        // 通过 BaseApplication 类提供的 getDaoSession() 获取具体 Dao
        return ((MyApplication) this.getApplication()).getDaoSession().getUserDao();
    }

    private SQLiteDatabase getDb() {
        // 通过 BaseApplication 类提供的 getDb() 获取具体 db
        return ((MyApplication) this.getApplication()).getDb();
    }

    @Override
    protected void onDestroy() {
        String arr = MyUtil.arrayToString(MyTestView.array);
        if(user!=null) {
            user.setScore(ChessUtil.score);
            user.setArrayStr(arr);
            getUserDao().update(user);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
