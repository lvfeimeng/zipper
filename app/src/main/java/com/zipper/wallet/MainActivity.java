package com.zipper.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zipper.wallet.activity.AddPropertyActivity;
import com.zipper.wallet.activity.ImportWalletActivity;
import com.zipper.wallet.activity.MyWalletActivity;
import com.zipper.wallet.activity.StartActivity;
import com.zipper.wallet.activity.UnlockActivity;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.utils.PreferencesUtils;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String pwd = PreferencesUtils.getString(mContext,KEY_HAND_PWD,PreferencesUtils.PROJECT);
        if(pwd !=null && !pwd.equals("")){
            Intent intent = new Intent(MainActivity.this, UnlockActivity.class);
            intent.putExtra("mode",1);
            startActivity(intent);
        }
        boolean isLogin = PreferencesUtils.getBoolean(mContext,KEY_IS_LOGIN,false,PreferencesUtils.USER);
        if (isLogin){
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(intent);
        }
        findViewById(R.id.textView)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this,
                                ImportWalletActivity.class));
                    }
                });
        findViewById(R.id.textView2)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, StartActivity.class);
                        startActivity(intent);
                        //finish();
                    }
                });

        findViewById(R.id.textView3)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, MyWalletActivity.class));
                    }
                });
        findViewById(R.id.textView4)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, AddPropertyActivity.class));
                    }
                });

        // Sha256Hash

    }
}
