package com.buyuphk.nettyclientdeomo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.buyuphk.nettyclientdeomo.adapter.OnlineUserListAdapter;
import com.buyuphk.nettyclientdeomo.service.RouteRequest;
import com.buyuphk.nettyclientdeomo.service.impl.RouteRequestImpl;
import com.buyuphk.nettyclientdeomo.vo.res.OnlineUsersResVO;

import java.lang.ref.WeakReference;
import java.util.List;

public class OnlineUserListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_user_list);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在加载中");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        listView = findViewById(R.id.activity_online_user_list_list_view);
        listView.setOnItemClickListener(this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userId = sharedPreferences.getString("userId", "");
        String userName = sharedPreferences.getString("userName", "");
        progressDialog.show();
        MyAsyncTaskOnlineUserList myAsyncTaskOnlineUserList = new MyAsyncTaskOnlineUserList(this);
        myAsyncTaskOnlineUserList.execute(userId, userName);
    }

    public void setData(List<OnlineUsersResVO.DataBodyBean> data) {
        if (data != null) {
            OnlineUserListAdapter onlineUserListAdapter = new OnlineUserListAdapter(this, data);
            listView.setAdapter(onlineUserListAdapter);
        } else {
            Toast.makeText(this, "没有在线用户", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OnlineUserListAdapter onlineUserListAdapter = (OnlineUserListAdapter) parent.getAdapter();
        OnlineUsersResVO.DataBodyBean dataBodyBean = (OnlineUsersResVO.DataBodyBean) onlineUserListAdapter.getItem(position);
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(String.valueOf(dataBodyBean.getUserId()));
        Toast.makeText(this, "复制用户ID成功", Toast.LENGTH_SHORT).show();
    }

    public static class MyAsyncTaskOnlineUserList extends AsyncTask<String, Integer, List<OnlineUsersResVO.DataBodyBean>> {
        private WeakReference<OnlineUserListActivity> onlineUserListActivityWeakReference;
        
        public MyAsyncTaskOnlineUserList(OnlineUserListActivity onlineUserListActivity) {
            onlineUserListActivityWeakReference = new WeakReference<OnlineUserListActivity>(onlineUserListActivity);
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
}
