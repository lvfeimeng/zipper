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

        void getTokenCoinBalance(int coin_id, String token_type, String json);

        void getCoinHistory(int coin_id, String json);

        void sendTransaction(int coin_id, String json);

        void sendTokenTransaction(int coin_id, String token_type,String json);

        void getBlockChainInfo(int coin_id);

        void getBtcUtxo(int coin_id, String json);
    }

}
