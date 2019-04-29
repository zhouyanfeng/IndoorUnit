package com.sanquan.indoorunit.bean;

import java.util.List;

/**
 * 作者：zyf on 2018/9/21.
 * 用途：天气bean
 */
public class WeatherBean {

    /**
     * code : 0
     * msg : success
     * info : {"error":0,"status":"success","date":"2018-09-21","results":[{"currentCity":"深圳市","pm25":"27","index":[{"des":"天气炎热，建议着短衫、短裙、短裤、薄型T恤衫等清凉夏季服装。","tipt":"穿衣指数","title":"穿衣","zs":"炎热"},{"des":"较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。","tipt":"洗车指数","title":"洗车","zs":"较适宜"},{"des":"各项气象条件适宜，发生感冒机率较低。但请避免长期处于空调房间中，以防感冒。","tipt":"感冒指数","title":"感冒","zs":"少发"},{"des":"天气较好，较适宜进行各种运动，但因天气热，请适当减少运动时间，降低运动强度。","tipt":"运动指数","title":"运动","zs":"较适宜"},{"des":"属中等强度紫外线辐射天气，外出时建议涂擦SPF高于15、PA+的防晒护肤品，戴帽子、太阳镜。","tipt":"紫外线强度指数","title":"紫外线强度","zs":"中等"}],"weather_data":[{"date":"周五 09月21日 (实时：31℃)","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/duoyun.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/duoyun.png","weather":"多云","wind":"无持续风向微风","temperature":"32 ~ 27℃"},{"date":"周六","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/zhenyu.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/zhenyu.png","weather":"阵雨","wind":"无持续风向微风","temperature":"32 ~ 26℃"},{"date":"周日","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/zhenyu.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/zhenyu.png","weather":"阵雨","wind":"无持续风向微风","temperature":"30 ~ 25℃"},{"date":"周一","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/zhenyu.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/zhenyu.png","weather":"阵雨","wind":"无持续风向微风","temperature":"29 ~ 24℃"}]}]}
     */

    private int code;
    private String msg;
    private InfoBean info;

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
        /**
         * error : 0
         * status : success
         * date : 2018-09-21
         * results : [{"currentCity":"深圳市","pm25":"27","index":[{"des":"天气炎热，建议着短衫、短裙、短裤、薄型T恤衫等清凉夏季服装。","tipt":"穿衣指数","title":"穿衣","zs":"炎热"},{"des":"较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。","tipt":"洗车指数","title":"洗车","zs":"较适宜"},{"des":"各项气象条件适宜，发生感冒机率较低。但请避免长期处于空调房间中，以防感冒。","tipt":"感冒指数","title":"感冒","zs":"少发"},{"des":"天气较好，较适宜进行各种运动，但因天气热，请适当减少运动时间，降低运动强度。","tipt":"运动指数","title":"运动","zs":"较适宜"},{"des":"属中等强度紫外线辐射天气，外出时建议涂擦SPF高于15、PA+的防晒护肤品，戴帽子、太阳镜。","tipt":"紫外线强度指数","title":"紫外线强度","zs":"中等"}],"weather_data":[{"date":"周五 09月21日 (实时：31℃)","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/duoyun.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/duoyun.png","weather":"多云","wind":"无持续风向微风","temperature":"32 ~ 27℃"},{"date":"周六","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/zhenyu.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/zhenyu.png","weather":"阵雨","wind":"无持续风向微风","temperature":"32 ~ 26℃"},{"date":"周日","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/zhenyu.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/zhenyu.png","weather":"阵雨","wind":"无持续风向微风","temperature":"30 ~ 25℃"},{"date":"周一","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/zhenyu.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/zhenyu.png","weather":"阵雨","wind":"无持续风向微风","temperature":"29 ~ 24℃"}]}]
         */

        private int error;
        private String status;
        private String date;
        private List<ResultsBean> results;

        public int getError() {
            return error;
        }

