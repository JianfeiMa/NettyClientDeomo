package com.buyuphk.nettyclientdeomo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.buyuphk.nettyclientdeomo.adapter.ChatRoomAdapter;
import com.buyuphk.nettyclientdeomo.db.MySQLiteOpenHelper;
import com.buyuphk.nettyclientdeomo.service.MsgHandle;
import com.buyuphk.nettyclientdeomo.service.impl.MsgHandler;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {
    private EditText etWillMessage;
    private ListView listView;
    private MyBroadcastReceiver myBroadcastReceiver;
    private ChatRoomAdapter chatRoomAdapter;
    private Button buttonSendMessage;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Intent intent = getIntent();
        long lUserId = intent.getLongExtra("userId", 0);
        userId = String.valueOf(lUserId);
        String userName = intent.getStringExtra("userName");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(userName);
        }
        etWillMessage = findViewById(R.id.activity_main_et_will_message);
        listView = findViewById(R.id.activity_main_list_view);
        buttonSendMessage = findViewById(R.id.activity_main_button_send_message);
        buttonSendMessage.setOnClickListener(this);

        listView.setOnItemLongClickListener(this);
        chatRoomAdapter = new ChatRoomAdapter(this, getAllMessage());
        listView.setAdapter(chatRoomAdapter);

        etWillMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    buttonSendMessage.setEnabled(false);
                } else {
                    buttonSendMessage.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        myBroadcastReceiver = new MyBroadcastReceiver();
        registerReceiver(myBroadcastReceiver, new IntentFilter("netty_socket"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }

    private List<String> getAllMessage() {
        MyApplication myApplication = (MyApplication) getApplication();
        MySQLiteOpenHelper mySQLiteOpenHelper = myApplication.getMySQLiteOpenHelper();
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + MySQLiteOpenHelper.CHAT_ROOM_MESSAGE, null);
        List<String> messages = new ArrayList<>();
        while (cursor.moveToNext()) {
            String message = cursor.getString(cursor.getColumnIndex("message"));
            messages.add(message);
        }
        cursor.close();
        return messages;
    }

    private void saveOneMessage(String message) {
        MyApplication myApplication = (MyApplication) getApplication();
        MySQLiteOpenHelper mySQLiteOpenHelper = myApplication.getMySQLiteOpenHelper();
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("message", message);
        sqLiteDatabase.insert(MySQLiteOpenHelper.CHAT_ROOM_MESSAGE, null, contentValues);
    }

    private void deleteOneMessage(String message) {
        MyApplication myApplication = (MyApplication) getApplication();
        MySQLiteOpenHelper mySQLiteOpenHelper = myApplication.getMySQLiteOpenHelper();
        SQLiteDatabase sqLiteDatabase = mySQLiteOpenHelper.getWritableDatabase();
        String[] whereArgs = new String[1];
        whereArgs[0] = message;
        sqLiteDatabase.delete(MySQLiteOpenHelper.CHAT_ROOM_MESSAGE, "message = ?", whereArgs);
        chatRoomAdapter.dataChanged(getAllMessage());
    }

    @Override
    public void onClick(View v) {
        String sWillMessage = etWillMessage.getText().toString();
        if (sWillMessage.equals("")) {
            Toast.makeText(this, "不能输入空发送信息", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sUserId = sharedPreferences.getString("userId", "");
        String sUserName = sharedPreferences.getString("userName", "");
        String sMessage = userId + ";;" + sWillMessage;
        chatRoomAdapter.addNewMessage(sMessage);
        listView.setSelection(chatRoomAdapter.getCount() - 1);
        saveOneMessage(sMessage);
        etWillMessage.setText("");
        MyAsyncTask1 myAsyncTask1 = new MyAsyncTask1(sUserId, sUserName);
        myAsyncTask1.execute(sMessage);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        new AlertDialog.Builder(this)
                .setMessage("你确定要删除这条消息吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ChatRoomAdapter chatRoomAdapter = (ChatRoomAdapter) parent.getAdapter();
                        String message = (String) chatRoomAdapter.getItem(position);
                        deleteOneMessage(message);
                    }
                })
                .create()
                .show();
        return false;
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("msg");
            if (msg != null && !msg.equals("")) {
                chatRoomAdapter.addNewMessage(msg);
                listView.setSelection(chatRoomAdapter.getCount() - 1);
                saveOneMessage(msg);
            }
        }
    }

    public static class MyAsyncTask1 extends AsyncTask<String, Integer, String> {
        private String userId;
        private String userName;

        public MyAsyncTask1(String userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }

        @Override
        protected String doInBackground(String... strings) {
            MsgHandle msgHandle = new MsgHandler(userId, userName);
            msgHandle.sendMsg(strings[0]);
            return null;
        }
    }
}
