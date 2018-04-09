package com.zipper.wallet.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.zipper.wallet.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 私钥.
 */
public class PrivateKeyFragment extends Fragment {


    protected View rootView;
    protected RadioGroup radioGroup;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private List<Fragment> list;

    public PrivateKeyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_private_key, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        radioGroup = (RadioGroup) rootView.findViewById(R.id.radio_group);

        list = new ArrayList<>();
        SubPrivateKeyFragment subFragment;
        Bundle bundle;
        for (int i = 0; i < 2; i++) {
            subFragment = new SubPrivateKeyFragment();
            bundle = new Bundle();
            bundle.putInt("type", i);
            subFragment.setArguments(bundle);
            list.add(subFragment);
        }

        fm = getChildFragmentManager();
        ft = fm.beginTransaction();
        for (Fragment fragment : list) {
            ft.add(R.id.frame_layout, fragment);
        }
        ft.commit();
        commit(0);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.raido_cleartext:
                    commit(0);
                    break;
                case R.id.raido_ciphertext:
                    commit(1);
                    break;
                default:
                    break;
            }

        });
    }

    private void commit(int index) {
        ft = fm.beginTransaction();
        if (index == 0) {
            ft.show(list.get(0)).hide(list.get(1));
        } else {
            ft.show(list.get(1)).hide(list.get(0));
        }
        ft.commit();
    }

}
