package com.kykj.im.demotest.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kykj.im.demotest.R;
import com.kykj.im.demotest.adapter.MsgAdapter;
import com.kykj.im.demotest.javabean.MsgBean;
import com.kykj.im.demotest.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/5.
 */

public class MsgFrament extends Fragment {

    private ListView listView;
    private MsgAdapter msgAdapter;
    private List<MsgBean> msgBeanList;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    public MsgFrament(){
        this.msgBeanList = setMsgListData();
    }

    public MsgFrament(List<MsgBean> msgBeanList){
        this.msgBeanList = msgBeanList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        initListView();
        return rootView;
    }

    private void initListView(){
        msgAdapter = new MsgAdapter(getActivity(),msgBeanList);
        listView.setAdapter(msgAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ToastUtils.showMsg(MsgFrament.this.getActivity(),"第"+i+"被点击");
            }
        });
    }

    /**
     * 消息列表数据
     * @return
     */
    private List<MsgBean> setMsgListData(){
        msgBeanList = new ArrayList<>();
        msgBeanList.clear();
        msgBeanList.add(new MsgBean("vectoria","["+"online"+"]"));
        msgBeanList.add(new MsgBean("tomas","["+"offline"+"]"));
        msgBeanList.add(new MsgBean("peter","["+"online"+"]"));
        msgBeanList.add(new MsgBean("Alisa","["+"online"+"]"));
        return msgBeanList;
    }

}
