package com.kykj.im.demotest.presenter;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kykj.im.demotest.javabean.MessageEvent;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.OnlineStateChangeListener;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/7/6.
 */

public class MsgPresenter {

    protected String sessionId;

    public MsgPresenter(String sessionId){
        this.sessionId = sessionId;
    }

    public void registerObservers(boolean register) {
        if (register) {
//            registerUserInfoObserver();
        } else {
//            unregisterUserInfoObserver();
        }
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeReceiveMessage(incomingMessageObserver, register);
//        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(commandObserver, register);
//        FriendDataCache.getInstance().registerFriendDataChangedObserver(friendDataChangedObserver, register);
    }

    public void registerOnlineStateChangeListener(boolean register) {
        if (!NimUIKit.enableOnlineState()) {
            return;
        }
        if (register) {
            NimUIKit.addOnlineStateChangeListeners(onlineStateChangeListener);
        } else {
            NimUIKit.removeOnlineStateChangeListeners(onlineStateChangeListener);
        }
    }

    OnlineStateChangeListener onlineStateChangeListener = new OnlineStateChangeListener() {
        @Override
        public void onlineStateChange(Set<String> accounts) {
            // 更新 toolbar
//            if (accounts.contains(sessionId)) {
//                // 按照交互来展示
//                displayOnlineState();
//            }
        }
    };

    /**
     * 命令消息接收观察者
     */
    Observer<CustomNotification> commandObserver = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification message) {
            if (!sessionId.equals(message.getSessionId()) || message.getSessionType() != SessionTypeEnum.P2P) {
                return;
            }
            showCommandMessage(message);
        }
    };

    /**
     * 消息接收观察者
     */
    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            if (messages == null || messages.isEmpty()) {
                return;
            }else{
                postEvent(messages.get(0).getFromNick()+" : "+messages.get(0).getContent());
            }
//            messageListPanel.onIncomingMessage(messages);
//            sendMsgReceipt(); // 发送已读回执
        }
    };


    protected void showCommandMessage(CustomNotification message) {
        String content = message.getContent();
        try {
            JSONObject json = JSON.parseObject(content);
            int id = json.getIntValue("id");
            if (id == 1) {
                // 正在输入
            } else {
                postEvent(content);
            }

        } catch (Exception e) {

        }
    }

    private void postEvent(String content){
        MessageEvent event = new MessageEvent();
        event.setEventTag(MessageEvent.TAG_NEW_MSG);
        event.setObject(content);
        EventBus.getDefault().post(event);
    }

    public void sendMessage(String content){
        // 创建文本消息
        IMMessage message = MessageBuilder.createTextMessage(
                sessionId, // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                content // 文本内容
        );
      // 发送消息。如果需要关心发送结果，可设置回调函数。发送完成时，会收到回调。如果失败，会有具体的错误码。
        NIMClient.getService(MsgService.class).sendMessage(message,true);
    }

}
