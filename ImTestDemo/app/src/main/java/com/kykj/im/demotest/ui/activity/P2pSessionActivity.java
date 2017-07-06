package com.kykj.im.demotest.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kykj.im.demotest.R;
import com.kykj.im.demotest.cache.DemoCache;
import com.kykj.im.demotest.javabean.MessageEvent;
import com.kykj.im.demotest.presenter.MainPresenter;
import com.kykj.im.demotest.presenter.MsgPresenter;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nim.uikit.session.module.ModuleProxy;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 单聊聊天界面
 * Created by Administrator on 2017/7/6.
 */

public class P2pSessionActivity extends UI {

    private String account;
    private MainPresenter mainPresenter;
    private MsgPresenter msgPresenter;

    @Bind(R.id.btn_send)
    Button btn_send;

    @Bind(R.id.tv_receive_msg)
    TextView tv_receive_msg;//显示接收到的消息

    @Bind(R.id.tv_send_msg)
    TextView tv_send_msg;//显示已发送的消息

    @Bind(R.id.iv_voice)
    ImageView iv_voice;

    @Bind(R.id.iv_expression)
    ImageView iv_expression;

    @Bind(R.id.iv_add)
    ImageView iv_add;

    @Bind(R.id.et_content)
    EditText et_content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p2p_session);
        ButterKnife.bind(this);
        account = getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
        mainPresenter = new MainPresenter(this);
        msgPresenter = new MsgPresenter(account);
        initActionBar();
        initView();
        msgPresenter.registerObservers(true);
//        msgPresenter.registerOnlineStateChangeListener(true);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        msgPresenter.registerObservers(false);
//        msgPresenter.registerOnlineStateChangeListener(false);
        EventBus.getDefault().unregister(this);
    }

    private void initActionBar(){
        ToolBarOptions options = new ToolBarOptions();
//        options.titleId = R.string.user_profile;
        options.titleString = account;
        setToolBar(R.id.toolbar, options);

        TextView toolbarView = findView(R.id.action_bar_right_clickable_textview);
        if (!DemoCache.getAccount().equals(account)) {
            toolbarView.setVisibility(View.GONE);
            return;
        } else {
            toolbarView.setVisibility(View.VISIBLE);
        }
        toolbarView.setText(account);
        toolbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                 mainPresenter.startActivity(P2pSessionActivity.this,account,null,);
//                UserProfileSettingActivity.start(UserProfileActivity.this, account);
            }
        });
    }

    private void initView(){
//        if (getCurrentFocus() == et_content || !TextUtils.isEmpty(et_content.getText())){
            iv_add.setVisibility(View.GONE);
            btn_send.setVisibility(View.VISIBLE);
//        }else{
//            btn_send.setVisibility(View.GONE);
//            iv_add.setVisibility(View.VISIBLE);
//        }

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送消息
                String content = et_content.getText().toString().trim();
                tv_send_msg.setText(content);
                msgPresenter.sendMessage(content);
                et_content.setText("");
            }
        });
    }

    @Subscribe
    public void onNewMsg(MessageEvent event){
        if(event.getEventTag().equals(MessageEvent.TAG_NEW_MSG)){
            tv_receive_msg.setText("\n"+event.getObject());
        }
    }
}
