package com.icarus.mobilemagic;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ChooseCardActivity extends Activity {

    private ConnectedThread mConnectedThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_card);

        // Retrieve the MAC address of the connected client
        Bundle bundle = getIntent().getExtras();
        String address = bundle.getString("remote_device_address");
        BluetoothDevice remoteDevice =
                MainActivity.mBluetoothAdapter.getRemoteDevice(address);
        ConnectThread client = new ConnectThread(remoteDevice);
        client.start();

        Button buttonOne = (Button) findViewById(R.id.one);
        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "1";
                byte[] vibes = s.getBytes();
                mConnectedThread.write(vibes);
            }
        });

        Button buttonTwo = (Button) findViewById(R.id.two);
        buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "2";
                byte[] vibes = s.getBytes();
                mConnectedThread.write(vibes);
            }
        });

        Button buttonThree = (Button) findViewById(R.id.three);
        buttonThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "3";
                byte[] vibes = s.getBytes();
                mConnectedThread.write(vibes);
            }
        });

        Button buttonFour = (Button) findViewById(R.id.four);
        buttonFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "4";
                byte[] vibes = s.getBytes();
                mConnectedThread.write(vibes);
            }
        });

        Button buttonFive = (Button) findViewById(R.id.five);
        buttonFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "5";
                byte[] vibes = s.getBytes();
                mConnectedThread.write(vibes);
            }
        });
    }

    /**
     * Thread that initiates a Bluetooth connection, as a client.
     * Code borrowed and modified from android developer site.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = mmDevice.createRfcommSocketToServiceRecord(MainActivity.MY_UUID);
            } catch (IOException e) { /**/ }

            mmSocket = tmp;
        }

        public void run() {
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                // Do work to manage the connection (in a separate thread)
                mConnectedThread = new ConnectedThread(mmSocket);
            } catch (IOException ioe) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { /**/ }
            }
        }
    }
}