        public void setError(int error) {
            this.error = error;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public List<ResultsBean> getResults() {
            return results;
        }

        public void setResults(List<ResultsBean> results) {
            this.results = results;
        }

        public static class ResultsBean {
            /**
             * currentCity : 深圳市
             * pm25 : 27
             * index : [{"des":"天气炎热，建议着短衫、短裙、短裤、薄型T恤衫等清凉夏季服装。","tipt":"穿衣指数","title":"穿衣","zs":"炎热"},{"des":"较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。","tipt":"洗车指数","title":"洗车","zs":"较适宜"},{"des":"各项气象条件适宜，发生感冒机率较低。但请避免长期处于空调房间中，以防感冒。","tipt":"感冒指数","title":"感冒","zs":"少发"},{"des":"天气较好，较适宜进行各种运动，但因天气热，请适当减少运动时间，降低运动强度。","tipt":"运动指数","title":"运动","zs":"较适宜"},{"des":"属中等强度紫外线辐射天气，外出时建议涂擦SPF高于15、PA+的防晒护肤品，戴帽子、太阳镜。","tipt":"紫外线强度指数","title":"紫外线强度","zs":"中等"}]
             * weather_data : [{"date":"周五 09月21日 (实时：31℃)","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/duoyun.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/duoyun.png","weather":"多云","wind":"无持续风向微风","temperature":"32 ~ 27℃"},{"date":"周六","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/zhenyu.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/zhenyu.png","weather":"阵雨","wind":"无持续风向微风","temperature":"32 ~ 26℃"},{"date":"周日","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/zhenyu.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/zhenyu.png","weather":"阵雨","wind":"无持续风向微风","temperature":"30 ~ 25℃"},{"date":"周一","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/zhenyu.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/zhenyu.png","weather":"阵雨","wind":"无持续风向微风","temperature":"29 ~ 24℃"}]
             */

            private String currentCity;
            private String pm25;
            private List<IndexBean> index;
            private List<WeatherDataBean> weather_data;

            public String getCurrentCity() {
                return currentCity;
            }

            public void setCurrentCity(String currentCity) {
                this.currentCity = currentCity;
            }

            public String getPm25() {
                return pm25;
            }

            public void setPm25(String pm25) {
                this.pm25 = pm25;
            }

            public List<IndexBean> getIndex() {
                return index;
            }

            public void setIndex(List<IndexBean> index) {
                this.index = index;
            }

            public List<WeatherDataBean> getWeather_data() {
                return weather_data;
            }

            public void setWeather_data(List<WeatherDataBean> weather_data) {
                this.weather_data = weather_data;
            }

            public static class IndexBean {
                /**
                 * des : 天气炎热，建议着短衫、短裙、短裤、薄型T恤衫等清凉夏季服装。
                 * tipt : 穿衣指数
                 * title : 穿衣
                 * zs : 炎热
                 */

                private String des;
                private String tipt;
                private String title;
                private String zs;

                public String getDes() {
                    return des;
                }

                public void setDes(String des) {
                    this.des = des;
                }

                public String getTipt() {
                    return tipt;
                }

                public void setTipt(String tipt) {
                    this.tipt = tipt;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getZs() {
                    return zs;
                }

                public void setZs(String zs) {
                    this.zs = zs;
                }
            }

            public static class WeatherDataBean {
                @Override
                public String toString() {
                    return "WeatherDataBean{" +
                            "date='" + date + '\'' +
                            ", dayPictureUrl='" + dayPictureUrl + '\'' +
                            ", nightPictureUrl='" + nightPictureUrl + '\'' +
                            ", weather='" + weather + '\'' +
                            ", wind='" + wind + '\'' +
                            ", temperature='" + temperature + '\'' +
                            '}';
                }

                /**
                 * date : 周五 09月21日 (实时：31℃)
                 * dayPictureUrl : http://api.map.baidu.com/images/weather/day/duoyun.png
                 * nightPictureUrl : http://api.map.baidu.com/images/weather/night/duoyun.png
                 * weather : 多云
                 * wind : 无持续风向微风
                 * temperature : 32 ~ 27℃
                 */


                private String date;
                private String dayPictureUrl;
                private String nightPictureUrl;
                private String weather;
                private String wind;
                private String temperature;

                public String getDate() {
                    return date;
                }

                public void setDate(String date) {
                    this.date = date;
                }

                public String getDayPictureUrl() {
                    return dayPictureUrl;
                }

                public void setDayPictureUrl(String dayPictureUrl) {
                    this.dayPictureUrl = dayPictureUrl;
                }

                public String getNightPictureUrl() {
                    return nightPictureUrl;
                }

                public void setNightPictureUrl(String nightPictureUrl) {
                    this.nightPictureUrl = nightPictureUrl;
                }

                public String getWeather() {
                    return weather;
                }

                public void setWeather(String weather) {
                    this.weather = weather;
                }

                public String getWind() {
                    return wind;
                }

                public void setWind(String wind) {
                    this.wind = wind;
                }

                public String getTemperature() {
                    return temperature;
                }

                public void setTemperature(String temperature) {
                    this.temperature = temperature;
                }
            }
        }
    }
}
