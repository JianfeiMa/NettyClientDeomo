package com.buyuphk.nettyclientdeomo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog progressDialog;
    private EditText editTextUserId;
    private EditText editTextUserName;
    private Button button;
    private boolean result = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("登录");
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在加载中");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        editTextUserId = findViewById(R.id.activity_login_user_id_value);
        editTextUserName = findViewById(R.id.activity_login_user_name_value);
        button = findViewById(R.id.activity_login_button_login);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String userId = editTextUserId.getText().toString();
        String userName = editTextUserName.getText().toString();
        if (!userId.equals("") && !userName.equals("")) {
            Toast.makeText(this, "正在与服务器建立连接...", Toast.LENGTH_SHORT).show();
            MyAsyncTask myAsyncTask = new MyAsyncTask(userId, userName, this);
            myAsyncTask.execute();
        } else {
            Toast.makeText(this, "用户ID或者用户名为空", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (result) {
            Intent intent = new Intent();
            intent.putExtra("userId", editTextUserId.getText().toString());
            intent.putExtra("userName", editTextUserName.getText().toString());
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    public static class MyAsyncTask extends AsyncTask<String, Integer, Boolean> {
        private String userId;
        private String userName;
        private WeakReference<LoginActivity> contextWeakReference;

        public MyAsyncTask(String userId, String userName, LoginActivity context) {
            this.userId = userId;
            this.userName = userName;
            contextWeakReference = new WeakReference<LoginActivity>(context);
        }

        @Override
        protected Boolean doInBackground(String... objects) {
            AbstractClient abstractClient = new CIMClientImpl(contextWeakReference.get(), Long.valueOf(userId), userName);
            boolean result = abstractClient.start();
            if (result) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(contextWeakReference.get());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userId", userId);
                editor.putString("userName", userName);
                editor.apply();
            }
            contextWeakReference.get().result = result;
            return result;
        }
    }
}
