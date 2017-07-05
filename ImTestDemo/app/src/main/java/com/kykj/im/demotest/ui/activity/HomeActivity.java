package com.kykj.im.demotest.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kykj.im.demotest.cache.MessageEvent;
import com.kykj.im.demotest.presenter.MainPresenter;
import com.kykj.im.demotest.utils.LogUtil;
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

public class HomeActivity extends Activity {

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
    }
}
