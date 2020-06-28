package com.jlf.dbzh.bean;

public class JoinBean {
    /**
     * {
     * "type": "join",
     * "data": {
     * "userinfo": {
     * "user_id": "1",
     * "username": "username",
     * "power": 50,
     * "lng": 30.213213,
     * "lat": 123.123213,
     * "role": "leader",
     * "flv_url": "http://"
     * }
     * }
     * }
     */

    private String type;
    private Data data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private UserInfo userinfo;

        public UserInfo getUserinfo() {
            return userinfo;
        }

        public void setUserinfo(UserInfo userinfo) {
            this.userinfo = userinfo;
        }

        public static class UserInfo {
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
    }
}
