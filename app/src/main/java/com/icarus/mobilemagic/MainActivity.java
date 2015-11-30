package com.icarus.mobilemagic;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.UUID;

/**
 * Activity where the user determines if this device will be the magician or
 * the volunteer's device.
 */
public class MainActivity extends Activity {

    public static final String NAME = "Mobile Magic";
    public static final String MY_UUID_STRING =
            "01bb533e-7481-42ec-b042-2b63e2151d7a";
    public static final UUID MY_UUID = UUID.fromString(MY_UUID_STRING);

    // The device's Bluetooth radio
    protected final static BluetoothAdapter mBluetoothAdapter =
            BluetoothAdapter.getDefaultAdapter();

    private Button mMagicianModeButton;
    private Button mVolunteerModeButton;

    private final int REQUEST_ENABLE_BT = 1;
    public static final int REQUEST_DISCOVERABLE = 2;
    public static final int MESSAGE_READ = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check for Bluetooth capability, and if Bluetooth is enabled
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "This device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent
                    (BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // MAGICIAN MODE button
        mMagicianModeButton = (Button) findViewById(R.id.btn_magician);
        mMagicianModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // everything is ok, start Magician activity
                if (mBluetoothAdapter != null &&
                        mBluetoothAdapter.getScanMode() ==
                        BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    Intent magicianIntent = new Intent
                            (getApplicationContext(), MagicianActivity.class);
                    startActivity(magicianIntent);
                } else { // request that this device become discoverable
                    Intent discoverableIntent = new
                            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE);
                }
            }
        });

        // VOLUNTEER MODE button
        mVolunteerModeButton = (Button) findViewById(R.id.btn_volunteer);
        mVolunteerModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start new activity where volunteer finds the magician
                Intent volunteerIntent = new Intent
                        (getApplicationContext(), ConnectActivity.class);
                startActivity(volunteerIntent);
            }
        });
    }

    /**
     * Handles the result from an activity started by an intent.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                // do nothing
                break;
            case REQUEST_DISCOVERABLE:
                // start new activity if device is now discoverable
                if (mBluetoothAdapter.getScanMode() ==
                        BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    Intent magicianIntent = new Intent(this, MagicianActivity.class);
                    startActivity(magicianIntent);
                } else {
                    Toast.makeText(this, "A volunteer might not find you!",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}