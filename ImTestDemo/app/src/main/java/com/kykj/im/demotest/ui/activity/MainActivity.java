package com.kykj.im.demotest.ui.activity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.kykj.im.demotest.R;
import com.kykj.im.demotest.javabean.MsgBean;
import com.kykj.im.demotest.presenter.MainPresenter;
import com.kykj.im.demotest.ui.fragment.ContactsFrament;
import com.kykj.im.demotest.ui.fragment.LiveRoomFrament;
import com.kykj.im.demotest.ui.fragment.MsgFrament;
import com.kykj.im.demotest.utils.ToastUtils;
import com.kykj.im.demotest.weight.ActionItem;
import com.kykj.im.demotest.weight.TitlePopup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 主页
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;

    ImageView iv_menu;

    List<Fragment> fragmentList = new ArrayList<>();//页面列表

    TitlePopup titlePopup;

    private MainPresenter mainPresenter;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);
        initPopMenu();
        initVal();
        initActionBar();
        iv_menu = (ImageView) findViewById(R.id.iv_menu);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titlePopup.show(v);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragmentList.get(position);
//            Fragment fragment = new DummySectionFragment(setMsgListData());
//            Bundle bundle = new Bundle();
//            bundle.putInt("tab_item",position);
//            fragment.setArguments(bundle);
            return fragment;
        }
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_message).toUpperCase(l);
                case 1:
                    return getString(R.string.title_contacts).toUpperCase(l);
                case 2:
                    return getString(R.string.title_live_room).toUpperCase(l);
            }
            return null;
        }
    }

    private void initActionBar(){
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        setFragmentList();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

    }


    /**
     * 页面列表
     */
    private List<Fragment> setFragmentList(){
        fragmentList.clear();
        fragmentList.add(new MsgFrament());
        fragmentList.add(new ContactsFrament());
        fragmentList.add(new LiveRoomFrament());
        return fragmentList;
    }

//    @Subscribe
//    public void onContactsChencged(MessageEvent event){
//        if(MessageEvent.TAG_ADD_FRIEND_SUCCESS.equals(event.getEventTag())){
//
//        }else if(MessageEvent.TAG_DELETE_FRIEND_SUCCESS.equals(event.getEventTag())){
//
//        }
//    }

    private void initPopMenu(){
        titlePopup = new TitlePopup(this);
        // 实例化标题栏弹窗
        titlePopup = new TitlePopup(this, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 给标题栏弹窗添加子类
        titlePopup.addAction(new ActionItem(this, R.string.add_friend,
                R.drawable.nim_emoji_icon_inactive));
        titlePopup.addAction(new ActionItem(this, R.string.create_chat_group,
                R.drawable.nim_message_input_emotion_pressed));
        titlePopup.addAction(new ActionItem(this, R.string.create_high_group,
                R.drawable.nim_message_input_emotion));
        titlePopup.addAction(new ActionItem(this, R.string.search_high_group,
                R.drawable.nim_message_input_emotion_pressed));
        titlePopup.addAction(new ActionItem(this, R.string.settings,
                R.drawable.nim_emoji_icon));
        titlePopup.setItemOnClickListener(new TitlePopup.OnItemOnClickListener() {
            @Override
            public void onItemClick(ActionItem item, int position) {
                switch (position){
                    case 0:
                        mainPresenter.startActivity(context,"",null,AddFriendActivity.class);
//                         ToastUtils.showMsg(MainActivity.this,position+"");
                        break;
                    case 1:
                         ToastUtils.showMsg(MainActivity.this,position+"");break;
                     case 2:
                         ToastUtils.showMsg(MainActivity.this,position+"");
                         break;
                     case 3:
                         ToastUtils.showMsg(MainActivity.this,position+"");
                         break;
                     case 4:
                         ToastUtils.showMsg(MainActivity.this,position+"");
                         break;
                     default:break;
                 }
            }
        });
    }

    /**
     * 变量初始化
     */
    private void initVal(){
        mainPresenter = new MainPresenter(this);
        context = MainActivity.this;
    }

}
