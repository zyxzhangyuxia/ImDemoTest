package com.kykj.im.demotest.application;

import android.app.Application;

import com.kykj.im.demotest.cache.DemoCache;
import com.kykj.im.demotest.presenter.ApplicationPresenter;
import com.netease.nimlib.sdk.NIMClient;

/**
 * Created by Administrator on 2017/7/4.
 */

public class DemoApplication extends Application {

    private ApplicationPresenter presenter;

    @Override
    public void onCreate() {
        super.onCreate();
        presenter = new ApplicationPresenter(this);
        DemoCache.setContext(this);
        /**
         * SDK初始化，如果已经存在登录用户，则SDK完成自动登录
         * 1.this
         * 2.自动登录的参数
         * 3.一些config
         */
        NIMClient.init(this, presenter.loginInfo(), presenter.options());

        if(presenter.isMainProcess()){
            //数据初始化  消息，联系人等
            presenter.initDemoMode();
        }
    }
}
