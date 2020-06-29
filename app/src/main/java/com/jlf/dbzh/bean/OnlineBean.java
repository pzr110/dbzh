/**
 * Copyright 2020 bejson.com
 */
package com.jlf.dbzh.bean;

import java.util.List;

/**
 * Auto-generated: 2020-06-28 15:10:39
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class OnlineBean {

    private String type;
    private Data data;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public static class Data {

        private List<UserList> userList;

        private UserInfo userinfo;

        private String user_id;
        private String power;
        private Position position;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getPower() {
            return power;
        }

        public void setPower(String power) {
            this.power = power;
        }

        public Position getPosition() {
            return position;
        }

        public void setPosition(Position position) {
            this.position = position;
        }

        public UserInfo getUserinfo() {
            return userinfo;
        }

        public void setUserinfo(UserInfo userinfo) {
            this.userinfo = userinfo;
        }

        public void setUserList(List<UserList> userList) {
            this.userList = userList;
        }

        public List<UserList> getUserList() {
            return userList;
        }

        public static class Position{
            private String lng;
            private String lat;

            public String getLng() {
                return lng;
            }

            public void setLng(String lng) {
                this.lng = lng;
            }

            public String getLat() {
                return lat;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }
        }

        public static class UserInfo{
            private String  user_id;
            private String  username;
            private int  power;
            private double  lng;
            private double  lat;
            private String  role;
            private String  flv_url;

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


        public static class UserList {

            private String user_id;
            private String username;
            private int power;
            private double lng;
            private double lat;
            private String role;
            private String flv_url;

            private boolean isSelect;

            public boolean isSelect() {
                return isSelect;
            }

            public void setSelect(boolean select) {
                isSelect = select;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getUser_id() {
                return user_id;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getUsername() {
                return username;
            }

            public void setPower(int power) {
                this.power = power;
            }

            public int getPower() {
                return power;
            }

            public void setLng(double lng) {
                this.lng = lng;
            }

            public double getLng() {
                return lng;
            }

            public void setLat(double lat) {
                this.lat = lat;
            }

            public double getLat() {
                return lat;
            }

            public void setRole(String role) {
                this.role = role;
            }

            public String getRole() {
                return role;
            }

            public void setFlv_url(String flv_url) {
                this.flv_url = flv_url;
            }

            public String getFlv_url() {
                return flv_url;
            }

        }

    }

}
