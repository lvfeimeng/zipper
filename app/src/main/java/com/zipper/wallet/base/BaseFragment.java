package com.zipper.wallet.base;

import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

/**
 * Created by AlMn on 2018/04/11.
 */

public class BaseFragment extends Fragment {


    public void toast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public class TextWatcherImpl implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
