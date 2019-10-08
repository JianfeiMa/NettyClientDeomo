package com.buyuphk.nettyclientdeomo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.buyuphk.nettyclientdeomo.R;
import com.buyuphk.nettyclientdeomo.vo.res.OnlineUsersResVO;

import java.util.List;

public class OnlineUserListAdapter extends BaseAdapter {
    private Context context;
    private List<OnlineUsersResVO.DataBodyBean> onlineUserList;

    public OnlineUserListAdapter(Context context, List<OnlineUsersResVO.DataBodyBean> onlineUserList) {
        this.context = context;
        this.onlineUserList = onlineUserList;
    }

    @Override
    public int getCount() {
        return onlineUserList == null ? 0 : onlineUserList.size();
    }

    @Override
    public Object getItem(int position) {
        return onlineUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_online_user_list, null);
            viewHolder = new ViewHolder();
            viewHolder.userId =  convertView.findViewById(R.id.item_online_user_list_user_id);
            viewHolder.userName = convertView.findViewById(R.id.item_online_user_list_user_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String sUserId = "用户ID：" + String.valueOf(onlineUserList.get(position).getUserId());
        String sUserName = "用户名：" + onlineUserList.get(position).getUserName();
        viewHolder.userId.setText(sUserId);
        viewHolder.userName.setText(sUserName);
        return convertView;
    }

    public class ViewHolder {
        private TextView userId;
        private TextView userName;
    }
}
