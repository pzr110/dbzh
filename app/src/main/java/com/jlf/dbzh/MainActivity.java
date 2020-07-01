package com.jlf.dbzh;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.jlf.dbzh.adapter.CallAdapter;
import com.jlf.dbzh.adapter.SoliderAdapter;
import com.jlf.dbzh.bean.JoinBean;
import com.jlf.dbzh.bean.OnlineBean;
import com.jlf.dbzh.bean.TokenBean;
import com.jlf.dbzh.im.JWebSocketClient;
import com.jlf.dbzh.im.JWebSocketClientService;
import com.jlf.dbzh.im.Util;
import com.jlf.dbzh.model.ITRTCAudioCall;
import com.jlf.dbzh.model.TRTCAudioCallImpl;
import com.jlf.dbzh.model.TRTCAudioCallListener;
import com.jlf.dbzh.utils.net.Api;
import com.jlf.dbzh.utils.net.BaseSubscriber;
import com.zplayer.library.ZPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements ZPlayer.OnNetChangeListener {
    private static final int REQ_PERMISSION_CODE = 0x1000;
    private int mGrantedCount = 0;          // 权限个数计数，获取Android系统权限
    private MapView mMvMain;
    private AMap aMap;
    private MyLocationStyle myLocationStyle;

    private RelativeLayout mRlTop;
    private LinearLayout mLlBr;
    private LinearLayout mLlCallList;
    private LinearLayout mLlSoldierList;
    private RecyclerView mRecyclerSoldier;
    private RecyclerView mRecyclerCall;
    private LinearLayout mLlBottom;
    private ImageView mTvDown;
    private ImageView mIvPhone;
    private TextView mTvCallAll;
    private TextView mTvNum;
    private ImageView mIvClose;
    private ImageView mIvCloseAll;


    private boolean isEdit = false;


    private List<OnlineBean.Data.UserList> mLists;
    private List<OnlineBean.Data.UserList> mCallLists;
    private SoliderAdapter mAdapter;
    private CallAdapter mCallAdapter;

    private String mUser_id;
    private String mAppId;
    private String mUserSig;
    private String mFlv_url;
    private TextView mTvSelectAll;


    /////////////// Socket
    private Context mContext;
    private JWebSocketClient client;
    private JWebSocketClientService.JWebSocketClientBinder binder;
    private JWebSocketClientService jWebSClientService;

    private ChatMessageReceiver chatMessageReceiver;

    private TextView mTvTest;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("MainActivity", "服务与活动成功绑定");
            binder = (JWebSocketClientService.JWebSocketClientBinder) iBinder;
            jWebSClientService = binder.getService();
            client = jWebSClientService.client;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("MainActivity", "服务与活动成功断开");
        }
    };
    private List<OnlineBean.Data.UserList> mUserList;
    private String mSolider_id;


    private class ChatMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
