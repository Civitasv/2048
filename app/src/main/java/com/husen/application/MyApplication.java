package com.husen.application;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.husen.greendao.DaoMaster;
import com.husen.greendao.DaoSession;
import com.husen.utils.MyOpenHelper;

public class MyApplication extends Application {
    public DaoSession daoSession;
    public SQLiteDatabase db;
    public MyOpenHelper helper;
    public DaoMaster daoMaster;

    @Override
    public void onCreate() {
        super.onCreate();
        setupDatabase();
    }

    private void setupDatabase() {
        helper = new MyOpenHelper(this, "user3.db", null);
        db = helper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}
