package com.kykj.im.demotest.javabean;

/**
 * Created by Administrator on 2017/7/3.
 */

public class MsgBean {
    String name;
    String status;

    public MsgBean(){}

    public MsgBean(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MsgBean{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
