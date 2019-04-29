package com.sanquan.indoorunit.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：zyf on 2018/8/31.
 * 用途：TODO
 */
public class FeedbackBean implements Serializable{


    @Override
    public String toString() {
        return "FeedbackBean{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", res=" + res +
                '}';
    }

    /**
     * msg : success
     * code : 0
     * res : [{"event_id":58,"event_content":"恩琪","event_img":["localhost/uploads/feedbackImage/20180831/53886c5ffd45b2b5f80b94ed13827ad0.jpg"],"event_msg":null,"event_step":"未处理","event_type":"房屋报修","event_create_time":"2018-08-31 09:42:23"},{"event_id":59,"event_content":"恩琪","event_img":["localhost/uploads/feedbackImage/20180831/3acaa3bf6603324104d25c544e69d47d.jpg"],"event_msg":null,"event_step":"未处理","event_type":"房屋报修","event_create_time":"2018-08-31 09:48:43"},{"event_id":64,"event_content":"测试","event_img":["localhost/uploads/feedbackImage/20180831/3593797fdc51b6ef210212ed72d41ba5.jpg"],"event_msg":null,"event_step":"未处理","event_type":"房屋报修","event_create_time":"2018-08-31 05:04:25"},{"event_id":66,"event_content":"测试","event_img":["localhost/uploads/feedbackImage/20180831/077ea6b733b2d59d4a62b4b73e98dc92.jpg","localhost/uploads/feedbackImage/20180831/e855923e8fbf7d8821d403d667369e3c.jpg","localhost/uploads/feedbackImage/20180831/9c06bc153a684f3ba5a1e037be4f5758.jpg"],"event_msg":null,"event_step":"未处理","event_type":"房屋报修","event_create_time":"2018-08-31 05:20:53"}]
     */


    private String msg;
    private int code;
    private ArrayList<ResBean> res;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<ResBean> getRes() {
        return res;
    }

    public void setRes(ArrayList<ResBean> res) {
        this.res = res;
    }

    public static class ResBean implements Serializable{
        @Override
        public String toString() {
            return "ResBean{" +
                    "event_id=" + event_id +
                    ", event_content='" + event_content + '\'' +
                    ", event_msg='" + event_msg + '\'' +
                    ", event_step='" + event_step + '\'' +
                    ", event_type='" + event_type + '\'' +
                    ", event_create_time='" + event_create_time + '\'' +
                    ", event_img=" + event_img +
                    '}';
        }

        /**
         * event_id : 58
         * event_content : 恩琪
         * event_img : ["localhost/uploads/feedbackImage/20180831/53886c5ffd45b2b5f80b94ed13827ad0.jpg"]
         * event_msg : null
         * event_step : 未处理
         * event_type : 房屋报修
         * event_create_time : 2018-08-31 09:42:23
         */



        private int event_id;
        private String event_content;
        private String event_msg;
        private String event_step;
        private String event_type;
        private String event_create_time;
        private ArrayList<String> event_img;

        public int getEvent_id() {
            return event_id;
        }

        public void setEvent_id(int event_id) {
            this.event_id = event_id;
        }

        public String getEvent_content() {
            return event_content;
        }

        public void setEvent_content(String event_content) {
            this.event_content = event_content;
        }

        public String getEvent_msg() {
            return event_msg;
        }

        public void setEvent_msg(String event_msg) {
            this.event_msg = event_msg;
        }

        public String getEvent_step() {
            return event_step;
        }

        public void setEvent_step(String event_step) {
            this.event_step = event_step;
        }

        public String getEvent_type() {
            return event_type;
        }

        public void setEvent_type(String event_type) {
            this.event_type = event_type;
        }

        public String getEvent_create_time() {
            return event_create_time;
        }

        public void setEvent_create_time(String event_create_time) {
            this.event_create_time = event_create_time;
        }

        public ArrayList<String> getEvent_img() {
            return event_img;
        }

        public void setEvent_img(ArrayList<String> event_img) {
            this.event_img = event_img;
        }
    }
}
