package com.zipper.wallet.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zipper.wallet.R;

/**
 * 明文私钥、密文私钥共用页面
 */
public class SubPrivateKeyFragment extends Fragment {

    protected View rootView;
    protected EditText editPrimaryKey;
    protected EditText editPassword;
    protected EditText editConfirmPassword;
    protected EditText editPasswordHint;
    protected CheckBox checkBox;
    protected TextView textAgreement;
    protected Button btnImport;
    protected TextView textPrimaryKey;
    protected EditText editPrimaryKeyPassword;
    protected LinearLayout layoutPrimaryKey;

    private int type = 0;//0---明文，1--密文

    public SubPrivateKeyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sub_private_key, container, false);
        initView(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type", 0);
            if (type == 0) {
                layoutPrimaryKey.setVisibility(View.GONE);
                editPrimaryKey.setHint("输入明文私钥");
            } else {
                layoutPrimaryKey.setVisibility(View.VISIBLE);
                editPrimaryKey.setHint("输入加密私钥");
            }
        }
    }

    private void initView(View rootView) {
        editPrimaryKey = (EditText) rootView.findViewById(R.id.editPrimaryKey);
        editPassword = (EditText) rootView.findViewById(R.id.editPassword);
        editConfirmPassword = (EditText) rootView.findViewById(R.id.editConfirmPassword);
        editPasswordHint = (EditText) rootView.findViewById(R.id.editPasswordHint);
        checkBox = (CheckBox) rootView.findViewById(R.id.checkBox);
        textAgreement = (TextView) rootView.findViewById(R.id.textAgreement);
        btnImport = (Button) rootView.findViewById(R.id.btnImport);
        textPrimaryKey = (TextView) rootView.findViewById(R.id.textPrimaryKey);
        editPrimaryKeyPassword = (EditText) rootView.findViewById(R.id.editPrimaryKeyPassword);
        layoutPrimaryKey = (LinearLayout) rootView.findViewById(R.id.layoutPrimaryKey);
        btnImport.setOnClickListener(v -> {

        });
    }

}
