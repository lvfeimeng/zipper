package com.zipper.wallet.activity.home.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zipper.wallet.activity.home.bean.CoinBalanceBean;
import com.zipper.wallet.activity.home.bean.HomeCoinsBean;
import com.zipper.wallet.activity.home.bean.PropertyRecordBean;
import com.zipper.wallet.activity.home.contract.HomeContract;
import com.zipper.wallet.database.BtcUtxo;
import com.zipper.wallet.utils.RuntHTTPApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;

/**
 * Created by AlMn on 2018/04/19.
 */

public class HomePresenter implements HomeContract.Presenter {

    public static final int TYPE_GET_COINS = 11;
    public static final int TYPE_GET_BTC_BALANCE = 221;
    public static final int TYPE_GET_ETH_BALANCE = 222;
    public static final int TYPE_BTC_HISTORY = 331;
    public static final int TYPE_ETH_HISTORY = 332;

    private HomeContract.View view;
    private Context context;

    public HomePresenter(Context context, HomeContract.View view) {
        this.view = view;
        this.context = context;
    }

    public HomePresenter(HomeContract.View view) {
        this.view = view;
    }

    @Override
    public void getCoins() {
        RuntHTTPApi.request(RuntHTTPApi.URL_GET_COINS, "", new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                view.doFailure();
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    HomeCoinsBean bean = new Gson().fromJson(response, HomeCoinsBean.class);
                    view.doSuccess(TYPE_GET_COINS, bean.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    @Override
//    public void getCoinBalance(int type, String json) {
//        String url = "";
//        if (type == TYPE_GET_BTC_BALANCE) {
//            url = RuntHTTPApi.URL_BTC_BALANCE;
//        } else if (type == TYPE_GET_ETH_BALANCE) {
//            url = RuntHTTPApi.URL_ETH_BALANCE;
//        }
//        RuntHTTPApi.request(url, json, new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                view.doFailure();
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                CoinBalanceBean bean = new Gson().fromJson(response, CoinBalanceBean.class);
//                view.doSuccess(type, bean.getData());
//            }
//        });
//    }

    @Override
    public void getCoinBalance(int coin_id, String json) {
        String url = coin_id + "/getaddressinfo";
        RuntHTTPApi.request(url, json, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                view.doFailure();
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                CoinBalanceBean bean = new Gson().fromJson(response, CoinBalanceBean.class);
                if (bean.getData() != null) {
                    view.doSuccess(coin_id, bean.getData());
                } else {
                    view.doFailure();
                }
            }
        });
    }

    @Override
    public void getTokenCoinBalance(int coin_id, String token_type, String json) {
        String url = token_type + "/getaddressinfo";
        RuntHTTPApi.request(url, json, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                view.doFailure();
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                CoinBalanceBean bean = new Gson().fromJson(response, CoinBalanceBean.class);
                view.doSuccess(coin_id, bean.getData());
            }
        });
    }

//    @Override
//    public void getCoinHistory(int type, String json) {
//        String url = "";
//        if (type == TYPE_BTC_HISTORY) {
//            url = RuntHTTPApi.URL_BTC_HISTORY;
//        } else if (type == TYPE_ETH_HISTORY) {
//            url = RuntHTTPApi.URL_ETH_HISTORY;
//        }
//        RuntHTTPApi.request(url, json, new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                view.doFailure();
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                PropertyRecordBean bean = new Gson().fromJson(response, PropertyRecordBean.class);
//                view.doSuccess(type, bean.getData());
//            }
//        });
//    }

    @Override
    public void getCoinHistory(int coin_id, String json) {
        String url = coin_id + "/gethistoryinfo";
        RuntHTTPApi.request(url, json, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                view.doFailure();
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                if (TextUtils.isEmpty(response)) {
                    view.doFailure();
                    return;
                }
                PropertyRecordBean bean = new Gson().fromJson(response, PropertyRecordBean.class);
                if (bean.getData() == null) {
                    view.doFailure();
                    return;
                }
                view.doSuccess(coin_id, bean.getData());
            }
        });
    }

    @Override
    public void sendTransaction(int coin_id, String json) {
        String url = coin_id + "/sendrawtransaction";
        RuntHTTPApi.request(url, json, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                view.doFailure();
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                view.doSuccess(coin_id, response);
            }
        });
    }

    @Override
    public void sendTokenTransaction(int coin_id,String token_type, String json) {
        String url = token_type+"/sendrawtransaction";
        RuntHTTPApi.request(url, json, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                view.doFailure();
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                view.doSuccess(coin_id, response);
            }
        });
    }

    @Override
    public void getBlockChainInfo(int coin_id) {
        String url = coin_id + "/getblockchaininfo";
        RuntHTTPApi.request(url, "", new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                view.doFailure();
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                view.doSuccess(coin_id, response);
            }
        });
    }

    @Override
    public void getBtcUtxo(int coin_id, String json) {
        String url = coin_id + "/getutxo";
        RuntHTTPApi.request(url, json, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                view.doFailure();
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                if (TextUtils.isEmpty(response)) {
                    view.doFailure();
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int errCode = jsonObject.optInt("errCode");
                    String data = jsonObject.optString("data");
                    if (errCode == 0 && !TextUtils.isEmpty(data)) {
                        List<BtcUtxo> list = new Gson().fromJson(data, new TypeToken<List<BtcUtxo>>() {
                        }.getType());
                        view.doSuccess(coin_id, list);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
