package com.sanquan.indoorunit.util;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 * @version V1.0
 * @desc AES 加密工具类
 */
public class AESUtil {

    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";//默认的加密算法


    /**
     * 加密
     * @param  input 加密的字符串
     * @param  key   解密的key
     * @return HexString
     */

    public static String encrypt(String input, String key){

        byte[] crypted = null;
        try{
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes());
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(e.toString());
        }
        return Base64.encode(crypted);

    }


    /**
     * @param input
     * @param key
     * @return
     */
    public static String decrypt(String input, String key){
        byte[] output = null;
        try{
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(Base64.decode(input));
            if(output!=null)
                return new String(output);
            else
                return "";
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }


    //加密方法 str为传输的值 key
    public static String aesEncrypt(String str, String key)  {
        if (str == null || key == null) return null;
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes("utf-8"), "AES"));
            byte[] bytes = cipher.doFinal(str.getBytes("utf-8"));
            return Base64.encode(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String aesDecrypt(String str, String key)   {
        if (str == null || key == null) return null;
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes("utf-8"), "AES"));
            byte[] bytes = Base64.decode(str);
            bytes = cipher.doFinal(bytes);
            return new String(bytes, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        String s = "hello,您好";
        System.out.println("s:" + s);
        String s1 = AESUtil.encrypt(s, "1234");
        System.out.println("s1:" + s1);
        System.out.println("s2:"+AESUtil.decrypt(s1, "1234"));
        String ss = "{\"code\":0,\"msg\":\"success\",\"info\":{\"error\":0,\"status\":\"success\",\"date\":\"2018-11-13\",\"results\":[{\"currentCity\":\"\\u6df1\\u5733\\u5e02\",\"pm25\":\"36\",\"index\":[{\"des\":\"\\u5efa\\u8bae\\u7740\\u957f\\u8896T\\u6064\\u3001\\u886c\\u886b\\u52a0\\u5355\\u88e4\\u7b49\\u670d\\u88c5\\u3002\\u5e74\\u8001\\u4f53\\u5f31\\u8005\\u5b9c\\u7740\\u9488\\u7ec7\\u957f\\u8896\\u886c\\u886b\\u3001\\u9a6c\\u7532\\u548c\\u957f\\u88e4\\u3002\",\"tipt\":\"\\u7a7f\\u8863\\u6307\\u6570\",\"title\":\"\\u7a7f\\u8863\",\"zs\":\"\\u8212\\u9002\"},{\"des\":\"\\u8f83\\u9002\\u5b9c\\u6d17\\u8f66\\uff0c\\u672a\\u6765\\u4e00\\u5929\\u65e0\\u96e8\\uff0c\\u98ce\\u529b\\u8f83\\u5c0f\\uff0c\\u64e6\\u6d17\\u4e00\\u65b0\\u7684\\u6c7d\\u8f66\\u81f3\\u5c11\\u80fd\\u4fdd\\u6301\\u4e00\\u5929\\u3002\",\"tipt\":\"\\u6d17\\u8f66\\u6307\\u6570\",\"title\":\"\\u6d17\\u8f66\",\"zs\":\"\\u8f83\\u9002\\u5b9c\"},{\"des\":\"\\u5404\\u9879\\u6c14\\u8c61\\u6761\\u4ef6\\u9002\\u5b9c\\uff0c\\u65e0\\u660e\\u663e\\u964d\\u6e29\\u8fc7\\u7a0b\\uff0c\\u53d1\\u751f\\u611f\\u5192\\u673a\\u7387\\u8f83\\u4f4e\\u3002\",\"tipt\":\"\\u611f\\u5192\\u6307\\u6570\",\"title\":\"\\u611f\\u5192\",\"zs\":\"\\u5c11\\u53d1\"},{\"des\":\"\\u5929\\u6c14\\u8f83\\u597d\\uff0c\\u8d76\\u5feb\\u6295\\u8eab\\u5927\\u81ea\\u7136\\u53c2\\u4e0e\\u6237\\u5916\\u8fd0\\u52a8\\uff0c\\u5c3d\\u60c5\\u611f\\u53d7\\u8fd0\\u52a8\\u7684\\u5feb\\u4e50\\u5427\\u3002\",\"tipt\":\"\\u8fd0\\u52a8\\u6307\\u6570\",\"title\":\"\\u8fd0\\u52a8\",\"zs\":\"\\u9002\\u5b9c\"},{\"des\":\"\\u7d2b\\u5916\\u7ebf\\u5f3a\\u5ea6\\u8f83\\u5f31\\uff0c\\u5efa\\u8bae\\u51fa\\u95e8\\u524d\\u6d82\\u64e6SPF\\u572812-15\\u4e4b\\u95f4\\u3001PA+\\u7684\\u9632\\u6652\\u62a4\\u80a4\\u54c1\\u3002\",\"tipt\":\"\\u7d2b\\u5916\\u7ebf\\u5f3a\\u5ea6\\u6307\\u6570\",\"title\":\"\\u7d2b\\u5916\\u7ebf\\u5f3a\\u5ea6\",\"zs\":\"\\u5f31\"}],\"weather_data\":[{\"date\":\"\\u5468\\u4e8c 11\\u670813\\u65e5 (\\u5b9e\\u65f6\\uff1a21\\u2103)\",\"dayPictureUrl\":\"http:\\/\\//\\/api.map.baidu.com\\/im\\/images\\/weather\\/day\\/y\\/duoyun.png\",\"n\",\"nightPictureUrl\":\"http:\\/\\//\\/api.map.baidu.com\\/im\\/images\\/weather\\/night\\/t\\/duoyun.png\",\"w\",\"weather\":\"\\u591a\\u4e91\",\"wind\":\"\\u65e0\\u6301\\u7eed\\u98ce\\u5411\\u5fae\\u98ce\",\"temperature\":\"26 ~ 21\\u2103\"},{\"date\":\"\\u5468\\u4e09\",\"dayPictureUrl\":\"http:\\/\\//\\/api.map.baidu.com\\/im\\/images\\/weather\\/day\\/y\\/duoyun.png\",\"n\",\"nightPictureUrl\":\"http:\\/\\//\\/api.map.baidu.com";
        String sssd = AESUtil.encrypt(ss,"0bc084249779fc1a");
        System.out.println("sssd:" + sssd);
    }

}
