package com.zipper.wallet.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * 网络请求api
 *
 * @author Runt02
 */
public class RuntHTTPApi {

    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 1000; // 超时时间

    //120.92.34.88
    public final static String IP = "120.131.13.167",//  http://172.16.4.76:8080/coin/getcoininfos
    // www.soarsan.com
    PORT = ":80",
            CHARSET = "utf-8",
            PROJECT_URL = "http://" + IP + PORT + "/",
            SERVER_URL = "http://" + IP + PORT + "/",
            URL_GET_COINS = "coin/getcoininfos",//获取币种列表信息
            URL_BTC_BALANCE="btc/getaddressinfo",//获取btc余额信息
            URL_ETH_BALANCE="eth/getaddressinfo",//获取eth余额信息
            URL_BTC_HISTORY="btc/gethistoryinfo",//获取btc历史记录
            URL_ETH_HISTORY="eth/gethistoryinfo";//获取eth历史记录

    public static String getImagePath(String name) {
        return SERVER_URL + "icon/andriod/" + name;
    }

    /**
     * okhttp访问接口,多文件加参数传递
     *
     * @param lastUrl
     * @param params
     * @param stringCallback
     */
    public static void toReApi(String lastUrl, Map<String, Object> params, MyStringCallBack stringCallback) {
        params.put("submit", "1");
        String url = SERVER_URL + lastUrl;
        System.out.println("---------------传输的数据-------------------");
        System.out.println("url:" + url);
        printMap(params, "");
        PostFormBuilder pfBuilder = OkHttpUtils.post().url(url);
        for (String key : params.keySet()) {
            if (params.get(key) instanceof Collection) {
                for (File file : (Collection<File>) params.get(key)) {
                    pfBuilder.addFile(key, file.getName(), file);
                }
            } else if (params.get(key) instanceof File) {
                File file = (File) params.get(key);
                pfBuilder.addFile(key, file.getName(), file);
            } else {
                pfBuilder.addParams(key, params.get(key).toString());
            }
        }
        System.out.println("stringCallback:" + stringCallback);
        if (stringCallback != null) {
            pfBuilder.build().execute(stringCallback);
        }
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    //以json为参数传递
    public static void request(String lastUrl, String json, StringCallback callback) {
        String url = SERVER_URL + lastUrl;
        if (json == null) {
            return;
        }
        OkHttpUtils.postString()
                .url(url)
                .content(json)
                .mediaType(JSON)
                .build()
                .execute(callback);
    }

    //以Map为参数传递
    public static void request(String lastUrl, Map<String, String> map, StringCallback callback) {
        if (map == null) {
            return;
        }
        String json = new Gson().toJson(map);
        String url = SERVER_URL + lastUrl;
        OkHttpUtils.postString()
                .url(url)
                .content(json)
                .mediaType(JSON)
                .build()
                .execute(callback);
    }

//    public static void request2(String lastUrl, Map<String, Object> params, StringCallback callback) {
//        String url = SERVER_URL + lastUrl;
//        PostFormBuilder builder = OkHttpUtils.post().url(url);
//        if (params != null) {
//            for (Map.Entry<String, Object> entry : params.entrySet()) {
//                if (entry.getValue() instanceof File) {
//                    File file = (File) entry.getValue();
//                    builder.addFile(entry.getKey(), file.getName(), file);
//                } else if (entry.getValue() instanceof String) {
//                    builder.addParams(entry.getKey(), entry.getValue().toString());
//                }
//            }
//        }
//        if (callback != null) {
//            builder.build().execute(callback);
//        }
//    }

    /**
     * 解析json字符串
     *
     * @param jsonStr
     * @return
     */
    public static Map<String, Object> parseJsonToMap(String jsonStr) {
        //System.out.println("jsonStr:" + jsonStr);
        Map<String, Object> map = new TreeMap<String, Object>();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);// 解析字符串
            Iterator iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                Object value = jsonObject.get(key).toString();
                try {
                    String str = value.toString();
                    if (str.indexOf('[') == 0) {
                        value = parseJsonToCollection(key, jsonObject.getJSONArray(key));

                    } else {
                        JSONObject tempJson = new JSONObject(value.toString());// 解析字符串
                        value = parseJsonToMap(value.toString());
                    }
                } catch (Exception e) {
                    //MyLog.e("error", e.getMessage());
                }
                //System.out.println("key:"+key+" value:"+value.toString());
                map.put(key, value);
            }
        } catch (org.json.JSONException e) {
            //System.out.println("解析json错误:" + e.getMessage());
        }
        return map;
    }


    /**
     * 解析json字符串
     *
     * @return
     */
    public static Collection<Map<String, Object>> parseJsonToCollection(String key, JSONArray jsonList) {
        Collection<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
        try {
            for (int i = 0; i < jsonList.length(); ++i) {
                Map map = RuntHTTPApi.parseJsonToMap(jsonList.getJSONObject(i).toString());
                list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 输出打印map集合
     *
     * @param map
     * @param space 需要打印的空格
     */
    public static void printMap(Map map, String space) {
        for (Object key : map.keySet()) {
            System.out.print(space + key + ":");
            if (map.get(key) instanceof Map) {
                System.out.println();
                printMap((Map<String, Object>) map.get(key), space + "\\\t");
            } else if (map.get(key) instanceof Collection) {
                System.out.println();
                printCollection((Collection) map.get(key), space + "\\\t");
            } else {
                System.out.println(map.get(key));
            }
        }
    }


    /**
     * 输出打印list集合
     *
     * @param list
     * @param space 需要打印的空格
     */
    public static void printCollection(Collection list, String space) {
        for (Object param : list) {
            System.out.println(param);
            if (param instanceof Map) {
                System.out.println();
                printMap((Map<String, Object>) param, space + "\\\t");
            } else if (param instanceof Collection) {
                System.out.println();
                printCollection((Collection) param, space + "\\\t");
            } else {
                System.out.println(param);
            }
        }
    }


    /**
     * 获取随机字符串
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "";
        String words = "abcdefghijklmnopqrstuvwxyz";
        words += words.toUpperCase();
        words += "0123456789";
        base = words;
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 获取随机字符串
     *
     * @param number
     * @return
     */
    public static char getRandomWord(int number) { //length表示生成字符串的长度
        String base = "";
        String words = "abcdefghijklmnopqrstuvwxyz";
        words += words.toUpperCase();
        words += "0123456789";
        base = words;
        return base.charAt(number % base.length());
    }

    public static String getRandomTime() {
        String timelin = new Date().getTime() + "";
        System.out.println(timelin);
        char[] chars = timelin.toCharArray();
        List<String> list = new LinkedList<>();
        for (int i = 0; i < chars.length; i++) {
            if (i < chars.length - 2) {
                String a = timelin.substring(i, i + 1);
                int intA = Integer.parseInt(a);
                String b = timelin.substring(i, i + 2);
                int intB = Integer.parseInt(b);
                if (intA > 64 && intA < 91
                        || intA > 96 && intA < 123) {

                    System.out.print(Character.toChars(Integer.parseInt(a)));
                    System.out.print(" ");
                    list.add(String.valueOf(Character.toChars(Integer
                            .parseInt(a))));
                } else if (intB > 64 && intB < 91
                        || intB > 96 && intB < 123) {
                    System.out.print(Character.toChars(Integer.parseInt(b)));
                    System.out.print(" ");
                    list.add(String.valueOf(Character.toChars(Integer
                            .parseInt(b))));
                } else {
                    String randomStr = getRandomString(1);
                    System.out.print(" " + randomStr + " ");
                    list.add(randomStr);
                }
            }
        }
        String re = "";
        for (String s : list) {
            re += s;
        }
        return re;
    }


    public static byte[] get128Byte(String s) {

        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            return md;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * MD5加密
     *
     * @param s
     * @return
     */
    public static String MD5(String s) {
        try {
            // 获得密文
            byte[] hash = get128Byte(s);
            StringBuffer result = new StringBuffer(hash.length * 2);
            for (int i = 0; i < hash.length; i++) {
                if (((int) hash[i] & 0xff) < 0x10) {
                    result.append("0");
                }
                result.append(Long.toString((int) hash[i] & 0xff, 16));
            }
            return new String(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public abstract static class ResPonse {
        public abstract void doSuccessThing(Map<String, Object> param);

        public abstract void doErrorThing(Map<String, Object> param);
    }


    public static class MyStringCallBack extends StringCallback {
        Context mContext;
        String TAG = "MyStringCallBack";
        ResPonse resPonse;

        public final static String MESS_TIP_NET_ERROR = "网络连接不畅，请稍后再试！！！";
        public final static String KEY_MES_CODE = "errCode";
        public final static String KEY_MES_MESSAGE = "message";
        public final static String KEY_CODE_SUCCESS = "0";//code 0 成功

        public MyStringCallBack(Context context, ResPonse resPonse) {
            this.resPonse = resPonse;
            mContext = context;
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            Toast.makeText(mContext, MESS_TIP_NET_ERROR, Toast.LENGTH_LONG).show();
            MyLog.e(TAG, e.getLocalizedMessage() + " \\n" + e.getMessage() + " \\n" + e.toString());
            if (resPonse != null) {
                //showTipDialog(message.toString());
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("error", MESS_TIP_NET_ERROR);
                resPonse.doErrorThing(param);
            }
        }

        @Override
        public void onResponse(final String response, int id) {
            MyLog.i(TAG, "response:" + response);
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);
                        String jsonstr = jsonObject.toString();
                        Map<String, Object> param = RuntHTTPApi.parseJsonToMap(jsonstr);
                        RuntHTTPApi.printMap(param, " ");
                        Object message = param.get(KEY_MES_MESSAGE);
                        MyLog.i(TAG, param.toString());

                        if (message == null) {
                            message = jsonstr;
                        }
                        if (KEY_CODE_SUCCESS.equals(param.get(KEY_MES_CODE))) {
                            if (resPonse != null) {
                                resPonse.doSuccessThing(param);
                            }
                        } else {
                            Toast.makeText(mContext, message + "", Toast.LENGTH_SHORT).show();
                            if (resPonse != null) {
                                //showTipDialog(message.toString());
                                param.put("error", "没有数据");
                                resPonse.doErrorThing(param);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
