package com.zipper.wallet.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zipper.wallet.R;
import com.zipper.wallet.WebBrowserActivity;
import com.zipper.wallet.activity.ImportWalletActivity;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.base.BaseFragment;
import com.zipper.wallet.utils.CreateAcountUtils;
import com.zipper.wallet.utils.PreferencesUtils;

import net.bither.bitherj.crypto.hd.DeterministicKey;
import net.bither.bitherj.crypto.hd.HDKeyDerivation;
import net.bither.bitherj.crypto.mnemonic.MnemonicCode;
import net.bither.bitherj.crypto.mnemonic.MnemonicException;
import net.bither.bitherj.utils.Sha256Hash;
import net.bither.bitherj.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.zipper.wallet.base.BaseActivity.PARAMS_TITLE;
import static com.zipper.wallet.base.BaseActivity.PARAMS_URL;

/**
 * 助记词.
 */
public class MnemonicWordFragment extends BaseFragment {

    private static final String TAG = "MnemonicWordFragment";

    protected View rootView;
    protected EditText editWord;
    protected EditText editPassword;
    protected EditText editConfirmPassword;
    protected EditText editPasswordHint;
    protected CheckBox checkBox;
    protected TextView textAgreement;
    protected Button btnImport;
    protected TextView textWord;

    private Context mContext;

