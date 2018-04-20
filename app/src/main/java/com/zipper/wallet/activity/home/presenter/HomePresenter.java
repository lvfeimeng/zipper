package com.zipper.wallet.activity.home.presenter;

import android.content.Context;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zipper.wallet.activity.home.bean.HomeCoinsBean;
import com.zipper.wallet.activity.home.contract.HomeContract;
import com.zipper.wallet.utils.RuntHTTPApi;

import okhttp3.Call;

/**
 * Created by AlMn on 2018/04/19.
 */

public class HomePresenter implements HomeContract.Presenter {

    public static final int TYPE_GET_COINS = 11;
    public static final int TYPE_GET_BTC_BALANCE = 22;

    private HomeContract.View view;
    private Context context;

    public HomePresenter(Context context, HomeContract.View view) {
        this.view = view;
        this.context = context;
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
                    view.doSuccess(TYPE_GET_COINS, bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void getBtcBalance(String json) {
        RuntHTTPApi.request(RuntHTTPApi.URL_GET_COINS, json, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                view.doFailure();
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                view.doSuccess(TYPE_GET_BTC_BALANCE,response);
            }
        });
    }
}
