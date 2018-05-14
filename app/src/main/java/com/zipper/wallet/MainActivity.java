package com.zipper.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zipper.wallet.activity.AddPropertyActivity;
import com.zipper.wallet.activity.ImportWalletActivity;
import com.zipper.wallet.activity.MyWalletActivity;
import com.zipper.wallet.activity.PropertyActvity;
import com.zipper.wallet.activity.StartActivity;
import com.zipper.wallet.base.BaseActivity;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(intent);
        findViewById(R.id.textView).setOnClickListener(v ->
                startActivity(new Intent(this, ImportWalletActivity.class))
        );
        findViewById(R.id.textView2).setOnClickListener(v ->
                startActivity(new Intent(this, PropertyActvity.class))
        );

        findViewById(R.id.textView3).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, MyWalletActivity.class))
        );
        findViewById(R.id.textView4).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddPropertyActivity.class))
        );

        // Sha256Hash
        finish();

    }
}
