package com.jlf.dbzh;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ToastUtils;
import com.zplayer.library.ZPlayer;

public class NewActivity extends AppCompatActivity implements ZPlayer.OnNetChangeListener {

    private ZPlayer mPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        mPlayer = findViewById(R.id.view_super_player);
        initPlayer();
        findViewById(R.id.tv_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort("播放");
                mPlayer.setTitle("播放")
                        .play("http://47.108.82.225:8080/cmcc/XZOCNBI3.flv?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0eXBlIjoiYWRtaW4iLCJhZG1pbl9pZCI6MSwidGltZSI6MTU5MzQxNzE4NywiZXhwaXJlIjpudWxsfQ.6xKp-FXccP5vvjMt8H5UCe0_fLE8cfHy1IQiPcW4h-4");//开始播放视频

            }
        });
    }

    private void initPlayer() {
        mPlayer.setTitle("播放")
                .play("http://47.108.82.225:8080/cmcc/XZOCNBI3.flv?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0eXBlIjoiYWRtaW4iLCJhZG1pbl9pZCI6MSwidGltZSI6MTU5MzQxNzE4NywiZXhwaXJlIjpudWxsfQ.6xKp-FXccP5vvjMt8H5UCe0_fLE8cfHy1IQiPcW4h-4");//开始播放视频
        mPlayer.setLive(true)//设置该地址是直播的地址
                .setNetChangeListener(true)//设置监听手机网络的变化,这个参数是内部是否处理网络监听，和setOnNetChangeListener没有关系
                .setOnNetChangeListener(this)//实现网络变化的回调
                .setScaleType(ZPlayer.SCALETYPE_FITXY)//图片缩放方式
                .setPlayerWH(0, mPlayer.getMeasuredHeight())//设置竖屏的时候屏幕的高度，如果不设置会切换后按照16:9的高度重置
                .setAlwaysShowControl()  //设置则一直显示
                //准备好视频回调
                .onPrepared(() -> {
                    //TODO 监听视频是否已经准备完成开始播放。（可以在这里处理视频封面的显示跟隐藏）
//                  ToastUtils.showShort("开始播放");
                    Toast.makeText(this, "开始播放", Toast.LENGTH_SHORT).show();

                })
                //播放完成回调
                .onComplete(() -> {
                    //TODO 监听视频是否已经播放完成了。（可以在这里处理视频播放完成进行的操作）
                })
                //视频信息回调
                .onInfo((what, extra) -> {
                    //TODO 监听视频的相关信息。
                    Log.e("VIDEO", "onError--what--" + what + "--Extra --" + extra);
                })
                //播放出错回调
                .onError((what, extra) -> {
                    //TODO 监听视频播放失败的回调
                    Log.e("VIDEO", "onError--what--" + what + "--Extra --" + extra);
                });
    }

    /**
     * 网络链接监听类
     */
    @Override
    public void onWifi() {
        Toast.makeText(this, "当前网络环境是WIFI", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMobile() {
        Toast.makeText(this, "当前网络环境是手机网络", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisConnect() {
        Toast.makeText(this, "网络链接断开", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNoAvailable() {
        Toast.makeText(this, "无网络链接", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPlayer != null) {
            mPlayer.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer != null) {
            mPlayer.onPause();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mPlayer != null) {
            mPlayer.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (mPlayer != null && mPlayer.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
