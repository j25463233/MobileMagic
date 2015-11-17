package com.icarus.mobilemagic;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends FragmentActivity {

    private static final int REQUEST_ENABLE_BT = 1111;
    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mReceiver;
    private Set<BluetoothDevice> mPairedDevices;
    private ListView mPairedListView;
    private ListView mNewListView;
    private ArrayAdapter<String> mPairedArrayAdapter;
    private ArrayAdapter<String> mNewArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check for a Bluetooth radio and if device has it enabled
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "This device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent
                    (BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        mPairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
//        if (mPairedDevices.size() > 0) {
//            mPairedListView = (ListView) findViewById(R.id.paired_devices);
//            mPairedArrayAdapter = new ArrayAdapter<>
//                    (this, android.R.layout.simple_list_item_1);
//            mPairedListView.setAdapter(mPairedArrayAdapter);
//            // Loop through paired devices
//            for (BluetoothDevice device : mPairedDevices) {
//                // Add name/address to an array adapter to show in ListView
//                mPairedArrayAdapter.add(device.getName() +
//                        "\n" + device.getAddress());
//            }
//        }

        mNewListView = (ListView) findViewById(R.id.other_devices);
        mNewArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1);
        mNewListView.setAdapter(mNewArrayAdapter);

//        Intent discoverableIntent = new
//                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//        startActivity(discoverableIntent);

        // Create a BroadcastReceiver for ACTION_FOUND
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra
                            (BluetoothDevice.EXTRA_DEVICE);
                    // Add name/address to an array adapter to show in ListView
                    mNewArrayAdapter.add(device.getName() +
                            "\n" + device.getAddress());
                }
            }
        };
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        mBluetoothAdapter.startDiscovery();
//        // Register for broadcasts when discovery has finished
//        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        this.registerReceiver(mReceiver, filter);

        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final Button vibrateButton = (Button) findViewById(R.id.vibrate_button);
        vibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long [] pattern = {0, 100, 150, 100, 150, 100, 150, 100};
                vibrator.vibrate(pattern, -1);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop discovery
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }
}