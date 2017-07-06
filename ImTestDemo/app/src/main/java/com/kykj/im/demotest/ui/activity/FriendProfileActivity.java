package com.kykj.im.demotest.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kykj.im.demotest.R;
import com.kykj.im.demotest.cache.DemoCache;
import com.kykj.im.demotest.javabean.MessageEvent;
import com.kykj.im.demotest.presenter.MainPresenter;
import com.kykj.im.demotest.utils.ToastUtils;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.dialog.EasyEditDialog;
import com.netease.nim.uikit.common.ui.widget.SwitchButton;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nim.uikit.model.ToolBarOptions;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.FriendServiceObserve;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.friend.model.MuteListChangedNotify;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 个人信息页
 * Created by Administrator on 2017/7/4.
 */

public class FriendProfileActivity extends UI {

    private String account;
    private BtnOnclickListener onclickListener;
    private MainPresenter mainPresenter;

//    private HeadImageView user_head_image;
    private TextView user_name;
    private TextView user_account;
    private TextView user_nick;
    private Button begin_chat;
    private Button remove_buddy;
    private Button add_buddy;

    private final boolean FLAG_ADD_FRIEND_DIRECTLY = true; // 是否直接加为好友开关，false为需要好友申请

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        ToolBarOptions options = new ToolBarOptions();
        options.titleId = R.string.user_profile;
        setToolBar(R.id.toolbar, options);
        mainPresenter = new MainPresenter(this);
        account = getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
        onclickListener = new BtnOnclickListener();
        initActionbar();

        findViews();
        registerObserver(true);

