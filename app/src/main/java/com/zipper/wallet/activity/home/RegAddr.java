package com.zipper.wallet.activity.home;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zipper.wallet.ether.Bytes;
import com.zipper.wallet.ether.KECCAK256;
import com.zipper.wallet.ether.RlpEncoder;
import com.zipper.wallet.ether.RlpList;
import com.zipper.wallet.ether.RlpString;
import com.zipper.wallet.ether.RlpType;
import com.zipper.wallet.ether.SignEther;
import com.zipper.wallet.utils.PreferencesUtils;
import com.zipper.wallet.utils.RuntHTTPApi;

import net.bither.bitherj.crypto.ECKey;
import net.bither.bitherj.crypto.hd.DeterministicKey;
import net.bither.bitherj.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class RegAddr {

    Context mContext;

    public RegAddr(Context context) {
        this.mContext = context;
    }

    private void putString(String key, String value) {
        PreferencesUtils.putString(mContext, key, value, PreferencesUtils.USER);
    }

    private String getString(String key) {
        return PreferencesUtils.getString(mContext, key, PreferencesUtils.USER);
    }

    public void checkFullAddress(DeterministicKey master, String address, String coinInfoList) {
        Map<String, String> map = new HashMap<>();
        map.put("addr", address);
        String json = new Gson().toJson(map);
        RegAddr.checkFullAddress(json, new RegAddr.Callback() {
            @Override
            public void doSuccess(String data) {
                putString("isRegisterAddress", "1");
            }

            @Override
            public void doFailure() {
                putString("isRegisterAddress", "0");
                registerAddress(master, address, coinInfoList);
            }
        });
    }

    public void registerAddress(DeterministicKey master, String address, String coinInfoList) {
        Map<String, String> map = new HashMap<>();
        map.put("addr", address);
        map.put("list", coinInfoList);
        String signData = address + coinInfoList;
        byte[] bytes = KECCAK256.keccak256(signData.getBytes());
        if (master == null) {
            return;
        }
        BigInteger bigInteger = master.getPrivKey();
        ECKey key = new ECKey(bigInteger, null, false);
        byte[] sigData = key.signHash(bytes, null);

        byte[] result = new byte[sigData.length];
        System.arraycopy(sigData, 1,  result, 0, 64 );
        System.arraycopy(sigData, 0, result, 64, 1 );
        result[64] = (byte)(result[64] -(byte)(27));

//        SignEther.SignatureData data = SignEther.signMessage(bytes, key);
//        List<RlpType> result = new ArrayList<>();
//
//        result.add(RlpString.create(data.getV()));
//        result.add(RlpString.create(Bytes.trimLeadingZeroes(data.getR())));
//        result.add(RlpString.create(Bytes.trimLeadingZeroes(data.getS())));

        //RlpList rlpList = new RlpList(result);
        String signed = Utils.bytesToHexString(result);//RlpEncoder.encode(rlpList)
        map.put("signed", "0x"+signed);
        String json = new Gson().toJson(map);
        RegAddr.registerAddress(json, new RegAddr.Callback() {
            @Override
            public void doSuccess(String data) {
                putString("isRegisterAddress", "1");
            }

            @Override
            public void doFailure() {
                putString("isRegisterAddress", "0");
            }
        });
    }

//    public void checkFullAddress(String address,String coinInfoList) {
//        Map<String, String> map = new HashMap<>();
//        map.put("addr",  address);
//        String json = new Gson().toJson(map);
//        RegAddr.checkFullAddress(json, new RegAddr.Callback() {
//            @Override
//            public void doSuccess() {
//                putString("isRegisterAddress", "1");
//            }
//
//            @Override
//            public void doFailure() {
//                putString("isRegisterAddress", "0");
//                registerAddress(address,coinInfoList);
//            }
//        });
//    }
//
//    public void registerAddress(String address,String coinInfoList) {
//        Map<String, String> map = new HashMap<>();
//        map.put("addr",  address);
//        map.put("list", coinInfoList);
//        String signData =  address + coinInfoList;
//        byte[] bytes= KECCAK256.keccak256(signData.getBytes());
//
//        String signed = "";
//        map.put("signed", signed);
//        String json = new Gson().toJson(map);
//        RegAddr.registerAddress(json, new RegAddr.Callback() {
//            @Override
//            public void doSuccess() {
//                putString("isRegisterAddress", "1");
//            }
//
//            @Override
//            public void doFailure() {
//                putString("isRegisterAddress", "0");
//            }
//        });
//    }

    public static void registerAddress(String json, Callback callback) {
        RuntHTTPApi.request("coin/RegisterAddr", json, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                callback.doFailure();
            }

            @Override
            public void onResponse(String response, int id) {
                callback.doSuccess(response);
            }
        });
    }

    public static void checkFullAddress(String json, Callback callback) {
        RuntHTTPApi.request("coin/GetAddrList", json, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                callback.doFailure();
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    if (TextUtils.isEmpty(response)) {
                        callback.doFailure();
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(response);
                    String data=jsonObject.optString("data");
                    if (!TextUtils.isEmpty(data)) {
                        callback.doSuccess(data);
                    }else{
                        callback.doFailure();
                    }
                } catch (JSONException e) {
                    callback.doFailure();
                    e.printStackTrace();
                }
            }
        });
    }

    public interface Callback {
        void doSuccess(String data);

        void doFailure();
    }

}