//            ToastUtils.showLong("Message" + message);
            Log.e("Socaaaaaaaaaaaaaaaaa", "Message" + message);

            if (message != null) {
                getJson(message);
            }


            boolean onOpen = intent.getBooleanExtra("onOpen", false);
            if (onOpen) {

                if (client != null && client.isOpen()) {
                    JoinBean jsonBean = new JoinBean();
                    jsonBean.setType("join");
                    JoinBean.Data data = new JoinBean.Data();
                    JoinBean.Data.UserInfo userInfo = new JoinBean.Data.UserInfo();
                    userInfo.setUser_id(mUser_id);
                    userInfo.setUsername("指挥一号");
                    userInfo.setPower(0);
                    userInfo.setLng(0);
                    userInfo.setLat(0);
                    userInfo.setRole("leader");
                    userInfo.setFlv_url(mFlv_url);
                    data.setUserinfo(userInfo);
                    jsonBean.setData(data);
                    String json = new Gson().toJson(jsonBean);
                    Log.e("Socaaaaaaaaaaaaaaaaa", "AAAAAAA" + mUser_id);

                    jWebSClientService.sendMsg(json);
                } else {
                    Util.showToast(mContext, "连接已断开，请稍等或重启App哟");
                }
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private void getJson(String message) {
        /**
         * {
         * 	"type": "online_users",
         * 	"data": {
         * 		"userList": [{
         * 			"user_id": "pc_web",
         * 			"username": "\u4e3b\u63a7\u7aef",
         * 			"power": 0,
         * 			"lng": 0,
         * 			"lat": 0,
         * 			"role": "leader",
         * 			"flv_url": ""
         *                }, {
         * 			"flv_url": "http:\/\/47.108.82.225:8080\/cmcc\/MQM2MMTB.flv?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0eXBlIjoicGxheWVyIiwidGltZSI6MTU5MzMyNjc3MCwiZXhwaXJlIjpudWxsfQ.Rqc-FxJ_jO_NVwtjdtfEwMRGnu0E6RYyJoG-4-Xa_kM",
         * 			"lat": 30.405243,
         * 			"lng": 104.082994,
         * 			"power": 6,
         * 			"role": "soldier",
         * 			"user_id": "soldier_3",
         * 			"username": "FITT360 F884"
         *        }, {
         * 			"user_id": "pc_web",
         * 			"username": "\u4e3b\u63a7\u7aef",
         * 			"power": 0,
         * 			"lng": 0,
         * 			"lat": 0,
         * 			"role": "leader",
         * 			"flv_url": ""
         *        }]* 	}
         * }
         */

        OnlineBean onlineBean = new Gson().fromJson(message, OnlineBean.class);

        String type = onlineBean.getType();

        if (onlineBean.getType().equals("online_users")) {
            mUserList = onlineBean.getData().getUserList();

            if (mUserList.size() > 0) {
                for (int i = 0; i < mUserList.size(); i++) {
                    mUserList.get(i).setSelect(false);

                    if (mUserList.get(i).getUser_id().equals("pc_web") || mUserList.get(i).getUser_id().equals(mUser_id)) {
                        mUserList.remove(i);
                    }
                }
            }


            mAdapter.addData(mUserList);

            mCallAdapter.addData(mUserList);

            mTvNum.setText("在线：" + mUserList.size());

        } else if (onlineBean.getType().equals("join")) {
            OnlineBean.Data.UserInfo userinfo = onlineBean.getData().getUserinfo();
            String user_id = userinfo.getUser_id();
            String username = userinfo.getUsername();
            int power = userinfo.getPower();
            double lat = userinfo.getLat();
            double lng = userinfo.getLng();
            String role = userinfo.getRole();
            String flv_url = userinfo.getFlv_url();

            Log.e("ListAAAAAAAAAA", "  joinjoin  " + user_id);
            OnlineBean.Data.UserList userList = new OnlineBean.Data.UserList();
            userList.setUser_id(user_id);
            userList.setUsername(username);
            userList.setPower(power);
            userList.setLat(lat);
            userList.setLng(lng);
            userList.setRole(role);
            userList.setFlv_url(flv_url);

            if (mUserList.size() == 0) {
                mUserList.add(userList);
            } else {
                for (int i = 0; i < mUserList.size(); i++) {
                    Log.e("ListAAAAAAAAAA", "  joinjoin   joinjoin  " + mUserList.get(i).getUser_id());
                    if (mUserList.get(i).getUser_id() != null) {
                        if (!mUserList.get(i).getUser_id().equals(user_id)) {
                            mUserList.add(userList);
                        }
                        if (mUserList.get(i).getUser_id().equals("pc_web") || mUserList.get(i).getUser_id().equals(mUser_id)) {
                            mUserList.remove(i);
                        }
                    }
//
                }
            }

//            if (mUserList.size()>0){
//                for (int i = 0; i < mUserList.size(); i++) {
//                    mUserList.get(i).setSelect(false);
//
//                    if (mUserList.get(i).getUser_id().equals("pc_web")||mUserList.get(i).getUser_id().equals(mUser_id)) {
//                        mUserList.remove(i);
//                    }
//                }
//            }

            mAdapter.setNewData(mUserList);
            mCallAdapter.setNewData(mUserList);
            mTvNum.setText("在线：" + mUserList.size());

        } else if (onlineBean.getType().equals("position")) {
            String user_id = onlineBean.getData().getUser_id();
            String lat = onlineBean.getData().getPosition().getLat();
            Log.e("ListAAAAAAAAAA", "position" + user_id + lat);
        } else if (onlineBean.getType().equals("leave")) {
            String user_id = onlineBean.getData().getUser_id();

            if (mUserList.size() > 0) {
                for (int i = 0; i < mUserList.size(); i++) {
                    if (mUserList.get(i).getUser_id().equals(user_id)) {
                        mUserList.remove(i);
                    }
//                if (mUserList.get(i).getUser_id().contains("pc_web")||mUserList.get(i).getUser_id().equals(mUser_id)) {
//                    mUserList.remove(i);
//                }
                }
            }

            mAdapter.setNewData(mUserList);
            mCallAdapter.setNewData(mUserList);
            mTvNum.setText("在线：" + mUserList.size());
        }

    }
    //////////////////Socket

    //////////语音
    private ITRTCAudioCall sCall;

    private int mCallType;

    private boolean checkOff = true;

    private ImageView mIvPhoneAll;


    /**
     * 拨号的回调
     */
    private TRTCAudioCallListener mTRTCAudioCallListener = new TRTCAudioCallListener() {

        @Override
        public void onError(int code, String msg) {
            ToastUtils.showShort("onError");
            Log.e("Listener", "onError" + msg);
        }

        @Override
        public void onInvited(String sponsor, List<String> userIdList, boolean isFromGroup, int callType) {
            ToastUtils.showShort("被邀请通话回调。");
            Log.e("Listener", "onInvited");

            sCall.accept(); // 接听

        }

        @Override
        public void onGroupCallInviteeListUpdate(List<String> userIdList) {
            ToastUtils.showShort("群聊更新邀请列表回调。");
            Log.e("Listener", "onGroupCallInviteeListUpdate" + userIdList.size());
        }

        @Override
        public void onUserEnter(String userId) {
            ToastUtils.showShort("用户进入通话回调。");
            Log.e("Listener", "onUserEnter" + userId);


        }

        @Override
        public void onUserLeave(String userId) {
            ToastUtils.showShort("用户离开通话回调。");
            Log.e("Listener", "onUserLeave" + userId);

        }

        @Override
        public void onReject(String userId) {
            ToastUtils.showShort("拒绝通话回调。");
            Log.e("Listener", "onReject" + userId);

        }

        @Override
        public void onNoResp(String userId) {
            ToastUtils.showShort("对方无回应回调。");
            Log.e("Listener", "onNoResp" + userId);


        }

        @Override
        public void onLineBusy(String userId) {
            Log.e("Listener", "onLineBusy" + userId);

        }

        @Override
        public void onCallingCancel() {
            Log.e("Listener", "onCallingCancel当前通话被取消回调。");

        }

        @Override
        public void onCallingTimeout() {
            Log.e("Listener", "onCallingTimeout 当前通话超时回调。");

        }

        @Override
        public void onCallEnd() {
            Log.e("Listener", "onCallEnd 通话结束回调。");

        }

        @Override
        public void onUserAudioAvailable(String userId, boolean isVideoAvailable) {
            Log.e("Listener", "onUserAudioAvailable 用户是否开启音频上行回调。 " + userId + isVideoAvailable);

        }

        @Override
        public void onUserVoiceVolume(Map<String, Integer> volumeMap) {
            Log.e("Listener", "onUserVoiceVolume 用户通话音量回调。" + volumeMap);

        }
    };

    /////////// 语音

    private boolean isCall = false;
    private ImageView mIvSelect;

    private ZPlayer player;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BarUtils.setStatusBarColor(this, Color.TRANSPARENT);
        BarUtils.setStatusBarLightMode(this, true);
        getVoiceToken();

        intViewId();
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMvMain.onCreate(savedInstanceState);

        if (checkPermission()) {
            initMap();
            getVoiceToken();
        }

        initRecycler();
        initRecyclerAll();


        //////Socket
        mContext = MainActivity.this;
        //启动服务
        startJWebSClientService();
        //绑定服务
        bindService();
        //注册广播
        doRegisterReceiver();
//        thread.start();

        initLister();

        initPlayer();

//        player.setTitle("直播")//设置视频的titleName
//                .play("http://47.108.82.225:8080/cmcc/XZOCNBI3.flv?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0eXBlIjoiYWRtaW4iLCJhZG1pbl9pZCI6MSwidGltZSI6MTU5MzQxNzE4NywiZXhwaXJlIjpudWxsfQ.6xKp-FXccP5vvjMt8H5UCe0_fLE8cfHy1IQiPcW4h-4");//开始播放视频


    }


    @SuppressLint("ResourceAsColor")
    private void initPlayer() {
        Log.e("AAAAAAAAAAA", "AAAAAAAAAAAA");
        player.setLive(true)//设置该地址是直播的地址
                .setNetChangeListener(true)//设置监听手机网络的变化,这个参数是内部是否处理网络监听，和setOnNetChangeListener没有关系
                .setOnNetChangeListener(this)//实现网络变化的回调
                .setScaleType(ZPlayer.SCALETYPE_FITXY)//图片缩放方式
                .setPlayerWH(0, player.getMeasuredHeight())//设置竖屏的时候屏幕的高度，如果不设置会切换后按照16:9的高度重置
                .setAlwaysShowControl()  //设置则一直显示
                //准备好视频回调
                .onPrepared(() -> {
                    //TODO 监听视频是否已经准备完成开始播放。（可以在这里处理视频封面的显示跟隐藏）
                    ToastUtils.showShort("开始播放");
                    findViewById(R.id.rl_player_center).getBackground().setAlpha(0);
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

    private void initCall() {
        // 初始化
        sCall = TRTCAudioCallImpl.sharedInstance(this);
        sCall.init();
        //2. 注册监听器
        sCall.addListener(mTRTCAudioCallListener);
        Log.e("EEEEEEEEEEEEEE", "mAppId-A-" + mAppId + "-A-mUser_id-A-" + mUser_id + "-A-mUserSig-A-" + mUserSig);

        sCall.login(Integer.parseInt(mAppId), mUser_id, mUserSig,
//        sCall.login(1400385140, "456", "eJyrVgrxCdYrSy1SslIy0jNQ0gHzM1NS80oy0zLBwiamZlDh4pTsxIKCzBQlK0MTAwNjC1MgBZFJrSjILEoFipuamhoZGEBFSzJzwWKWxsamJkbGxlBTMtOBprr4mFY5hSdbuhlZRLiYJFdleVeUuxf6*hf4h2b5e1ZmJeeYRvo6l5t5ZhTbKtUCAE0gMBg_",
                new ITRTCAudioCall.ActionCallBack() {

                    @Override
                    public void onError(int code, String msg) {

                    }

                    @Override
                    public void onSuccess() {
                        ToastUtils.showShort("登录成功");
                        Log.e("EEEEEEEEEEEEEE", "mAppId-A-" + mAppId + "-A-mUser_id-A-" + mUser_id + "-A-mUserSig-A-" + mUserSig);
                        sCall.setMicMute(false);
                        sCall.setHandsFree(false);
                    }
                });

    }


    private void initLister() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                OnlineBean.Data.UserList userList = mAdapter.getData().get(position);
                mSolider_id = userList.getUser_id();
                String flv_url = userList.getFlv_url();
                String username = userList.getUsername();
//                ToastUtils.showShort("直播开始");
                mLlCallList.setVisibility(View.GONE);
                mLlSoldierList.setVisibility(View.GONE);
                player.setTitle(username)//设置视频的titleName
                        .play(flv_url);//开始播放视频
                Log.e("AAAAAAAAAAA", "BBBBBBBBBBBB");
                mLlBottom.setVisibility(View.VISIBLE);


            }
        });

        mCallAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                if (!isCall) {
                    boolean select = mCallAdapter.getData().get(position).isSelect();
                    mIvSelect = view.findViewById(R.id.iv_select);
//                    ToastUtils.showShort("点击" + select);

                    if (!select) {
                        mIvSelect.setImageResource(R.mipmap.ic_select);
                        mCallAdapter.getData().get(position).setSelect(true);
                    } else {
                        mIvSelect.setImageResource(R.drawable.shape_unselect_circle);
                        mCallAdapter.getData().get(position).setSelect(false);
                    }
                } else {
                    ToastUtils.showShort("请先结束当前通话");
                }


