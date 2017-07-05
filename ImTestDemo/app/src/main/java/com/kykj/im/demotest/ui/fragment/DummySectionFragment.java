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

import java.util.List;


/**
 * /**
 * A dummy fragment representing a section of the app, but that simply displays dummy text.
 * This would be replaced with your application's content.
 *
 * Created by Administrator on 2017/7/3.
 */

public class DummySectionFragment  extends Fragment {

    private ListView listView;
    private MsgAdapter msgAdapter;
    private List<MsgBean> msgBeanList;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    public DummySectionFragment() {
    }

    public DummySectionFragment(List<MsgBean> msgBeanList){
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
                ToastUtils.showMsg(DummySectionFragment.this.getActivity(),"第"+i+"被点击");
            }
        });
    }

}
