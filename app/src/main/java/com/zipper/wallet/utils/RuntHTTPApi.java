package com.zipper.wallet.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 网络请求api
 * @author Runt02
 *
 */
public class RuntHTTPApi {

    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 1000; // 超时时间

	public final static String IP = "app.qitong.shop",//http://app.qitong.shop/index.php/
	// www.soarsan.com
	PORT = "",
			CHARSET = "utf-8",
			PROJECT_URL = "http://" + IP + PORT +"/index.php/",
            SERVER_URL = "http://" + IP + PORT+"/index.php/",
            URL_USER_INFO = "appapi/user/self_information",//修改用户资料
            URL_ADD_ADDRESS = "appapi/user/add_address",  //添加收货地址
			URL_DEL_ADDRESS = "appapi/user/delete_address/", 	//删除收货地址
			URL_EDIT_ADDRESS = "appapi/user/change_address/", 	//修改收货地址
			URL_ADDRESS_LIST = "appapi/user/address_list",//获取收货地址列表
			URL_VERIFYCODE = "Appapi/user/verify_mobile_code", // 验证手机号
			URL_PHONE_SET_PWD = "Appapi/user/mobile_find_pwd", // 手机号修改密码
			URL_GETCODE = "Appapi/user/send_message", // 获取手机验证码
			URL_EDITPWD = "Appapi/user/old_new_login_pwd", // 修改密码
			URL_EDITPHONE= "Appapi/user/modify_bind_mobile", // 修改手机
			URL_IS_IDENTIFY= "Appapi/user/is_identify_certification",//是否实名认证
			URL_DO_IDENTIFY= "Appapi/user/apply_identify_certification",//提交实名认证
			URL_GET_BANKCARDS= "appapi/user/bank_list",//获取绑定的银行卡列表
			URL_ADD_BACKCARD= "appapi/user/add_bank_card",//添加银行卡
			URL_GET_CARD_BANK_INFO= "appapi/user/bank_card_info",//查询卡号所属银行
			URL_UNBIND_CARD= "Appapi/user/remove_bind_bank",//解除绑定银行卡
			URL_GET_CASH= "Appapi/pay/withdraw_cash",//提现
			URL_SET_PAY_PWD= "Appapi/pay/set_pwd",//设置支付密码
			URL_GET_INTEGRAL_EXPENSE_RECORD= "appapi/integration/points_record/",//积分兑换记录
			URL_GET_BALANCE= "Appapi/user/get_account_balance",//获取余额
			URL_GET_INTEGRAL= "appapi/integration/index",//获取积分
			URL_CREATE_ORDER= "Appapi/pay/create_order",//创建订单
			URL_WALLETPAY= "Appapi/pay/coinspay",//创建订单
			URL_CATEGORY= "Appapi/shop/category_list",//获取经营类目
			URL_APPLY_STORE= "Appapi/shop/apply_shop",//申请店铺
			URL_APPLY_STORE_STATUS= "Appapi/shop/is_audit_pass",//店铺审核状态
			 URL_QUIT_ROOM = "quit_room";


	/**
	 *  okhttp访问接口,多文件加参数传递
	 * @param lastUrl
	 * @param params
	 * @param stringCallback
	 */
	public static void toReApi(String lastUrl, Map<String, Object> params, MyStringCallBack stringCallback){
		params.put("submit", "1");
		String url = SERVER_URL+lastUrl;
		System.out.println("---------------传输的数据-------------------");
		System.out.println("url:"+url);
		printMap(params, "");
		PostFormBuilder pfBuilder = OkHttpUtils.post().url(url);
		for(String key : params.keySet()){
			if (params.get(key) instanceof Collection) {
				for(File file : (Collection<File>)params.get(key)){
					pfBuilder.addFile(key,file.getName(),file);
				}
			} else if(params.get(key) instanceof File) {
				File file = (File)params.get(key);
				pfBuilder.addFile(key,file.getName(),file);
			} else {
				pfBuilder.addParams(key,params.get(key).toString());
			}
		}
		System.out.println("stringCallback:"+stringCallback);
		if(stringCallback!=null) {
			pfBuilder.build().execute(stringCallback);
		}
	}


