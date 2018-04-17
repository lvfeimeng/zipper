package com.zipper.wallet.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseFragment;
import com.zipper.wallet.database.WalletInfo;
import com.zipper.wallet.utils.CreateAcountUtils;
import com.zipper.wallet.utils.SqliteUtils;

import net.bither.bitherj.crypto.EncryptedData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 导出钱包
 */
public class ExportWalletFragment extends BaseFragment {

    private static final String ARG_PARAM = "type";
    private static final String ARG_PARAM2 = "mnemonicWord";
    private static final String ARG_PARAM3 = "cleartext";
    private static final String ARG_PARAM4 = "ciphertext";
    protected View rootView;
    protected TextView textMnemonicExplain;
    protected TextView textContent;
    protected TextView textPrimaryKeyExplain;
    protected Button button;

    private int type;
    private String resultStr = "";

    public ExportWalletFragment() {
        // Required empty public constructor
    }

    //0-备份助记词,1-导出明文私钥，2-导出加密私钥
    public static ExportWalletFragment newInstance(int type,String text) {
        ExportWalletFragment fragment = new ExportWalletFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM, type);
        switch (type) {
            case 0:
                args.putString(ARG_PARAM2, text);
                break;
            case 1:
                args.putString(ARG_PARAM3, text);
                break;
            case 2:
                args.putString(ARG_PARAM4, text);
                break;
            default:
                break;
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_PARAM);
            switch (type) {
                case 0:
                    resultStr = getArguments().getString(ARG_PARAM2);
                    break;
                case 1:
                    resultStr = getArguments().getString(ARG_PARAM3);
                    break;
                case 2:
                    resultStr = getArguments().getString(ARG_PARAM4);
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_export_wallet, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        textMnemonicExplain = (TextView) rootView.findViewById(R.id.text_mnemonic_explain);
        textContent = (TextView) rootView.findViewById(R.id.text_content);
        textPrimaryKeyExplain = (TextView) rootView.findViewById(R.id.text_primary_key_explain);
        button = (Button) rootView.findViewById(R.id.button);

        //new Thread(new InfoThread()).start();
        button.setOnClickListener(v -> onClick());
        setInfo(resultStr);
    }

    private void setPriKeyInfo() {
        button.setText("复制");
        textMnemonicExplain.setVisibility(View.GONE);
        textPrimaryKeyExplain.setVisibility(View.VISIBLE);
    }

    private void onClick() {
        switch (type) {
            case 0:
                break;
            case 1:
            case 2:
                copyToClipboard(getActivity(), textContent.getText().toString().trim());
                button.setEnabled(false);
                button.setText("已复制");
                break;
            default:
                break;
        }
    }

//    private void getMnemonicWord() {
//        StringBuilder words = new StringBuilder();
//        try {
//            CreateAcountUtils.instance(getActivity());//首先实例化助记词类的单例模式
//
//            byte[] randomSeed = CreateAcountUtils.createRandomSeed();//生成128位字节流
//
//            List<String> wordList = CreateAcountUtils.getMnemonicCode(randomSeed);//一局随机数获取助记词
//            for (int i = 0; i < wordList.size(); i++) {
//                words.append(wordList.get(i));
//                if (i < wordList.size() - 1) {
//                    words.append("   ");
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Message msg = handler.obtainMessage();
//        msg.what = 0;
//        msg.obj = words.toString();
//        handler.sendMessage(msg);
//    }
//
//    String cleartextKey = "";
//    String ciphertext = "";
//
//    private void getCleartextKey() {
//        try {
//            CreateAcountUtils.instance(getActivity());//首先实例化助记词类的单例模式
//
//            byte[] randomSeed = CreateAcountUtils.createRandomSeed();//生成128位字节流
//
//            List<String> words = CreateAcountUtils.getMnemonicCode(randomSeed);//一局随机数获取助记词
//
//            byte[] seed = CreateAcountUtils.createMnemSeed(words);//由助记词和密码生成种子,方法内含有转换512哈系数方式
//
//            DeterministicKey master = CreateAcountUtils.CreateRootKey(seed);//生成根公私钥对象
//            //DeterministicKey accountKey = CreateAcountUtils.getAccount(master);
//
//            //String mnemonicSeed = Utils.bytesToHexString(seed);//助记词生成的根种子
//            String priKey = Utils.bytesToHexString(master.getPrivKeyBytes());//根私钥
//            //String pubkey = Utils.bytesToHexString(master.getPubKey());//根公钥
//            cleartextKey = "902E82B8843B9385C7B51C41B7F929E1B44B576A8106EDA98E1A1957E75D150F";
//            ciphertext = new EncryptedData(priKey.getBytes(), "123456", false).toEncryptedString();
//            if (type == 1) {
//                Message msg = handler.obtainMessage();
//                msg.obj = cleartextKey;
//                handler.sendMessage(msg);
//            }
//            if (type == 2) {
//                Message msg = handler.obtainMessage();
//                msg.obj = ciphertext;
//                handler.sendMessage(msg);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager systemService = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (systemService != null) {
            systemService.setPrimaryClip(ClipData.newPlainText("text", text));
        }
    }

//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            String info = (String) msg.obj;
//            setInfo(info);
//        }
//    };

//    class InfoThread implements Runnable {
//        @Override
//        public void run() {
//
//            switch (type) {
//                case 0:
//                    getMnemonicWord();
//                    break;
//                case 1:
//                case 2:
//                    getCleartextKey();
//                    break;
//                default:
//                    break;
//            }
//        }
//    }

    private void setInfo(String info) {
        switch (type) {
            case 0:
                textContent.setText(info);
                button.setText("确认");
                textMnemonicExplain.setVisibility(View.VISIBLE);
                textPrimaryKeyExplain.setVisibility(View.GONE);
                break;
            case 1:
                textContent.setText(info);
                setPriKeyInfo();
                break;
            case 2:
                textContent.setText(info);
                setPriKeyInfo();
                textPrimaryKeyExplain.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

}
