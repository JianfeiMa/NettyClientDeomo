package com.buyuphk.nettyclientdeomo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Copyright (C), buyuphk物流中转站
 * author: JianfeiMa
 * email: majianfei93@163.com
 * revised: 2019-11-22 15:11
 * motto: 勇于向未知领域探索
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    public static final String CHAT_ROOM_MESSAGE = "chat_room_message";

    public MySQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + CHAT_ROOM_MESSAGE + " (id integer primary key autoincrement, message varchar(300));";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists " + CHAT_ROOM_MESSAGE;
        db.execSQL(sql);
        onCreate(db);
    }
}
