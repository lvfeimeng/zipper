package com.zipper.wallet.activity.home.contract;

/**
 * Created by AlMn on 2018/04/19.
 */

public interface HomeContract {

    interface View {
        void doSuccess(int coin_id, Object obj);

        void doFailure();
    }

    interface Presenter {
        void getCoins();

        void getCoinBalance(int coin_id, String json);

        void getCoinHistory(int coin_id, String json);

        void sendTransaction(int coin_id, String json);
    }

}