//                isEdit = !isEdit;
//                mCallAdapter.changeSelectImage(isEdit);

//                ToastUtils.showShort(mCallAdapter.getData().get(position).isSelect() + "");
//
            }
        });
    }

    /**
     * 动态注册广播
     */
    private void doRegisterReceiver() {
        chatMessageReceiver = new ChatMessageReceiver();
        IntentFilter filter = new IntentFilter("com.xch.servicecallback.content");
        registerReceiver(chatMessageReceiver, filter);
    }

    /**
     * 绑定服务
     */
    private void bindService() {
        Intent bindIntent = new Intent(mContext, JWebSocketClientService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 启动服务（websocket客户端服务）
     */
    private void startJWebSClientService() {
        Intent intent = new Intent(mContext, JWebSocketClientService.class);
        startService(intent);
    }

    private void getData() {

    }

    private void initRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerSoldier.setLayoutManager(layoutManager);
//        mRecyclerCall.setLayoutManager(layoutManager);
        mLists = new ArrayList<>();
        mAdapter = new SoliderAdapter(mLists);

        View emptyView = getLayoutInflater().inflate(R.layout.item_empty, null);
        mAdapter.setEmptyView(emptyView);
        mRecyclerSoldier.setAdapter(mAdapter);
    }

    private void initRecyclerAll() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerCall.setLayoutManager(layoutManager);
//        mRecyclerCall.setLayoutManager(layoutManager);
        mCallLists = new ArrayList<>();
        mCallAdapter = new CallAdapter(mCallLists);

        View emptyView = getLayoutInflater().inflate(R.layout.item_empty, null);
        mCallAdapter.setEmptyView(emptyView);
        mRecyclerCall.setAdapter(mCallAdapter);
    }

    private void initMap() {
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mMvMain.getMap();

            setLocation();

            LatLng latLng = new LatLng(39.906901, 116.397972);

            drawLatLng(latLng, "BJ");
        }
        //蓝点