	/**
	 * 解析json字符串
	 * @param jsonStr
	 * @return
	 */
	public static Map<String, Object> parseJsonToMap(String jsonStr) {
		//System.out.println("jsonStr:" + jsonStr);
		Map<String, Object> map = new TreeMap<String,Object>();
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);// 解析字符串
			Iterator iter = jsonObject.keys();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				Object value = jsonObject.get(key).toString();
				try {
					String str = value.toString();
					if(str.indexOf('[')==0){
						value = parseJsonToCollection(key,jsonObject.getJSONArray(key));

					}else{
						JSONObject tempJson = new JSONObject(value.toString());// 解析字符串
						value = parseJsonToMap(value.toString());
					}
				} catch (Exception e) {
					//Log.e("error", e.getMessage());
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
	 * @return
	 */
	public static Collection<Map<String,Object>> parseJsonToCollection(String key,JSONArray jsonList){
		Collection<Map<String,Object>> list = new LinkedList<Map<String,Object>>();
		try {
			for(int i = 0; i < jsonList.length(); ++i) {
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
				printCollection((Collection)map.get(key),space + "\\\t");
			}else {
				System.out.println(map.get(key));
			}
		}
	}


	/**
	 * 输出打印list集合
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
				printCollection((Collection)param,space + "\\\t");
			}else {
				System.out.println(param);
			}
		}
	}


	/**
	 * 获取随机字符串
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) { //length表示生成字符串的长度
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789_";
	    Random random = new Random();
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < length; i++) {
	        int number = random.nextInt(base.length());
	        sb.append(base.charAt(number));
	    }
	    return sb.toString();
	 }


	/**
	 * MD5加密
	 * @param s
	 * @return
	 */
	public final static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

	public abstract class ResPonse {
		public abstract void doSuccessThing(Map<String,Object> param);
		public abstract void doErrorThing(Map<String,Object> param);
	}


	public class MyStringCallBack extends StringCallback {
		Context mContext;
		String TAG = "MyStringCallBack";
		ResPonse resPonse;

		public final static String MESS_TIP_NET_ERROR = "网络连接不畅，请稍后再试！！！";
		public final static String KEY_MES_CODE= "code";
		public final static String KEY_MES_MESSAGE= "message";
		public final static String KEY_CODE_SUCCESS= "0";//code 0 成功

		public  MyStringCallBack(Context context,ResPonse resPonse){
			this.resPonse = resPonse;
			mContext = context;
		}
		@Override
		public void onError(Call call, Exception e, int id) {
			Toast.makeText(mContext, MESS_TIP_NET_ERROR, Toast.LENGTH_LONG).show();
			Log.e(TAG,e.getLocalizedMessage()+" \\n"+e.getMessage()+" \\n"+e.toString());
			if(resPonse!=null) {
				//showTipDialog(message.toString());
				resPonse.doErrorThing(new HashMap<String, Object>());
			}
		}

		@Override
		public void onResponse(final String response, int id) {
			Log.i(TAG,"response:"+response);
			((Activity)mContext).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					JSONObject jsonObject = null;
					try {
						jsonObject = new JSONObject(response);
						String jsonstr = jsonObject.toString();
						Map<String,Object> param = RuntHTTPApi.parseJsonToMap(jsonstr);
						RuntHTTPApi.printMap(param," ");
						Object message = param.get(KEY_MES_MESSAGE);
						Log.i(TAG,param.toString());

						if(message == null){
							message = jsonstr;
						}
						if (KEY_CODE_SUCCESS.equals(param.get(KEY_MES_CODE))) {
							if(resPonse!=null) {
								resPonse.doSuccessThing(param);
							}
						}else{
							Toast.makeText(mContext, message + "", Toast.LENGTH_SHORT).show();
							if(resPonse!=null) {
								//showTipDialog(message.toString());
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
