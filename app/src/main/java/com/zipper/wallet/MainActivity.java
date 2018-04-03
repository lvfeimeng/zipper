package com.zipper.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zipper.wallet.activity.AddPropertyActivity;
import com.zipper.wallet.activity.BackUpAcitivty;
import com.zipper.wallet.activity.ContactsActivity;
import com.zipper.wallet.activity.MyWalletActivity;
import com.zipper.wallet.base.BaseActivity;
import com.zipper.wallet.activity.ImportWalletActivity;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                        Intent intent = new Intent(MainActivity.this, BackUpAcitivty.class);
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
