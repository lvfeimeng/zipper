package com.zipper.wallet.base;

import com.zipper.wallet.utils.RuntHTTPApi;

import java.util.Map;

/**
 * Created by Administrator on 2018/4/10.
 */

public class CreateActvity extends BaseActivity {

    @Override
    protected boolean onBackKeyDown() {

        showDoubleButtonDialog("放弃创建钱包", "钱包还未创建成功，是否放弃？", new RuntHTTPApi.ResPonse() {
            @Override
            public void doSuccessThing(Map<String, Object> param) {
                finish();
            }

            @Override
            public void doErrorThing(Map<String, Object> param) {
                alertDialog.dismiss();
            }
        },"继续","放弃");

        return true;
    }
}
