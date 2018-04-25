package com.zipper.wallet.activity.home.contract;

/**
 * Created by AlMn on 2018/04/19.
 */

public interface HomeContract {

    interface View {
        void doSuccess(int type, Object obj);

        void doFailure();
    }

    interface Presenter {
        void getCoins();
        void getCoinBalance(int id, String json);
        void getCoinHistory(int id, String json);
    }

}
