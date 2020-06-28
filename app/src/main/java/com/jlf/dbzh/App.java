package com.jlf.dbzh;

import android.app.Application;
import android.content.Context;

import com.jlf.dbzh.utils.TokenUtils;

public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        TokenUtils.init(this);
    }
}
