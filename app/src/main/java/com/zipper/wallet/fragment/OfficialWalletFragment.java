package com.zipper.wallet.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.zipper.wallet.R;

/**
 * 官方钱包.
 */
public class OfficialWalletFragment extends Fragment {


    protected View rootView;
    protected EditText editContent;
    protected EditText editPassword;
    protected CheckBox checkBox;
    protected TextView textAgreement;
    protected Button btnImport;
    protected TextView textKeystore;

    public OfficialWalletFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_official_wallet, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        editContent = (EditText) rootView.findViewById(R.id.editContent);
        editPassword = (EditText) rootView.findViewById(R.id.editPassword);
        checkBox = (CheckBox) rootView.findViewById(R.id.checkBox);
        textAgreement = (TextView) rootView.findViewById(R.id.textAgreement);
        btnImport = (Button) rootView.findViewById(R.id.btnImport);
        textKeystore = (TextView) rootView.findViewById(R.id.textKeystore);
    }
}
