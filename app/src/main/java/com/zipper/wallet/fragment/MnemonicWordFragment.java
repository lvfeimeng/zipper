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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zipper.wallet.R;
import com.zipper.wallet.activity.ImportWalletActivity;
import com.zipper.wallet.activity.MyWalletActivity;
import com.zipper.wallet.activity.WebActivity;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.base.BaseFragment;
import com.zipper.wallet.utils.AlgorithmUtils;
import com.zipper.wallet.utils.CreateAcountUtils;
import com.zipper.wallet.utils.PreferencesUtils;
import com.zipper.wallet.utils.RuntHTTPApi;

import net.bither.bitherj.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    TextView txtPwdReWar, txtStrong;
    ImageView imgPwdSign;
    LinearLayout  linWarining;

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

        linWarining = (LinearLayout) rootView.findViewById(R.id.lin_warning);
        txtStrong = (TextView) rootView.findViewById(R.id.txt_strong);
        txtPwdReWar = (TextView) rootView.findViewById(R.id.txt_pwdre_warning);
        imgPwdSign = (ImageView) rootView.findViewById(R.id.img_pwd_sign);

        addTextChangedListener(editWord);
        addTextChangedListener(editPassword);
        addTextChangedListener(editConfirmPassword);
        textAgreement.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), WebBrowserActivity.class);
//            intent.putExtra(PARAMS_TITLE, "服务协议");
//            intent.putExtra(PARAMS_URL, "file:///android_asset/agreement.html");
            startActivity(new Intent(getActivity(), WebActivity.class)
                    .putExtra("type", 1));
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

        if (password.length() < 8) {
//            ((ImportWalletActivity) getActivity()).showTipDialog("密码不能小于8位数", null);
            toast("密码不小于8位字符");
            return;
        }

        if (!password.equals(passwordConfirm)) {
            Toast.makeText(getActivity(), "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> words = new ArrayList<>();
          wordStr= wordStr.replace((char) 160, (char) 32);
        words.addAll(Arrays.asList(wordStr.split(Character.toString((char)32))));
//        if (words.size()!=12) {
//            toast("请输入12个助记单词");
//            return;
//        }
        generateWalletAddress(words);

    }

    private void generateWalletAddress(List<String> words) {
        btnImport.setEnabled(false);
        CreateAcountUtils.instance(mContext);
        PreferencesUtils.putString(mContext, BaseActivity.KEY_WALLET_PWD,password, PreferencesUtils.VISITOR);
        byte[] seed = new byte[0];//由助记词和密码生成种子,方法内含有转换512哈系数方式
        try {
            seed = CreateAcountUtils.createMnemSeed(words);
            ((ImportWalletActivity)getActivity()).generateWalletAddress(Utils.bytesToHexString(CreateAcountUtils.entropyRandomSeed(words)),Utils.bytesToHexString(seed));
        } catch (Exception e) {
            btnImport.setEnabled(true);
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
                    handlePwdStrong(password);
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


    private void importSuccess() {
        ((ImportWalletActivity) getActivity()).showTipDialog("导入钱包成功", "正在后台生成钱包地址，可能需要一段时间，可在首页查看", "", "知道了",
                new RuntHTTPApi.ResPonse() {
                    @Override
                    public void doSuccessThing(Map<String, Object> param) {
                        startActivity(new Intent(getActivity(), MyWalletActivity.class)
                                .putExtra("isFromImportPage", true));
                    }

                    @Override
                    public void doErrorThing(Map<String, Object> param) {

                    }
                });
    }

    public void handlePwdStrong(String pwd) {

        switch (AlgorithmUtils.pwdLevel(pwd)){
            case -1:
                txtStrong.setText("");
                imgPwdSign.setImageResource(R.mipmap.pwd_n);
                linWarining.setVisibility(View.INVISIBLE);
                break;
            case 0 :
            case 1:
                imgPwdSign.setImageResource(R.mipmap.pwd_low);
                txtStrong.setTextColor(getResources().getColor(R.color.btn_delete));
                ((View)txtStrong.getParent()).setVisibility(View.VISIBLE);
                linWarining.setVisibility(View.VISIBLE);
                txtStrong.setText("弱");
                break;
            case 2:
                imgPwdSign.setImageResource(R.mipmap.pwd_well);
                txtStrong.setTextColor(getResources().getColor(R.color.text_link));
                txtStrong.setText("一般");
                linWarining.setVisibility(View.INVISIBLE);
                break;
            case 3:
                imgPwdSign.setImageResource(R.mipmap.pwd_good);
                txtStrong.setTextColor(getResources().getColor(R.color.text_link));
                txtStrong.setText("很好");
                linWarining.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
