package com.jlf.dbzh.im;

import android.content.Context;
import android.widget.Toast;

public class Util {
    public static final String ws = "ws://192.168.0.250:8888";//websocket测试地址

    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }
}
