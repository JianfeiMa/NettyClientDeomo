package com.buyuphk.nettyclientdeomo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.buyuphk.nettyclientdeomo.vo.res.RegisterUserResVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextNickName;
    private TextView tvUserId;
    private TextView tvUserName;
    private MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextNickName = findViewById(R.id.activity_register_et_nick_name);
        tvUserId = findViewById(R.id.activity_register_tv_user_id);
        tvUserName = findViewById(R.id.activity_register_tv_user_name);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        tvUserId.setText(sharedPreferences.getString("userId", ""));
        tvUserName.setText(sharedPreferences.getString("userName", ""));
        Button buttonSubmit = findViewById(R.id.activity_register_button_submit);
        buttonSubmit.setOnClickListener(this);
        myHandler = new MyHandler();

    }

    public void setInfo(String userId, String userName) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.putString("userName", userName);
        editor.apply();
        tvUserId.setText(userId);
        tvUserName.setText(userName);
    }

    @Override
    public void onClick(View v) {
        String nickName = editTextNickName.getText().toString();
        if (nickName.equals("")) {
            Toast.makeText(this, "不能输入空的昵称", Toast.LENGTH_SHORT).show();
            return;
        }
        MyAsyncTaskRegister myAsyncTaskRegister = new MyAsyncTaskRegister(this);
        myAsyncTaskRegister.execute(nickName);
    }

    public static class MyAsyncTaskRegister extends AsyncTask<String, Integer, String> {
        private WeakReference<RegisterActivity> registerActivityWeakReference;

        public MyAsyncTaskRegister(RegisterActivity registerActivity) {
            registerActivityWeakReference = new WeakReference<RegisterActivity>(registerActivity);
        }

        @Override
        protected String doInBackground(String... strings) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("userName", strings[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Request request = new Request.Builder()
                    .url("http://192.168.1.33:8083/registerAccount")
                    .post(requestBody)
                    .build();
            OkHttpClient okHttpClient = new OkHttpClient();
            Response response = null;
            try {
                response = okHttpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String result = "";
            if (response != null) {
                try {
                    result = response.body().string();
                    Log.d("debug", "注册返回的结果->" + result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!s.equals("")) {
                RegisterUserResVO registerUserResVO = JSON.parseObject(s, RegisterUserResVO.class);
                RegisterActivity registerActivity = registerActivityWeakReference.get();
                RegisterUserResVO.DataBody dataBody = registerUserResVO.getDataBody();
                if (dataBody != null) {
                    registerActivity.setInfo(String.valueOf(dataBody.getUserId()), dataBody.getUserName());
                }
            }
        }
    }

    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    String dataFromThread = (String) msg.obj;
                    Log.d("debug", "从主线程中接收到来自其他线程的数据：" + dataFromThread);
                    break;
                default:
                    Log.d("debug", "null");
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyThread myThread = new MyThread();
        myThread.start();
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            Message message = myHandler.obtainMessage();
            message.what = 0;
            message.obj = "这是线程发送过去的数据";
            boolean resultOfSend = myHandler.sendMessage(message);
            Log.d("debug", "打印发送结果：" + resultOfSend);
            tvUserName.setText("在线程操作UI");
        }
    }
}