//        MyLocationStyle myLocationStyle;
//        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
//        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
//        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
//        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
//        myLocationStyle.showMyLocation(true);
//        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
//            @Override
//            public void onMyLocationChange(Location location) {
//                Log.e("gps", location.getLatitude() + "--" + location.getLongitude());
//            }
//        });
    }

    private void setLocation() {
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.showMyLocation(true);
//            myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
//            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        aMap.moveCamera(CameraUpdateFactory.zoomTo(14)); // 放缩级别
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Log.e("dingwei", location.getLatitude() + "--" + location.getLongitude());
            }
        });
    }


    private void drawLatLng(LatLng latLng, String title) {
        MarkerOptions options = new MarkerOptions()
                //自定义图标 不添加为蓝色点点
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_location)))
                //在地图上标记位置的经纬度值。必填参数
                .position(latLng)
                //点标记的标题
                .title(title)
                //点标记的内容
                .snippet("DefaultMarker")
                //点标记是否可拖拽
                .draggable(true)
                //点标记是否可见
                .visible(true)
                //点标记的透明度
                .alpha(1.0f)
                //设置marker平贴地图效果
                .setFlat(false);
//        aMap.addMarker(options);
    }

    private boolean isSelectAll = true;

    private void intViewId() {


        mMvMain = findViewById(R.id.mv_main);

        mRlTop = findViewById(R.id.rl_top);
        mRlTop.bringToFront();

        mLlBr = findViewById(R.id.ll_br);
        mLlBr.bringToFront();

        mLlCallList = findViewById(R.id.ll_call_list);
        mLlCallList.bringToFront();
        mLlCallList.setVisibility(View.GONE);

        mLlSoldierList = findViewById(R.id.ll_soldier_list);
        mLlSoldierList.bringToFront();
        mLlSoldierList.setVisibility(View.GONE);


        mRecyclerSoldier = findViewById(R.id.recycler_soldier);
        mRecyclerCall = findViewById(R.id.recycler_call);

        player = findViewById(R.id.view_super_player);
        player.bringToFront();
        mLlBottom = findViewById(R.id.ll_bottom);
        mLlBottom.bringToFront();
        mLlBottom.setVisibility(View.GONE);


        mIvPhone = findViewById(R.id.iv_phone);

        findViewById(R.id.rl_phone).bringToFront();

        findViewById(R.id.rl_down).bringToFront();

        mTvNum = findViewById(R.id.tv_num);


        mTvSelectAll = findViewById(R.id.tv_select_all);

        mTvSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSelectAll) {
                    mCallAdapter.changeSelectImage(true);
                    isSelectAll = false;
                } else {
                    mCallAdapter.changeSelectImage(false);
                    isSelectAll = true;
                }


