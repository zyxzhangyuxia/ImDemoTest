package com.kykj.im.demotest.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.kykj.im.demotest.R;
import com.kykj.im.demotest.cache.DemoCache;
import com.kykj.im.demotest.javabean.MessageEvent;
import com.kykj.im.demotest.utils.LogUtil;
import com.kykj.im.demotest.utils.MD5;
import com.kykj.im.demotest.utils.ToastUtils;
import com.netease.nim.uikit.permission.MPermission;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by vectoria on 2017/6/27.
 */

public class MainPresenter {
    private Context context;
    private final int BASIC_PERMISSION_REQUEST_CODE = 110;
    public MainPresenter(Context context){
        this.context = context;
    }

    /**
     * 用户登录
     * @param account
     * @param token
     */
    public void login(String account, String token){
        if(TextUtils.isEmpty(account)){
            showMsg(context.getResources().getString(R.string.empty_username));
            return;
        }
        if(TextUtils.isEmpty(token)){
            showMsg(context.getResources().getString(R.string.empty_pwd));
            return;
        }
        LoginInfo loginInfo = new LoginInfo(account,tokenFromPassword(token));
        LogUtil.D("loginInfo.getAccount = "+loginInfo.getAccount());
        LogUtil.D("loginInfo.getToken = "+loginInfo.getToken());
        AbortableFuture<LoginInfo> loginRequest = NIMClient.getService(AuthService.class).login(loginInfo);

        RequestCallback<LoginInfo> callback = new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                LogUtil.D("param = "+param.getAccount()+"--account = "+""+param.getToken());
                //登录成功，缓存数据
                setDemoChahe(param.getAccount(),param.getToken());
                EventBus.getDefault().post(MessageEvent.TAG_LOGIN_SUCCESS);
            }

            @Override
            public void onFailed(int code) {
                LogUtil.D("onFailed.code = "+code);
                ToastUtils.showMsg(context,context.getResources().getString(R.string.login_fail));
            }

            @Override
            public void onException(Throwable exception) {
                LogUtil.D("exception.tostring= "+exception.toString());
            }
        };
        loginRequest.setCallback(callback);
    }

    /**
     * 加密
     * @param password
     * @return
     */
    public String tokenFromPassword(String password) {
        String appKey = readAppKey(context);
        boolean isDemo = "ab73599e8a6efec7abb0142449aa147f".equals(appKey);
        return isDemo ? MD5.getStringMD5(password) : password;
    }

    /**
     * 读取应用的app-key
     * @param context
     * @return
     */
    private static String readAppKey(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo != null) {
                LogUtil.D("appkey = "+appInfo.metaData.getString("com.netease.nim.appKey"));
                return appInfo.metaData.getString("com.netease.nim.appKey");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 申请权限
     */
    public void requestBasicPermission() {
        MPermission.with((Activity) context)
                .setRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(BASIC_PERMISSIONS)
                .request();
    }

    /**
     * 基本权限管理
     */

    private final String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    /**
     * 设置缓存并保存用户登录信息
     * @param account
     * @param token
     */
    public void setDemoChahe(String account, String token){
        DemoCache.setAccount(account);
        saveLoginInfo(account, token);
    }

    /**
     * 保存已登录的用户信息
     * @param account
     * @param token
     */
    public void saveLoginInfo(String account, String token){
        Preference.saveUserAccount(account);
        Preference.saveUserToken(token);
    }

    /**
     * toast信息
     * @param msg
     */
    private void showMsg(String msg){
        Toast.makeText(context,msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 启动该Activity
     * @param context
     * @param extras
     */
    public  void startActivity(Context context,String data,Intent extras,Class<?> cls){
        Intent intent = new Intent();
//        intent.setClass(context, HomeActivity.class);
          intent.setClass(context,cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Extras.EXTRA_ACCOUNT,data);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }
}
