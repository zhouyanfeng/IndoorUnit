package com.sanquan.indoorunit.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：zyf on 2018/8/9.
 * 用途：首页信息bean
 */
public class AppHomeInfoBean implements Serializable{


    /**
     * code : 0
     * msg : success
     * notice : [{"date_time":"2018-08-08 15:07:51","title":"aaaa","owner":"筐衰骆","content":"bbbbbb"},{"date_time":"2018-08-08 15:22:18","title":"按实际滴哦我怕看谁都怕看到我爬进D","owner":"筐衰骆","content":"阿斯加德挖迷彩VOA哦死【订机票奥斯卡级大神看打破好贵哦萨省点钱卡牌【啊手机掉几个就爱睡觉【健康噶是奇偶今年io"}]
     * banner : ["/home/web/www/public/uploads/banner/20180808\\b617c50f7763fc444b0bcbbad729fccf.bmp"]
     */

    private int code;
    private String msg;
    private List<NoticeBean> notice;
    private List<Banner> banner;

    @Override
    public String toString() {
        return "AppHomeInfoBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", notice=" + notice +
                ", banner=" + banner +
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

    public List<NoticeBean> getNotice() {
        return notice;
    }

    public void setNotice(List<NoticeBean> notice) {
        this.notice = notice;
    }

    public List<Banner> getBanner() {
        return banner;
    }

    public void setBanner(List<Banner> banner) {
        this.banner = banner;
    }

    public static class NoticeBean implements Serializable{
        @Override
        public String toString() {
            return "NoticeBean{" +
                    "date_time='" + date_time + '\'' +
                    ", title='" + title + '\'' +
                    ", owner='" + owner + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }

        /**
         * date_time : 2018-08-08 15:07:51
         * title : aaaa
         * owner : 筐衰骆
         * content : bbbbbb
         */


        private String date_time;
        private String title;
        private String owner;
        private String content;

        public String getDate_time() {
            return date_time;
        }

        public void setDate_time(String date_time) {
            this.date_time = date_time;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class Banner implements Serializable{
        private String pic;
        private String link;

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        @Override
        public String toString() {
            return "Banner{" +
                    "pic='" + pic + '\'' +
                    ", link='" + link + '\'' +
                    '}';
        }
    }
}
