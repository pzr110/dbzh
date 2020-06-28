package com.jlf.dbzh.bean;

public class SoliderBean {
    private String user_id;
    private String username;
    private int power;
    private double lng;
    private double lat;
    private String role;
    private String flv_url;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFlv_url() {
        return flv_url;
    }

    public void setFlv_url(String flv_url) {
        this.flv_url = flv_url;
    }
}
