package com.kykj.im.demotest.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kykj.im.demotest.cache.DemoCache;
import com.kykj.im.demotest.config.preference.Preferences;
import com.kykj.im.demotest.javabean.MessageEvent;
import com.kykj.im.demotest.presenter.MainPresenter;
import com.kykj.im.demotest.utils.LogUtil;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nim.uikit.permission.MPermission;
import com.netease.nim.uikit.permission.annotation.OnMPermissionDenied;
import com.netease.nim.uikit.permission.annotation.OnMPermissionGranted;
import com.netease.nim.uikit.permission.annotation.OnMPermissionNeverAskAgain;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import com.kykj.im.demotest.R;
import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2017/7/4.
 */

public class HomeActivity extends UI {

    @Bind(R.id.btn_login)
    Button btn_login;
    @Bind(R.id.et_username)
    EditText et_username;
    @Bind(R.id.et_pwd)
    EditText et_pwd;

    MainPresenter mainPresenter;

    private final int BASIC_PERMISSION_REQUEST_CODE = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initEventListener();
        EventBus.getDefault().register(this);
        DemoCache.setMainTaskLaunching(true);

        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.user_login;
        setToolBar(R.id.toolbar, options);
        initActionbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(canAutoLogin()){
            MainPresenter mainPresenter = new MainPresenter(this);
            mainPresenter.startActivity(this,DemoCache.getAccount(),null,MainActivity.class);
            finish();
        }
    }

    private void initEventListener() {
        mainPresenter = new MainPresenter(this);
        mainPresenter.requestBasicPermission();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });
    }

    /**
     * 登录
     */
    private void doLogin() {
        String username = et_username.getText().toString().trim();
        String pwd = et_pwd.getText().toString().trim();
        mainPresenter.login(username, pwd);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
        Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    @OnMPermissionNeverAskAgain(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
        Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccess(String msg) {
        if (MessageEvent.TAG_LOGIN_SUCCESS.equals(msg)) {
            LogUtil.D("接受登录成功的事件" + msg);
//            HomeActivity.startActivity(this, null);
            mainPresenter.startActivity(this,"",null,MainActivity.class);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        DemoCache.setMainTaskLaunching(false);
    }

    private void initActionbar() {
        TextView toolbarView = findView(R.id.action_bar_right_clickable_textview);
        toolbarView.setText(R.string.help);
    }
    /**
     * 判断是否自动登录
     */
    private boolean canAutoLogin(){
        String account = Preferences.getUserAccount();
        String token = Preferences.getUserToken();

        return !TextUtils.isEmpty(account) && !TextUtils.isEmpty(token);
    }
}
