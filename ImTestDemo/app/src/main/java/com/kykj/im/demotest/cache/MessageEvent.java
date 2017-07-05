package com.kykj.im.demotest.cache;

/**
 * EventBus事件
 * Created by vectoria on 2017/6/29.
 */

public class MessageEvent {
    public static final String TAG_LOGIN_SUCCESS = "tag_login_success";
    public static final String TAG_ADD_FRIEND_SUCCESS = "tag_add_friend_success";
    public static final String TAG_DELETE_FRIEND_SUCCESS = "tag_delete_friend_success";

    private String eventTag;
    private Object object;

    public MessageEvent(){}

    public MessageEvent(String eventTag) {
    }

    public MessageEvent(String eventTag,Object object) {
    }

    public String getEventTag() {
        return eventTag;
    }

    public void setEventTag(String eventTag) {
        this.eventTag = eventTag;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

}
