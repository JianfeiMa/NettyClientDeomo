package com.buyuphk.nettyclientdeomo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.buyuphk.nettyclientdeomo.service.MsgHandle;
import com.buyuphk.nettyclientdeomo.service.RouteRequest;
import com.buyuphk.nettyclientdeomo.service.impl.MsgHandler;
import com.buyuphk.nettyclientdeomo.service.impl.RouteRequestImpl;
import com.buyuphk.nettyclientdeomo.vo.res.OfflineUserResVO;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etUserId;
    private EditText etUserName;
    private EditText etMessage;
    private EditText etWillMessage;
    private TextView tvMessage;
    private TextView tvAlive;
    private MyBroadcastReceiver myBroadcastReceiver;
    private AliveBroadcastReceiver aliveBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etUserId = findViewById(R.id.activity_main_et_user_id);
        etUserName = findViewById(R.id.activity_main_et_user_name);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        etUserId.setText(sharedPreferences.getString("userId", ""));
        etUserName.setText(sharedPreferences.getString("userName", ""));
        etMessage = findViewById(R.id.activity_main_et_msg);
        etWillMessage = findViewById(R.id.activity_main_et_will_message);
        tvMessage = findViewById(R.id.activity_main_tv_msg);
        tvAlive = findViewById(R.id.activity_main_tv_alive);
        Button buttonLauncher = findViewById(R.id.activity_main_button_launcher);
        Button buttonSendMessage = findViewById(R.id.activity_main_button_send_message);
        buttonLauncher.setOnClickListener(this);
        buttonSendMessage.setOnClickListener(this);
        myBroadcastReceiver = new MyBroadcastReceiver();
        aliveBroadcastReceiver = new AliveBroadcastReceiver();
        registerReceiver(myBroadcastReceiver, new IntentFilter("netty_socket"));
        registerReceiver(aliveBroadcastReceiver, new IntentFilter("alive"));
    }

    @Override
    public void onClick(View v) {
        String sUserId = etUserId.getText().toString();
        String sUserName = etUserName.getText().toString();
        if (v.getId() == R.id.activity_main_button_launcher) {
            if (sUserId.equals("")) {
                Toast.makeText(this, "用户ID不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (sUserName.equals("")) {
                Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            MyAsyncTask myAsyncTask = new MyAsyncTask(sUserId, sUserName, this);
            myAsyncTask.execute();
        } else {
            String message = etMessage.getText().toString();
            if (message.equals("")) {
                Toast.makeText(this, "不能输入空的用户ID", Toast.LENGTH_SHORT).show();
                return;
            }
            String sWillMessage = etWillMessage.getText().toString();
            if (sWillMessage.equals("")) {
                Toast.makeText(this, "不能输入空发送信息", Toast.LENGTH_SHORT).show();
                return;
            }
            String s = tvMessage.getText().toString();
            s = s + "\n" + message + sWillMessage + "\n";
            tvMessage.setText(s);
            etWillMessage.setText("");
            MyAsyncTask1 myAsyncTask1 = new MyAsyncTask1(sUserId, sUserName);
            myAsyncTask1.execute(message + sWillMessage);
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
            String userId = etUserId.getText().toString();
            if (userId.equals("")) {
                Toast.makeText(this, "用户ID不能为空", Toast.LENGTH_SHORT).show();
                return false;
            }
            String userName = etUserName.getText().toString();
            if (userName.equals("")) {
                Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return false;
            }
            MyAsyncTask2 myAsyncTask2 = new MyAsyncTask2(this, userId, userName);
            myAsyncTask2.execute();
        } if (item.getItemId() == R.id.menu_main_register) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        } if (item.getItemId() == R.id.menu_main_online_list) {
            Intent intent = new Intent(this, OnlineUserListActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
        unregisterReceiver(aliveBroadcastReceiver);
    }

    private void showOfflineResult(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("msg");
            String s = tvMessage.getText().toString();
            if (msg != null && !msg.equals("")) {
                s = s + "\n" + msg;
                tvMessage.setText(s);
            }
        }
    }

    public class AliveBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String alive = tvAlive.getText().toString();
            if (alive.equals("")) {
                tvAlive.setText("1");
            } else {
                long alive1 = Long.valueOf(alive);
                alive1 ++;
                tvAlive.setText(String.valueOf(alive1));
            }
        }
    }
}
