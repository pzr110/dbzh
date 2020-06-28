package com.jlf.dbzh.bean;

import java.util.List;

public class TokenData {
    private String userid;
    private String user_id;

    private String channel;

    private String appid;
    private String appId;

    private String nonce;
    private String userSig;

    private int timestamp;

    private List<String> gslb;

    private String token;
    private String flv_url;

    public String getFlv_url() {
        return flv_url;
    }

    public void setFlv_url(String flv_url) {
        this.flv_url = flv_url;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUserSig() {
        return userSig;
    }

    public void setUserSig(String userSig) {
        this.userSig = userSig;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return this.channel;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppid() {
        return this.appid;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getNonce() {
        return this.nonce;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getTimestamp() {
        return this.timestamp;
    }

    public void setGslb(List<String> gslb) {
        this.gslb = gslb;
    }

    public List<String> getGslb() {
        return this.gslb;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