    public MnemonicWordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_mnemonic_word, container, false);
        initView(rootView);
        mContext = getActivity();
        return rootView;
    }

    private void initView(View rootView) {
        editWord = (EditText) rootView.findViewById(R.id.editWord);
        editPassword = (EditText) rootView.findViewById(R.id.editPassword);
        editConfirmPassword = (EditText) rootView.findViewById(R.id.editConfirmPassword);
        editPasswordHint = (EditText) rootView.findViewById(R.id.editPasswordHint);
        checkBox = (CheckBox) rootView.findViewById(R.id.checkBox);
        textAgreement = (TextView) rootView.findViewById(R.id.textAgreement);
        btnImport = (Button) rootView.findViewById(R.id.btnImport);
        textWord = (TextView) rootView.findViewById(R.id.textWord);
        addTextChangedListener(editWord);
        addTextChangedListener(editPassword);
        addTextChangedListener(editConfirmPassword);
        textAgreement.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WebBrowserActivity.class);
            intent.putExtra(PARAMS_TITLE, "服务协议");
            intent.putExtra(PARAMS_URL, "file:///android_asset/agreement.html");
            startActivity(intent);
        });
        btnImport.setOnClickListener(v -> submit());
    }

    private String wordStr = "";
    private String password = "";
    private String passwordConfirm = "";
    private String passwordHint = "";

    private void submit() {
        wordStr = editWord.getText().toString().trim();
        password = editPassword.getText().toString().trim();
        passwordConfirm = editConfirmPassword.getText().toString().trim();
        passwordHint = editPasswordHint.getText().toString().trim();
        if (!checkBox.isChecked()) {
            Toast.makeText(getActivity(), "请同意服务及隐私条款", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(wordStr) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordConfirm)) {
            Toast.makeText(getActivity(), "助记词及密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(passwordConfirm)) {
            Toast.makeText(getActivity(), "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> words = new ArrayList<>();
        words.addAll(Arrays.asList(wordStr.split(" ")));

//        if (words.size()!=12) {
//            toast("请输入12个助记单词");
//            return;
//        }
        generateWalletAddress(words);

    }

    private void generateWalletAddress(List<String> words) {
        CreateAcountUtils.instance(mContext);
        PreferencesUtils.putString(mContext, BaseActivity.KEY_WALLET_PWD,password, PreferencesUtils.VISITOR);
        byte[] seed = new byte[0];//由助记词和密码生成种子,方法内含有转换512哈系数方式
        try {
            seed = CreateAcountUtils.createMnemSeed(words);
            ((ImportWalletActivity)getActivity()).generateWalletAddress(Utils.bytesToHexString(CreateAcountUtils.entropyRandomSeed(words)),Utils.bytesToHexString(seed));
        } catch (Exception e) {
            e.printStackTrace();
            toast("助记词有误，请重新输入");
            return;
        }

    }

    private void addTextChangedListener(EditText editText) {
        editText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable s) {
                if (editText == editWord) {
                    wordStr = s.toString().trim();
                } else if (editText == editPassword) {
                    password = s.toString().trim();
                } else if (editText == editConfirmPassword) {
                    passwordConfirm = s.toString().trim();
                }
                if (TextUtils.isEmpty(wordStr) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordConfirm)) {
                    btnImport.setEnabled(false);
                } else {
                    btnImport.setEnabled(true);
                }
            }
        });
    }

    private void importWallet() {
        try {
            MnemonicCode.setInstance(new MnemonicCodeTestClass());
            SecureRandom random = new SecureRandom();
            byte[] mnemonicSeed = new byte[16];
            random.nextBytes(mnemonicSeed);
            List<String> words = getMnemonicCode(Utils.hexStringToByteArray(Utils.bytesToHexString(mnemonicSeed)));
            byte[] seed = MnemonicCode.toSeed(words, "");
            //Utils.bytesToHexString(seed)
            DeterministicKey master = HDKeyDerivation.createMasterPrivateKey(seed);

            Utils.bytesToHexString(master.getPrivKeyBytes());
            Utils.bytesToHexString(master.getPubKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getMnemonicCode(byte[] entropy) throws MnemonicException
            .MnemonicLengthException {
        if (entropy.length % 4 > 0) {
            throw new MnemonicException.MnemonicLengthException("Entropy length not multiple of "
                    + "32 bits.");
        }

        if (entropy.length == 0) {
            throw new MnemonicException.MnemonicLengthException("Entropy is empty.");
        }

        // We take initial entropy of ENT bits and compute its
        // checksum by taking first ENT / 32 bits of its SHA256 hash.

        byte[] hash = Sha256Hash.create(entropy).getBytes();
        boolean[] hashBits = bytesToBits(hash);

        boolean[] entropyBits = bytesToBits(entropy);
        int checksumLengthBits = entropyBits.length / 32;

        // We append these bits to the end of the initial entropy.
        boolean[] concatBits = new boolean[entropyBits.length + checksumLengthBits];
        System.arraycopy(entropyBits, 0, concatBits, 0, entropyBits.length);
        System.arraycopy(hashBits, 0, concatBits, entropyBits.length, checksumLengthBits);

        // Next we take these concatenated bits and split them into
        // groups of 11 bits. Each group encodes number from 0-2047
        // which is a position in a wordlist.  We convert numbers into
        // words and use joined words as mnemonic sentence.

        ArrayList<String> words = new ArrayList<String>();
        int nwords = concatBits.length / 11;
        for (int i = 0;
             i < nwords;
             ++i) {
            int index = 0;
            for (int j = 0;
                 j < 11;
                 ++j) {
                index <<= 1;
                if (concatBits[(i * 11) + j]) {
                    index |= 0x1;
                }
            }
            words.add((String) this.wordList.get(index));
        }

        return words;

    }

    public static ArrayList<String> wordList = new ArrayList<String>(2048);

    private static boolean[] bytesToBits(byte[] data) {
        boolean[] bits = new boolean[data.length * 8];
        for (int i = 0;
             i < data.length;
             ++i)
            for (int j = 0;
                 j < 8;
                 ++j)
                bits[(i * 8) + j] = (data[i] & (1 << (7 - j))) != 0;
        return bits;
    }

    public final class MnemonicCodeTestClass extends MnemonicCode {
        private static final String WordListPath = "english.txt";

        public MnemonicCodeTestClass() throws IOException {
            super();
        }

        @Override
        protected InputStream openWordList() throws IOException {
            InputStream stream = getActivity().getAssets().open(WordListPath);
            if (stream == null) {
                throw new FileNotFoundException(WordListPath);
            }
            return stream;
        }
    }

}