//                mIvSelect.setImageResource(R.mipmap.ic_select);
//                mCallAdapter.getData().get(position).setSelect(true);
            }
        });


        mTvDown = findViewById(R.id.tv_down);
        mTvDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLlBottom.getVisibility() == View.VISIBLE) {
                    mLlBottom.setVisibility(View.GONE);
                    player.stop();
                }
            }
        });


        mIvPhoneAll = findViewById(R.id.iv_phone_all);

        findViewById(R.id.rl_call_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCall) {
                    sCall.hangup();
                    isCall = false;
                    mIvPhoneAll.setImageResource(R.mipmap.img_call_all);
                }
            }
        });


        findViewById(R.id.rl_call_all).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                ToastUtils.showLong("长按");

                List<OnlineBean.Data.UserList> data = mCallAdapter.getData();

                if (data.size() > 0) {

                    List<String> callList = new ArrayList();
                    for (int i = 0; i < data.size(); i++) {
                        String user_id = data.get(i).getUser_id();
                        callList.add(user_id);
                    }

                    if (!isCall) {
                        sCall.groupCall(callList, "");
                        isCall = true;
                        mIvPhoneAll.setImageResource(R.mipmap.ic_hang_up);
                    } else {
//                        sCall.hangup();
//                        isCall = false;
//                        mIvPhoneAll.setImageResource(R.mipmap.img_call_all);
                    }


                } else {
                    sCall.hangup();
                    isCall = false;
                    mIvPhoneAll.setImageResource(R.mipmap.img_call_all);
                    ToastUtils.showShort("暂无设备在线");
                }

                return true;
            }
        });

        findViewById(R.id.rl_call_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLlCallList.getVisibility() == View.GONE) {
                    mLlCallList.setVisibility(View.VISIBLE);
                    mLlSoldierList.setVisibility(View.GONE);
                } else if (mLlCallList.getVisibility() == View.VISIBLE) {
                    mLlCallList.setVisibility(View.GONE);
                    mLlSoldierList.setVisibility(View.GONE);
                }
            }
        });

        findViewById(R.id.rl_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ActivityUtils.startActivity(MapDemoActivity.class);

                if (mLlSoldierList.getVisibility() == View.GONE) {
                    mLlSoldierList.setVisibility(View.VISIBLE);
                    mLlCallList.setVisibility(View.GONE);
                } else if (mLlSoldierList.getVisibility() == View.VISIBLE) {
                    mLlSoldierList.setVisibility(View.GONE);
                    mLlCallList.setVisibility(View.GONE);
                }
            }
        });

        findViewById(R.id.rl_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkOff) {
                    sCall.call(mSolider_id);
                    mIvPhone.setImageResource(R.mipmap.ic_hang_up);
                    checkOff = !checkOff;
                } else {
                    sCall.hangup();
                    mIvPhone.setImageResource(R.mipmap.ic_phone);
                    checkOff = !checkOff;
                }


            }
        });

        mTvCallAll = findViewById(R.id.tv_call_all);
        mTvCallAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callAll();

            }

            private void callAll() {
                List<OnlineBean.Data.UserList> data = mCallAdapter.getData();
                List<OnlineBean.Data.UserList> newList = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).isSelect()) {
                        newList.add(data.get(i));
                    }
                }

                if (newList.size() > 0) {
                    List<String> callList = new ArrayList();
                    for (int i = 0; i < newList.size(); i++) {
                        String user_id = newList.get(i).getUser_id();
                        callList.add(user_id);
                    }

                    if (!isCall) {
                        sCall.groupCall(callList, "");
                        isCall = true;
                        mTvCallAll.setText("结束通话");
                        mTvCallAll.setBackgroundResource(R.drawable.shape_red_2);
                    } else {
                        sCall.hangup();
                        isCall = false;
                        mTvCallAll.setText("立即进入");
                        mTvCallAll.setBackgroundResource(R.drawable.shape_green_2);
                    }


                } else {
                    sCall.hangup();
                    isCall = false;
                    mTvCallAll.setText("立即进入");
                    mTvCallAll.setBackgroundResource(R.drawable.shape_green_2);
                    ToastUtils.showShort("暂无设备在线");
                }


            }
        });

        mIvClose = findViewById(R.id.iv_close);
        mIvCloseAll = findViewById(R.id.iv_close_all);
