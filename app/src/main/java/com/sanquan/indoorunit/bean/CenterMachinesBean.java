package com.sanquan.indoorunit.bean;

import java.util.List;

/**
 * 作者：zyf on 2019/4/26.
 * 用途：TODO
 */
public class CenterMachinesBean {

    @Override
    public String toString() {
        return "CenterMachinesBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", center_list=" + center_list +
                '}';
    }

    /**
     * code : 0
     * msg : success
     * center_list : [{"door_id":502,"door_name":"xxx","door_device_id":"xxxxxxxxxxxx","door_community_id":57,"door_time":1552986557,"door_mqtt_topic":"/61/57/xxxxxxxxxxxx/","last_heart_time":"0","door_yzx_token":"eyJBbGciOiJ","door_version":"","door_accessToken":""}]
     */

    private int code;
    private String msg;
    private List<CenterListBean> center_list;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<CenterListBean> getCenter_list() {
        return center_list;
    }

    public void setCenter_list(List<CenterListBean> center_list) {
        this.center_list = center_list;
    }

    public static class CenterListBean {
        @Override
        public String toString() {
            return "CenterListBean{" +
                    "door_id=" + door_id +
                    ", door_name='" + door_name + '\'' +
                    ", door_device_id='" + door_device_id + '\'' +
                    ", door_community_id=" + door_community_id +
                    ", door_time=" + door_time +
                    ", door_mqtt_topic='" + door_mqtt_topic + '\'' +
                    ", last_heart_time='" + last_heart_time + '\'' +
                    ", door_yzx_token='" + door_yzx_token + '\'' +
                    ", door_version='" + door_version + '\'' +
                    ", door_accessToken='" + door_accessToken + '\'' +
                    '}';
        }

        /**
         * door_id : 502
         * door_name : xxx
         * door_device_id : xxxxxxxxxxxx
         * door_community_id : 57
         * door_time : 1552986557
         * door_mqtt_topic : /61/57/xxxxxxxxxxxx/
         * last_heart_time : 0
         * door_yzx_token : eyJBbGciOiJ
         * door_version :
         * door_accessToken :
         */



        private int door_id;
        private String door_name;
        private String door_device_id;
        private int door_community_id;
        private String door_time;
        private String door_mqtt_topic;
        private String last_heart_time;
        private String door_yzx_token;
        private String door_version;
        private String door_accessToken;

        public int getDoor_id() {
            return door_id;
        }

        public void setDoor_id(int door_id) {
            this.door_id = door_id;
        }

        public String getDoor_name() {
            return door_name;
        }

        public void setDoor_name(String door_name) {
            this.door_name = door_name;
        }

        public String getDoor_device_id() {
            return door_device_id;
        }

        public void setDoor_device_id(String door_device_id) {
            this.door_device_id = door_device_id;
        }

        public int getDoor_community_id() {
            return door_community_id;
        }

        public void setDoor_community_id(int door_community_id) {
            this.door_community_id = door_community_id;
        }

        public String getDoor_time() {
            return door_time;
        }

        public void setDoor_time(String door_time) {
            this.door_time = door_time;
        }

        public String getDoor_mqtt_topic() {
            return door_mqtt_topic;
        }

        public void setDoor_mqtt_topic(String door_mqtt_topic) {
            this.door_mqtt_topic = door_mqtt_topic;
        }

        public String getLast_heart_time() {
            return last_heart_time;
        }

        public void setLast_heart_time(String last_heart_time) {
            this.last_heart_time = last_heart_time;
        }

        public String getDoor_yzx_token() {
            return door_yzx_token;
        }

        public void setDoor_yzx_token(String door_yzx_token) {
            this.door_yzx_token = door_yzx_token;
        }

        public String getDoor_version() {
            return door_version;
        }

        public void setDoor_version(String door_version) {
            this.door_version = door_version;
        }

        public String getDoor_accessToken() {
            return door_accessToken;
        }

        public void setDoor_accessToken(String door_accessToken) {
            this.door_accessToken = door_accessToken;
        }
    }
}
