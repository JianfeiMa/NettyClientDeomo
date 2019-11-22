package com.buyuphk.nettyclientdeomo;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.buyuphk.nettyclientdeomo.db.MySQLiteOpenHelper;

/**
 * Copyright (C), buyuphk物流中转站
 * author: JianfeiMa
 * email: majianfei93@163.com
 * revised: 2019-11-22 15:13
 * motto: 勇于向未知领域探索
 */
public class MyApplication extends Application {
    private MySQLiteOpenHelper mySQLiteOpenHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        mySQLiteOpenHelper = new MySQLiteOpenHelper(this, "netty_client_demo", null, 1);
    }

    @Override
    public void onTerminate() {
        Log.d(getClass().getSimpleName(), "执行了onTerminate方法");
        super.onTerminate();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isDisconnected", false);
        editor.apply();
    }

    public MySQLiteOpenHelper getMySQLiteOpenHelper() {
        return mySQLiteOpenHelper;
    }

    public void setMySQLiteOpenHelper(MySQLiteOpenHelper mySQLiteOpenHelper) {
        this.mySQLiteOpenHelper = mySQLiteOpenHelper;
    }
}
