package com.zipper.wallet.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.widget.ImageView;

import com.zipper.wallet.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by AlMn on 2018/04/24.
 */

public class CoinsUtil {

    public static String getJson(Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open("coins.json")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static void addCoinIcon(ImageView imageView, String name, String fullName) {

        switch (name) {
            case "BNB":
                imageView.setBackgroundResource(R.mipmap.bnb);
                break;
            case "BTC":
                imageView.setBackgroundResource(R.mipmap.btc);
                break;
            case "BCH":
                imageView.setBackgroundResource(R.mipmap.bch);
                break;
            case "BTM":
                if (fullName.equals("Bytom")) {
                    imageView.setBackgroundResource(R.mipmap.bitmark);
                } else
                    imageView.setBackgroundResource(R.mipmap.btm);
                break;
            case "BCD":
                imageView.setBackgroundResource(R.mipmap.bcd);
                break;
            case "BCX":
                imageView.setBackgroundResource(R.mipmap.bcx);
                break;
            case "DOGE":
                imageView.setBackgroundResource(R.mipmap.dogecoin);
                break;
            case "ETH":
                imageView.setBackgroundResource(R.mipmap.eth);
                break;
            case "ICX":
                imageView.setBackgroundResource(R.mipmap.icx);
                break;
            case "EOS":
                if (fullName.equals("EOS")) {
                    imageView.setBackgroundResource(R.mipmap.eos);
                } else
                    imageView.setBackgroundResource(R.mipmap.eostoken);
                break;
            case "FT":
                imageView.setBackgroundResource(R.mipmap.ft);
                break;
            case "NEO":
                imageView.setBackgroundResource(R.mipmap.neo);
                break;
            case "LTC":
                imageView.setBackgroundResource(R.mipmap.ltc);
                break;
            case "OMG":
                imageView.setBackgroundResource(R.mipmap.omg);
                break;
            case "TRX":
                imageView.setBackgroundResource(R.mipmap.trx);
                break;
            case "VEN":
                imageView.setBackgroundResource(R.mipmap.ven);
                break;
            case "ZIP":
                imageView.setBackgroundResource(R.mipmap.zip);
                break;
            default:
                imageView.setBackgroundResource(R.mipmap.defaultcurrency);
                break;
        }
    }

}