        updateUserInfo();
        updateToggleView();
    }

    private void initActionbar(){
        TextView toolbarView = findView(R.id.action_bar_right_clickable_textview);
        if ( DemoCache.getAccount() != null && !DemoCache.getAccount().equals(account)) {
            toolbarView.setVisibility(View.GONE);
            return;
        } else {
            toolbarView.setVisibility(View.VISIBLE);
        }
        toolbarView.setText(R.string.edit);
        toolbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                UserProfileSettingActivity.start(UserProfileActivity.this, account);
            }
        });
    }

    private void findViews(){
//        user_head_image = (HeadImageView) findViewById(R.id.user_head_image);
        user_name = (TextView) findViewById(R.id.user_name);
        user_account = (TextView) findViewById(R.id.user_account);
        user_nick = (TextView) findViewById(R.id.user_nick);
        begin_chat = (Button) findViewById(R.id.begin_chat);
        remove_buddy = (Button) findViewById(R.id.remove_buddy);
        add_buddy = (Button) findViewById(R.id.add_buddy);
        begin_chat.setOnClickListener(onclickListener);
        remove_buddy.setOnClickListener(onclickListener);
        add_buddy.setOnClickListener(onclickListener);
    }

    private void registerObserver(boolean register){
        FriendDataCache.getInstance().registerFriendDataChangedObserver(friendDataChangedObserver, register);
        NIMClient.getService(FriendServiceObserve.class).observeMuteListChangedNotify(muteListChangedNotifyObserver, register);
    }

    class BtnOnclickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.begin_chat:
                    onChat();
                    break;
                case R.id.remove_buddy:
                    onRemoveFriend();
                    break;
                case R.id.add_buddy:
                    if (FLAG_ADD_FRIEND_DIRECTLY) {
                        doAddFriend(null, true);  // 直接加为好友
                    } else {
                        onAddFriendByVerify(); // 发起好友验证请求
                    }
                    break;
            }
        }
    }

    Observer<MuteListChangedNotify> muteListChangedNotifyObserver = new Observer<MuteListChangedNotify>() {
        @Override
        public void onEvent(MuteListChangedNotify notify) {
//            setToggleBtn(noticeSwitch, !notify.isMute());
        }
    };

    FriendDataCache.FriendDataChangedObserver friendDataChangedObserver = new FriendDataCache.FriendDataChangedObserver() {
        @Override
        public void onAddedOrUpdatedFriends(List<String> account) {
            updateUserOperatorView();
        }

        @Override
        public void onDeletedFriends(List<String> account) {
            updateUserOperatorView();
        }

        @Override
        public void onAddUserToBlackList(List<String> account) {
            updateUserOperatorView();
        }

        @Override
        public void onRemoveUserFromBlackList(List<String> account) {
            updateUserOperatorView();
        }
    };


    private void setToggleBtn(SwitchButton btn, boolean isChecked) {
        btn.setCheck(isChecked);
    }

    private void updateUserInfo() {
        if (NimUserInfoCache.getInstance().hasUser(account)) {
            updateUserInfoView();
            return;
        }

        NimUserInfoCache.getInstance().getUserInfoFromRemote(account, new RequestCallbackWrapper<NimUserInfo>() {
            @Override
            public void onResult(int code, NimUserInfo result, Throwable exception) {
                updateUserInfoView();
            }
        });
    }

    private void updateUserInfoView() {
        user_account.setText("帐号：" + account);
//        user_head_image.loadBuddyAvatar(account);

        if (DemoCache.getAccount().equals(account)) {
            user_name.setText(NimUserInfoCache.getInstance().getUserName(account));
        }

        final NimUserInfo userInfo = NimUserInfoCache.getInstance().getUserInfo(account);
        if (userInfo == null) {
            return;
        }
    }

    private void updateUserOperatorView() {
        begin_chat.setVisibility(View.VISIBLE);
        if (NIMClient.getService(FriendService.class).isMyFriend(account)) {
            remove_buddy.setVisibility(View.VISIBLE);
            add_buddy.setVisibility(View.GONE);
            updateAlias(true);
        } else {
            add_buddy.setVisibility(View.VISIBLE);
            remove_buddy.setVisibility(View.GONE);
            updateAlias(false);
        }
    }

    private void updateToggleView() {
        if (DemoCache.getAccount() != null && !DemoCache.getAccount().equals(account)) {
//            boolean black = NIMClient.getService(FriendService.class).isInBlackList(account);
//            boolean notice = NIMClient.getService(FriendService.class).isNeedMessageNotify(account);
            updateUserOperatorView();
        }
    }

    private SwitchButton addToggleItemView(String key, int titleResId, boolean initState) {
        ViewGroup vp = (ViewGroup) getLayoutInflater().inflate(R.layout.nim_user_profile_toggle_item, null);
        ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.isetting_item_height));
        vp.setLayoutParams(vlp);

        TextView titleText = ((TextView) vp.findViewById(R.id.user_profile_title));
        titleText.setText(titleResId);

        SwitchButton switchButton = (SwitchButton) vp.findViewById(R.id.user_profile_toggle);
        switchButton.setCheck(initState);
        switchButton.setOnChangedListener(onChangedListener);
        switchButton.setTag(key);
        return switchButton;
    }

    private void updateAlias(boolean isFriend) {
        if (isFriend) {
            Friend friend = FriendDataCache.getInstance().getFriendByAccount(account);
            if (friend != null && !TextUtils.isEmpty(friend.getAlias())) {
                user_nick.setVisibility(View.VISIBLE);
                user_name.setText(friend.getAlias());
                user_nick.setText("昵称：" + NimUserInfoCache.getInstance().getUserName(account));
            } else {
                user_nick.setVisibility(View.GONE);
                user_name.setText(NimUserInfoCache.getInstance().getUserName(account));
            }
        } else {
            user_nick.setVisibility(View.GONE);
            user_name.setText(NimUserInfoCache.getInstance().getUserName(account));
        }
    }

    private SwitchButton.OnChangedListener onChangedListener = new SwitchButton.OnChangedListener() {
        @Override
        public void OnChanged(View v, final boolean checkState) {
            final String key = (String) v.getTag();
            updateStateMap(checkState, key);
        }
    };

    private void updateStateMap(boolean checkState, String key) {
    }

    /**
     * 通过验证方式添加好友
     */
    private void onAddFriendByVerify() {
        final EasyEditDialog requestDialog = new EasyEditDialog(this);
        requestDialog.setEditTextMaxLength(32);
        requestDialog.setTitle(getString(R.string.add_friend_verify_tip));
        requestDialog.addNegativeButtonListener(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestDialog.dismiss();
            }
        });
        requestDialog.addPositiveButtonListener(R.string.send, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestDialog.dismiss();
                String msg = requestDialog.getEditMessage();
                doAddFriend(msg, false);
            }
        });
        requestDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        requestDialog.show();
    }

    private void doAddFriend(String msg, boolean addDirectly) {
        if (!NetworkUtil.isNetAvailable(this)) {
//            Toast.makeText(UserProfileActivity.this, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
            ToastUtils.showMsg(this,getResources().getString(R.string.network_is_not_available));
            return;
        }
        if (!TextUtils.isEmpty(account) && account.equals(DemoCache.getAccount())) {
            ToastUtils.showMsg(this,getResources().getString(R.string.add_friend_self_tip));
            return;
        }
        final VerifyType verifyType = addDirectly ? VerifyType.DIRECT_ADD : VerifyType.VERIFY_REQUEST;
        DialogMaker.showProgressDialog(this, "", true);
        NIMClient.getService(FriendService.class).addFriend(new AddFriendData(account, verifyType, msg))
                .setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                        DialogMaker.dismissProgressDialog();
                        updateUserOperatorView();
                        if (VerifyType.DIRECT_ADD == verifyType) {
                            ToastUtils.showMsg(FriendProfileActivity.this,getResources().getString(R.string.add_friend_success));
                            MessageEvent event = new MessageEvent();
                            event.setEventTag(MessageEvent.TAG_ADD_FRIEND_SUCCESS);
                            event.setObject(account);
                            EventBus.getDefault().post(event);
                        } else {
                            ToastUtils.showMsg(FriendProfileActivity.this,getResources().getString(R.string.friend_request_success));
                        }
                    }

                    @Override
                    public void onFailed(int code) {
                        DialogMaker.dismissProgressDialog();
                        if (code == 408) {
                            ToastUtils.showMsg(FriendProfileActivity.this, getResources().getString(R.string.network_is_not_available));
                        } else {
                            ToastUtils.showMsg(FriendProfileActivity.this, "on failed:" + code);
                        }
                    }

                    @Override
                    public void onException(Throwable exception) {
                        DialogMaker.dismissProgressDialog();
                    }
                });

    }

    private void onRemoveFriend() {
        if (!NetworkUtil.isNetAvailable(this)) {
            ToastUtils.showMsg(FriendProfileActivity.this, getResources().getString(R.string.network_is_not_available));
            return;
        }
        EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(this, getString(R.string.remove_friend),
                getString(R.string.remove_friend_tip), true,
                new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {

                    }

                    @Override
                    public void doOkAction() {
                        DialogMaker.showProgressDialog(FriendProfileActivity.this, "", true);
                        NIMClient.getService(FriendService.class).deleteFriend(account).setCallback(new RequestCallback<Void>() {
                            @Override
                            public void onSuccess(Void param) {
                                DialogMaker.dismissProgressDialog();
                                EventBus.getDefault().post(new MessageEvent(MessageEvent.TAG_DELETE_FRIEND_SUCCESS,account));
                                ToastUtils.showMsg(FriendProfileActivity.this, getResources().getString(R.string.remove_friend_success));
                                finish();
                            }

                            @Override
                            public void onFailed(int code) {
                                DialogMaker.dismissProgressDialog();
                                if (code == 408) {
                                    ToastUtils.showMsg(FriendProfileActivity.this, getResources().getString(R.string.network_is_not_available));
                                } else {
                                    ToastUtils.showMsg(FriendProfileActivity.this, "on failed:" + code);
                                }
                            }

                            @Override
                            public void onException(Throwable exception) {
                                DialogMaker.dismissProgressDialog();
                                ToastUtils.showMsg(FriendProfileActivity.this, "on exception:" + exception.toString());
                            }
                        });
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            dialog.show();
        }
    }

    private void onChat() {
//        NimUIKit.startP2PSession(this,account);
//        SessionHelper.startP2PSession(this, account);
       mainPresenter.startActivity(this,account,null,P2pSessionActivity.class);
    }

}
