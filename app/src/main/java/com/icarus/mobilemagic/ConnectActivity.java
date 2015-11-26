package com.icarus.mobilemagic;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

public class ConnectActivity extends Activity {

    private Set<BluetoothDevice> mSetPairedDevices;
    private ArrayAdapter<String> mPairedArrayAdapter;
    private ListView mPairedListView;
    private Button mAddRemoveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        mPairedListView = (ListView) findViewById(R.id.paired_devices);
        showPairedDevices();

        // ADD/REMOVE button
        mAddRemoveButton = (Button) findViewById(R.id.btn_add_remove);
        mAddRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent
                        (android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(settingsIntent);
            }
        });
    }

    public void showPairedDevices() {
        mPairedArrayAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1);
        mPairedListView.setAdapter(mPairedArrayAdapter);
        mSetPairedDevices = MainActivity.mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (mSetPairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : mSetPairedDevices) {
                // Add name/address to an array adapter to show in ListView
                mPairedArrayAdapter.add(device.getName() +
                        "\n" + device.getAddress());
            }
            mPairedListView.setOnItemClickListener(mDeviceClickListener);
        } else {
            mPairedArrayAdapter.add("No devices");
        }
    }

    /**
     * The OnItemClickListener for all devices in the ListView
     */
    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Switch to the ChooseCardActivity
            Intent chooseCardIntent = new Intent
                    (getApplicationContext(), ChooseCardActivity.class);
            chooseCardIntent.putExtra("remote_device_address", address);
            startActivity(chooseCardIntent);
            finish();
        }
    };
}