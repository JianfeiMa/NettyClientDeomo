package com.buyuphk.nettyclientdeomo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.buyuphk.nettyclientdeomo.adapter.ChatRoomAdapter;
import com.buyuphk.nettyclientdeomo.adapter.OnlineUserListAdapter;
import com.buyuphk.nettyclientdeomo.db.MySQLiteOpenHelper;
import com.buyuphk.nettyclientdeomo.service.MsgHandle;
import com.buyuphk.nettyclientdeomo.service.RouteRequest;
import com.buyuphk.nettyclientdeomo.service.impl.MsgHandler;
import com.buyuphk.nettyclientdeomo.service.impl.RouteRequestImpl;
import com.buyuphk.nettyclientdeomo.vo.res.OfflineUserResVO;
import com.buyuphk.nettyclientdeomo.vo.res.OnlineUsersResVO;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvUserId;
    private TextView tvUserName;
    private TextView tvAlive;
    private ListView listView;
    private MyBroadcastReceiver myBroadcastReceiver;
    private AliveBroadcastReceiver aliveBroadcastReceiver;
    private InactiveBroadcastReceiver inactiveBroadcastReceiver;
    private ProgressDialog progressDialog;
    private OnlineUserListAdapter onlineUserListAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Netty即时通讯");
        }
        tvUserId = findViewById(R.id.activity_main_et_user_id);
        tvUserName = findViewById(R.id.activity_main_et_user_name);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userId = sharedPreferences.getString("userId", "");
        String userName = sharedPreferences.getString("userName", "");
        tvUserId.setText(userId);
        tvUserName.setText(userName);
        listView = findViewById(R.id.activity_main_list_view);
        tvAlive = findViewById(R.id.activity_main_tv_alive);
        Button buttonLauncher = findViewById(R.id.activity_main_button_launcher);
        buttonLauncher.setOnClickListener(this);
        myBroadcastReceiver = new MyBroadcastReceiver();
        aliveBroadcastReceiver = new AliveBroadcastReceiver();
        inactiveBroadcastReceiver = new InactiveBroadcastReceiver();
        registerReceiver(myBroadcastReceiver, new IntentFilter("netty_socket"));
        registerReceiver(aliveBroadcastReceiver, new IntentFilter("alive"));
        registerReceiver(inactiveBroadcastReceiver, new IntentFilter("channelInactive"));

        TextView refresh = findViewById(R.id.activity_main_text_view_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = sharedPreferences.getString("userId", "");
                String userName = sharedPreferences.getString("userName", "");
                getOnlineUser(userId, userName);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OnlineUserListAdapter onlineUserListAdapter = (OnlineUserListAdapter) parent.getAdapter();
                OnlineUsersResVO.DataBodyBean dataBodyBean = (OnlineUsersResVO.DataBodyBean) onlineUserListAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, ChatRoomActivity.class);
                intent.putExtra("userId", dataBodyBean.getUserId());
                intent.putExtra("userName", dataBodyBean.getUserName());
                startActivity(intent);
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在加载中");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        getOnlineUser(userId, userName);
    }

    private void getOnlineUser(String userId, String userName) {
        if (userId != null && !userId.equals("") && userName != null && !userName.equals("")) {
            progressDialog.show();
            MyAsyncTaskOnlineUserList myAsyncTaskOnlineUserList = new MyAsyncTaskOnlineUserList(this);
            myAsyncTaskOnlineUserList.execute(userId, userName);
        } else {
            Toast.makeText(this, "还未注册用户", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        String sUserId = tvUserId.getText().toString();
        String sUserName = tvUserName.getText().toString();
        if (v.getId() == R.id.activity_main_button_launcher) {
            if (sUserId.equals("")) {
                Toast.makeText(this, "用户ID不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (sUserName.equals("")) {
                Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "正在与服务器建立连接...", Toast.LENGTH_SHORT).show();
            MyAsyncTask myAsyncTask = new MyAsyncTask(sUserId, sUserName, this);
            myAsyncTask.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_main_offline) {
            String userId = tvUserId.getText().toString();
            if (userId.equals("")) {
                Toast.makeText(this, "用户ID不能为空", Toast.LENGTH_SHORT).show();
                return false;
            }
            String userName = tvUserName.getText().toString();
            if (userName.equals("")) {
                Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return false;
            }
            MyAsyncTask2 myAsyncTask2 = new MyAsyncTask2(this, userId, userName);
            myAsyncTask2.execute();
        } if (item.getItemId() == R.id.menu_main_register) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivityForResult(intent, 88);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
        unregisterReceiver(aliveBroadcastReceiver);
        unregisterReceiver(inactiveBroadcastReceiver);
    }

    private void showOfflineResult(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.contains("userId")) {
            editor.remove("userId");
        }
        if (sharedPreferences.contains("userName")) {
            editor.remove("userName");
        }
        editor.apply();
        tvUserId.setText("");
        tvUserName.setText("");
    }

    private void createNotification(String message) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Notification notification = builder
                .setTicker("你有新的消息")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("")
                .setContentText(message)
                .setContentInfo("")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String userId = data.getStringExtra("userId");
            String userName = data.getStringExtra("userName");
            tvUserId.setText(userId);
            tvUserName.setText(userName);
            Toast.makeText(this, "注册成功，点击一键启动向服务器发起连接", Toast.LENGTH_SHORT).show();
        }
    }

    public static class MyAsyncTask extends AsyncTask<String, Integer, String> {
        private String userId;
        private String userName;
        private WeakReference<Context> contextWeakReference;

        public MyAsyncTask(String userId, String userName, Context context) {
            this.userId = userId;
            this.userName = userName;
            contextWeakReference = new WeakReference<Context>(context);
        }

        @Override
        protected String doInBackground(String... objects) {
            AbstractClient abstractClient = new CIMClientImpl(contextWeakReference.get(), Long.valueOf(userId), userName);
            abstractClient.start();
            return null;
        }
    }

    /**
     * 客服端下线网络执行线程
     */
    public static class MyAsyncTask2 extends AsyncTask<String, Integer, String> {
        private WeakReference<MainActivity> mainActivityWeakReference;
        private String userId;
        private String userName;

        public MyAsyncTask2(MainActivity mainActivity, String userId, String userName) {
            mainActivityWeakReference = new WeakReference<MainActivity>(mainActivity);
            this.userId = userId;
            this.userName = userName;
        }

        @Override
        protected String doInBackground(String... strings) {
            RouteRequest routeRequest = new RouteRequestImpl(Long.valueOf(userId), userName);
            OfflineUserResVO offlineUserResVO = routeRequest.offLine();
            if (offlineUserResVO != null) {
                return offlineUserResVO.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                mainActivityWeakReference.get().showOfflineResult(s);
            }
        }
    }

    public static class MyAsyncTaskOnlineUserList extends AsyncTask<String, Integer, List<OnlineUsersResVO.DataBodyBean>> {
        private WeakReference<MainActivity> onlineUserListActivityWeakReference;

        public MyAsyncTaskOnlineUserList(MainActivity onlineUserListActivity) {
            onlineUserListActivityWeakReference = new WeakReference<MainActivity>(onlineUserListActivity);
        }

        @Override
        protected List<OnlineUsersResVO.DataBodyBean> doInBackground(String... strings) {
            RouteRequest routeRequest = new RouteRequestImpl(Long.valueOf(strings[0]), strings[1]);
            List<OnlineUsersResVO.DataBodyBean> onlineUserList = null;
            try {
                onlineUserList = routeRequest.onlineUsers();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return onlineUserList;
        }

        @Override
        protected void onPostExecute(List<OnlineUsersResVO.DataBodyBean> onlineUserList) {
            super.onPostExecute(onlineUserList);
            onlineUserListActivityWeakReference.get().progressDialog.dismiss();
            onlineUserListActivityWeakReference.get().setData(onlineUserList);
        }
    }

    public void setData(List<OnlineUsersResVO.DataBodyBean> data) {
        if (data != null) {
            OnlineUserListAdapter onlineUserListAdapter = new OnlineUserListAdapter(this, data);
            listView.setAdapter(onlineUserListAdapter);
        } else {
            Toast.makeText(this, "没有在线用户", Toast.LENGTH_SHORT).show();
        }
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("msg");
            if (msg != null && !msg.equals("")) {
                createNotification(msg);
            }
        }
    }

    public class AliveBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String status = "在线...";
            tvAlive.setText(status);
            tvAlive.setTextColor(Color.BLUE);
        }
    }

    public class InactiveBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String status = "离线";
            tvAlive.setText(status);
            tvAlive.setTextColor(Color.RED);
        }
    }
}
