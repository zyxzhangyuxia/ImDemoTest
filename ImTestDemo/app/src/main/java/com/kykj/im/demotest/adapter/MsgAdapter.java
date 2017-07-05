package com.kykj.im.demotest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kykj.im.demotest.R;
import com.kykj.im.demotest.javabean.MsgBean;

import java.util.List;


/**
 * Created by Administrator on 2017/7/3.
 */

public class MsgAdapter extends BaseAdapter {
    Context context;
    List<MsgBean> msg_lists;

    TextView tv_name;
    TextView tv_status;

    public MsgAdapter(){}

    public MsgAdapter(Context context,List<MsgBean> msg_lists){
        this.context = context;
        this.msg_lists = msg_lists;
    }

    @Override
    public int getCount() {
        return msg_lists.size();
    }

    @Override
    public Object getItem(int i) {
        return msg_lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.item_listview_layout,viewGroup,false);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_status = (TextView) view.findViewById(R.id.tv_status);
        tv_name.setText(msg_lists.get(i).getName());
        tv_status.setText(msg_lists.get(i).getStatus());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //开始聊天窗口
            }
        });
        return view;
    }
}