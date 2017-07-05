package com.kykj.im.demotest.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/7/3.
 */

public class ToastUtils {
    public static void showMsg(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
