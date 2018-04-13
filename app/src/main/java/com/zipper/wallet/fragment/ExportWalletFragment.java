package com.zipper.wallet.fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zipper.wallet.R;
import com.zipper.wallet.base.BaseFragment;
import com.zipper.wallet.utils.CreateAcountUtils;

import net.bither.bitherj.crypto.EncryptedData;
import net.bither.bitherj.crypto.hd.DeterministicKey;
import net.bither.bitherj.utils.Utils;

import java.util.List;

/**
 * 导出钱包
 */
public class ExportWalletFragment extends BaseFragment {

    private static final String ARG_PARAM = "type";
    protected View rootView;
    protected TextView textMnemonicExplain;
    protected TextView textContent;
    protected TextView textPrimaryKeyExplain;
    protected Button button;

    private int type;

    public ExportWalletFragment() {
        // Required empty public constructor
    }

    //0-备份助记词,1-导出明文私钥，20-导出加密私钥
    public static ExportWalletFragment newInstance(int type) {
        ExportWalletFragment fragment = new ExportWalletFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_PARAM);
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

        new Thread(new InfoThread()).start();
        button.setOnClickListener(v -> onClick());
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

    private String getMnemonicWord() {
        StringBuilder words = new StringBuilder();
        try {
            CreateAcountUtils.instance(getActivity());//首先实例化助记词类的单例模式

            byte[] randomSeed = CreateAcountUtils.createRandomSeed();//生成128位字节流

            List<String> wordList = CreateAcountUtils.getMnemonicCode(randomSeed);//一局随机数获取助记词
            for (int i = 0; i < wordList.size(); i++) {
                words.append(wordList.get(i));
                if (i < wordList.size() - 1) {
                    words.append("   ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return words.toString();
    }

    private String getCiphertextKey() {
        String key = getCleartextKey();
        return new EncryptedData(key.getBytes(), "123456").toEncryptedString();
    }

    private String getCleartextKey() {
        try {
            CreateAcountUtils.instance(getActivity());//首先实例化助记词类的单例模式

            byte[] randomSeed = CreateAcountUtils.createRandomSeed();//生成128位字节流

            List<String> words = CreateAcountUtils.getMnemonicCode(randomSeed);//一局随机数获取助记词

            byte[] seed = CreateAcountUtils.createMnemSeed(words);//由助记词和密码生成种子,方法内含有转换512哈系数方式

            DeterministicKey master = CreateAcountUtils.CreateRootKey(seed);//生成根公私钥对象
            //DeterministicKey accountKey = CreateAcountUtils.getAccount(master);

            //String mnemonicSeed = Utils.bytesToHexString(seed);//助记词生成的根种子
            String priKey = Utils.bytesToHexString(master.getPrivKeyBytes());//根私钥
            //String pubkey = Utils.bytesToHexString(master.getPubKey());//根公钥
            return priKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager systemService = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (systemService != null) {
            systemService.setPrimaryClip(ClipData.newPlainText("text", text));
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String info = (String) msg.obj;
            setInfo(info);
        }
    };

    class InfoThread implements Runnable {
        @Override
        public void run() {
            Message msg = handler.obtainMessage();
            msg.what = type;
            switch (type) {
                case 0:
                    msg.obj = getMnemonicWord();
                    break;
                case 1:
                    msg.obj = getCleartextKey();
                    break;
                case 2:
                    msg.obj = getCiphertextKey();
                    break;
                default:
                    break;
            }
            handler.sendMessage(msg);
        }
    }

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
                break;
            default:
                break;
        }
    }

}
