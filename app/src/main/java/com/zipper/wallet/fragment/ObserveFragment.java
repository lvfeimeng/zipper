package com.zipper.wallet.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zipper.wallet.R;

/**
 * 观察.
 */
public class ObserveFragment extends Fragment {


    protected View rootView;
    protected EditText editWalletAddress;
    protected Button btnImport;
    protected TextView textOfflineSign;

    public ObserveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_observe, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        editWalletAddress = (EditText) rootView.findViewById(R.id.editWalletAddress);
        btnImport = (Button) rootView.findViewById(R.id.btnImport);
        textOfflineSign = (TextView) rootView.findViewById(R.id.textOfflineSign);
    }
}
