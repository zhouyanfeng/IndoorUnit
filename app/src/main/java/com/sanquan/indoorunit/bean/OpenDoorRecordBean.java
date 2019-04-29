package com.sanquan.indoorunit.bean;

import java.util.List;

/**
 * 作者：zyf on 2018/8/4.
 * 用途：记录开门信息的bean
 */
public class OpenDoorRecordBean {

    /**
     * code : 0
     * msg : success
     * unlock_log : [{"dev_id":" ","door_name":" ","unlock_time":" ","access_mode":" ","Face_url":" "}]
     */

    private int code;
    private String msg;
    private List<UnlockLogBean> unlock_log;

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

    public List<UnlockLogBean> getUnlock_log() {
        return unlock_log;
    }

    public void setUnlock_log(List<UnlockLogBean> unlock_log) {
        this.unlock_log = unlock_log;
    }

    public static class UnlockLogBean {
        @Override
        public String toString() {
            return "UnlockLogBean{" +
                    "dev_id='" + dev_id + '\'' +
                    ", door_name='" + door_name + '\'' +
                    ", unlock_time='" + unlock_time + '\'' +
                    ", access_mode='" + access_mode + '\'' +
                    ", Face_url='" + face_url + '\'' +
                    '}';
        }

        /**
         * dev_id :
         * door_name :
         * unlock_time :
         * access_mode :
         * Face_url :
         */



        private String dev_id;
        private String door_name;
        private String unlock_time;
        private String access_mode;
        private String face_url;

        public String getDev_id() {
            return dev_id;
        }

        public void setDev_id(String dev_id) {
            this.dev_id = dev_id;
        }

        public String getDoor_name() {
            return door_name;
        }

        public void setDoor_name(String door_name) {
            this.door_name = door_name;
        }

        public String getUnlock_time() {
            return unlock_time;
        }

        public void setUnlock_time(String unlock_time) {
            this.unlock_time = unlock_time;
        }

        public String getAccess_mode() {
            return access_mode;
        }

        public void setAccess_mode(String access_mode) {
            this.access_mode = access_mode;
        }

        public String getFace_url() {
            return face_url;
        }

        public void setFace_url(String face_url) {
            this.face_url = face_url;
        }
    }

    @Override
    public String toString() {
        return "OpenDoorRecordBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", unlock_log=" + unlock_log +
                '}';
    }
}