//        mIvClose.bringToFront();
//        mIvCloseAll.bringToFront();

        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlSoldierList.setVisibility(View.GONE);
                mLlCallList.setVisibility(View.GONE);
            }
        });
        mIvCloseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlSoldierList.setVisibility(View.GONE);
                mLlCallList.setVisibility(View.GONE);
            }
        });
    }

    private void getVoiceToken() {


        HashMap<String, Object> params = new HashMap<>();
//        params.put("device_id", device_sn);
//        params.put("type", "publisher");
        Api.getRetrofit().getToken(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<TokenBean>(this) {

                    @Override
                    public void onStart() {
                        super.onStart();
//                        recyclerView.refreshComplete();
                        Log.e("TAGPZR", "onStart");
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
//                        recyclerView.refreshComplete();
                        Log.e("TAGPZR", "onCompleted");

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
//                        showRec(false);
//                        recyclerView.refreshComplete();
                        Log.e("TAGPZR", "onError" + e.toString());

                    }

                    @Override
                    public void onNext(TokenBean bean) {
                        super.onNext(bean);

                        mUser_id = bean.getData().getUser_id();
                        mAppId = bean.getData().getAppId();
                        mUserSig = bean.getData().getUserSig();
                        mFlv_url = bean.getData().getFlv_url();
                        Log.e("VOICE", "U" + mUser_id + mAppId + mUserSig);

                        initCall();

//                        String appid = bean.getData().getAppid();
//                        String nonce = bean.getData().getNonce();
//                        String gslb = bean.getData().getGslb().get(0).toString();
//                        int timestamp = bean.getData().getTimestamp();
//                        String token = bean.getData().getToken();
//                        String channel = bean.getData().getChannel();
//                        String userid = bean.getData().getUserid();

//                        ToastUtils.showLong(channel);

//                        joinChannel(appid, nonce, gslb, timestamp, token, channel, userid);
                    }
                });

    }

    private boolean checkPermission() {

        /**
         * Manifest.permission.ACCESS_COARSE_LOCATION,
         * 					Manifest.permission.ACCESS_FINE_LOCATION,
         * 					Manifest.permission.WRITE_EXTERNAL_STORAGE,
         * 					Manifest.permission.READ_EXTERNAL_STORAGE,
         * 					Manifest.permission.READ_PHONE_STATE,
         * 					BACK_LOCATION_PERMISSION
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        permissions.toArray(new String[0]),
                        REQ_PERMISSION_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSION_CODE) {
            for (int ret : grantResults) {
                if (PackageManager.PERMISSION_GRANTED == ret) mGrantedCount++;
            }
            if (mGrantedCount == permissions.length) {
                getVoiceToken();
                initMap();
                //首次启动，权限都获取到，才能正常进入通话  初始化
//                initCall(); //首次启动，权限都获取到，才能正常进入通话
            } else {
                Toast.makeText(this, getString(R.string.rtc_permisson_error_tip), Toast.LENGTH_SHORT).show();
            }
            mGrantedCount = 0;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMvMain.onDestroy();

        unbindService(serviceConnection);

        unregisterReceiver(chatMessageReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMvMain.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMvMain.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMvMain.onSaveInstanceState(outState);
    }
}
