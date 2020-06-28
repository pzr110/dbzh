package com.jlf.dbzh.ui;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.jlf.dbzh.MainActivity;
import com.jlf.dbzh.R;
import com.jlf.dbzh.bean.BaseBean;
import com.jlf.dbzh.bean.User;
import com.jlf.dbzh.utils.JsonParser;
import com.jlf.dbzh.utils.L;
import com.jlf.dbzh.utils.MD5;
import com.jlf.dbzh.utils.TokenUtils;
import com.jlf.dbzh.utils.net.Api;
import com.jlf.dbzh.utils.net.BaseSubscriber;

import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends BaseActivity {
    private EditText mEtAccount;
    private EditText mEtPass;
    private TextView mTvSubmit;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViewId() {
        mEtAccount = findViewById(R.id.et_account);
        mEtPass = findViewById(R.id.et_pass);
        mTvSubmit = findViewById(R.id.tv_submit);
        mTvSubmit.setOnClickListener(this);

    }

    @Override
    protected void loadData() {

        boolean isLogin = SPUtils.getInstance().getBoolean("IsLogin");
        if (isLogin){
            ActivityUtils.startActivity(MainActivity.class);
            finish();
        }

    }

    @Override
    public void widgetClick(View view) {
        if (view.getId()==R.id.tv_submit){
            if (mEtAccount.getText().toString().equals("")) {
                ToastUtils.showShort("请输入账号");
            } else if (mEtPass.getText().toString().equals("")) {
                ToastUtils.showShort("请输入密码");
            } else {
                postLogin();
            }
        }
    }

    private void postLogin() {
        //执行登录
        HashMap<String, Object> params = new HashMap<>();
        params.put("username", mEtAccount.getText().toString());
        params.put("password", MD5.md5Decode32(mEtPass.getText().toString()));
        Api.getRetrofit().login(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseBean<User>>(this) {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Login", "Error:" + e.toString());
                        super.onError(e);
                    }

                    @Override
                    public void onNext(BaseBean<User> userBaseBean) {
                        super.onNext(userBaseBean);
                        L.e("onNext");
                        String user = JsonParser.serializeToJson(userBaseBean.getData());
                        Log.e("TokenTag", "TAG" + userBaseBean.getData().getUserToken());


                        TokenUtils.setToken(userBaseBean.getData().getUserToken());
                        TokenUtils.setUserId(Integer.toString(userBaseBean.getData().getUserInfo().getId()));
                        TokenUtils.setUser(user);
                        User user1 = JsonParser.deserializeByJson(TokenUtils.getUser(), User.class);

                        String username = userBaseBean.getData().getUserInfo().getUsername();
                        String userpass = userBaseBean.getData().getUserInfo().getPassword();
                        SPUtils.getInstance().put("IsLogin", true);
                        SPUtils.getInstance().put("username", username);
                        SPUtils.getInstance().put("userpass", userpass);


                        //进入下一个页面
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("type", 1);
                        startActivity(intent);
                        finish();
                    }
                });

    }
}
