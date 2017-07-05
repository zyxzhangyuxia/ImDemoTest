package com.kykj.im.demotest.cache;

/**
 * EventBus事件
 * Created by vectoria on 2017/6/29.
 */

public class MessageEvent {
    public static final String TAG_LOGIN_SUCCESS = "tag_login_success";

    private String eventTag;

    public MessageEvent(String tagLoginSuccess) {
    }

    public String getEventTag() {
        return eventTag;
    }

    public void setEventTag(String eventTag) {
        this.eventTag = eventTag;
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "eventTag='" + eventTag + '\'' +
                '}';
    }
}
