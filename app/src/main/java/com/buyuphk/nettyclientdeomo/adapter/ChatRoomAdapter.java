package com.buyuphk.nettyclientdeomo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.buyuphk.nettyclientdeomo.R;

import java.util.List;

/**
 * Copyright (C), buyuphk物流中转站
 * author: JianfeiMa
 * email: majianfei93@163.com
 * revised: 2019-11-22 13:41
 * motto: 勇于向未知领域探索
 */
public class ChatRoomAdapter extends BaseAdapter {
    private Context context;
    private List<String> messages;

    public ChatRoomAdapter(Context context, List<String> messages) {
        this.context = context;
        this.messages = messages;
    }

    public void addNewMessage(String message) {
        messages.add(message);
        notifyDataSetChanged();
    }

    public void dataChanged(List<String> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages == null ? 0 : messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_root, null);
            viewHolder = new ViewHolder();
            viewHolder.setTvMessage(convertView.findViewById(R.id.item_chat_room_user_message));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String msg = messages.get(position);
        viewHolder.getTvMessage().setText(msg);
        return convertView;
    }

    private class ViewHolder {
        private TextView tvMessage;

        public TextView getTvMessage() {
            return tvMessage;
        }

        public void setTvMessage(TextView tvMessage) {
            this.tvMessage = tvMessage;
        }
    }
}
