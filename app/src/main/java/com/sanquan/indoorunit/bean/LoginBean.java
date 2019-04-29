package com.sanquan.indoorunit.bean;

/**
 * 作者：zyf on 2018/10/9.
 * 用途：TODO
 */
public class LoginBean {


    /**
     * code : 0
     * msg : success
     * info : {"operator_id":61,"operator_name":"三甲","community_id":57,"community_name":"三全视讯","id":371,"name":"三全视讯03幢00单元1层2号室内机测试","area_id":659,"building_id":381,"mqtt_server":"tcp://139.159.239.174:61883","mqtt_topic":"/61/57/b3eabfdb42356K5s/get","yzx_token":"eyJBbGciOiJIUzI1NiIsIkFjY2lkIjoiNmJlYmI5NTQ1Y2IyNDA2NmFlM2ZlYWU4OWJhZGUxZjMiLCJBcHBpZCI6IjRjNjY3ZWM5MWYzODQ1YWY4Y2JiNzQ4NTFkNzY5YjlkIiwiVXNlcmlkIjoic3FzeDAwMDAwMSJ9.FpdSz145MhvyF+z3hXgEZe9llRYB42yabnnKzqcNEO4=","face_threshold":"0.85","upload_face":false,"ipc_stream_url":"","device_secret":"","recognize_interval":5,"area_path":"314,537","room_name":"102"}
     */

    private int code;
    private String msg;
    private InfoBean info;

    @Override
    public String toString() {
        return "LoginBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", info=" + info +
                '}';
    }

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

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public static class InfoBean {


        @Override
        public String toString() {
            return "InfoBean{" +
                    "operator_id=" + operator_id +
                    ", operator_name='" + operator_name + '\'' +
                    ", community_id=" + community_id +
                    ", community_name='" + community_name + '\'' +
                    ", floor='" + floor + '\'' +
                    ", room='" + room + '\'' +
                    ", building_name='" + building_name + '\'' +
                    ", id=" + id +
                    ", name='" + name + '\'' +
                    ", area_id=" + area_id +
                    ", building_id=" + building_id +
                    ", mqtt_server='" + mqtt_server + '\'' +
                    ", mqtt_topic='" + mqtt_topic + '\'' +
                    ", yzx_token='" + yzx_token + '\'' +
                    ", face_threshold='" + face_threshold + '\'' +
                    ", upload_face=" + upload_face +
                    ", ipc_stream_url='" + ipc_stream_url + '\'' +
                    ", device_secret='" + device_secret + '\'' +
                    ", recognize_interval=" + recognize_interval +
                    ", area_path='" + area_path + '\'' +
                    ", room_name='" + room_name + '\'' +
                    '}';
        }

        /**
         * operator_id : 61
         * operator_name : 三甲
         * community_id : 57
         * community_name : 三全视讯
         * id : 371
         * name : 三全视讯03幢00单元1层2号室内机测试
         * area_id : 659
         * building_id : 381
         * mqtt_server : tcp://139.159.239.174:61883
         * mqtt_topic : /61/57/b3eabfdb42356K5s/get
         * yzx_token : eyJBbGciOiJIUzI1NiIsIkFjY2lkIjoiNmJlYmI5NTQ1Y2IyNDA2NmFlM2ZlYWU4OWJhZGUxZjMiLCJBcHBpZCI6IjRjNjY3ZWM5MWYzODQ1YWY4Y2JiNzQ4NTFkNzY5YjlkIiwiVXNlcmlkIjoic3FzeDAwMDAwMSJ9.FpdSz145MhvyF+z3hXgEZe9llRYB42yabnnKzqcNEO4=
         * face_threshold : 0.85
         * upload_face : false
         * ipc_stream_url :
         * device_secret :
         * recognize_interval : 5
         * area_path : 314,537
         * room_name : 102
         */



        private int operator_id;
        private String operator_name;
        private int community_id;
        private String community_name;
        private String floor;
        private String room;
        private String building_name;
        private int id;
        private String name;
        private int area_id;
        private int building_id;
        private String mqtt_server;
        private String mqtt_topic;
        private String yzx_token;
        private String face_threshold;
        private boolean upload_face;
        private String ipc_stream_url;
        private String device_secret;
        private int recognize_interval;
        private String area_path;
        private String room_name;

        public String getFloor() {
            return floor;
        }

        public void setFloor(String floor) {
            this.floor = floor;
        }

        public String getRoom() {
            return room;
        }

        public void setRoom(String room) {
            this.room = room;
        }

        public String getBuilding_name() {
            return building_name;
        }

        public void setBuilding_name(String building_name) {
            this.building_name = building_name;
        }

        public int getOperator_id() {
            return operator_id;
        }

        public void setOperator_id(int operator_id) {
            this.operator_id = operator_id;
        }

        public String getOperator_name() {
            return operator_name;
        }

        public void setOperator_name(String operator_name) {
            this.operator_name = operator_name;
        }

        public int getCommunity_id() {
            return community_id;
        }

        public void setCommunity_id(int community_id) {
            this.community_id = community_id;
        }

        public String getCommunity_name() {
            return community_name;
        }

        public void setCommunity_name(String community_name) {
            this.community_name = community_name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getArea_id() {
            return area_id;
        }

        public void setArea_id(int area_id) {
            this.area_id = area_id;
        }

        public int getBuilding_id() {
            return building_id;
        }

        public void setBuilding_id(int building_id) {
            this.building_id = building_id;
        }

        public String getMqtt_server() {
            return mqtt_server;
        }

        public void setMqtt_server(String mqtt_server) {
            this.mqtt_server = mqtt_server;
        }

        public String getMqtt_topic() {
            return mqtt_topic;
        }

        public void setMqtt_topic(String mqtt_topic) {
            this.mqtt_topic = mqtt_topic;
        }

        public String getYzx_token() {
            return yzx_token;
        }

        public void setYzx_token(String yzx_token) {
            this.yzx_token = yzx_token;
        }

        public String getFace_threshold() {
            return face_threshold;
        }

        public void setFace_threshold(String face_threshold) {
            this.face_threshold = face_threshold;
        }

        public boolean isUpload_face() {
            return upload_face;
        }

        public void setUpload_face(boolean upload_face) {
            this.upload_face = upload_face;
        }

        public String getIpc_stream_url() {
            return ipc_stream_url;
        }

        public void setIpc_stream_url(String ipc_stream_url) {
            this.ipc_stream_url = ipc_stream_url;
        }

        public String getDevice_secret() {
            return device_secret;
        }

        public void setDevice_secret(String device_secret) {
            this.device_secret = device_secret;
        }

        public int getRecognize_interval() {
            return recognize_interval;
        }

        public void setRecognize_interval(int recognize_interval) {
            this.recognize_interval = recognize_interval;
        }

        public String getArea_path() {
            return area_path;
        }

        public void setArea_path(String area_path) {
            this.area_path = area_path;
        }

        public String getRoom_name() {
            return room_name;
        }

        public void setRoom_name(String room_name) {
            this.room_name = room_name;
        }
    }
}
