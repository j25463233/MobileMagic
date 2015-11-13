package com.icarus.mobilemagic;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();
            BluetoothComFragment fragment = new BluetoothComFragment();
            transaction.replace(R.id.setup_fragment, fragment);
            transaction.commit();
        }
    }
}