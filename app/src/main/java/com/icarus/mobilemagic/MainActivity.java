package com.icarus.mobilemagic;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class MainActivity extends Activity {

    private final String NAME = "Mobile Magic";
    private final String MY_UUID_STRING = "01bb533e-7481-42ec-b042-2b63e2151d7a";
    private UUID MY_UUID;

    private ConnectedThread mConnectedThread;
    private static Handler mHandler;

    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mSetPairedDevices;
    private ArrayAdapter<String> mPairedArrayAdapter;
    private ListView mPairedListView;

    private Button mMagicianModeButton;
    private Button mVolunteerModeButton;
    private Button mAddRemoveButton;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISCOVERABLE = 2;
    private static final int MESSAGE_READ = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MY_UUID = UUID.fromString(MY_UUID_STRING);

        mHandler = new Handler(Looper.getMainLooper()) {
            /**
             * The Android system invokes this method when it receives a new
             * message for a thread it's managing.
             */
            @Override
            public void handleMessage(Message inputMessage) {
                mAddRemoveButton.setText(inputMessage.obj.toString());
            }
        };

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

        // MAGICIAN MODE button
        mMagicianModeButton = (Button) findViewById(R.id.btn_magician);
        mMagicianModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent discoverableIntent = new
                        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE);
            }
        });

        // VOLUNTEER MODE button
        mVolunteerModeButton = (Button) findViewById(R.id.btn_volunteer);
        mVolunteerModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConnectedThread.mmSocket.isConnected()) {
                    Toast.makeText(getApplicationContext(), "Devices Connected", Toast.LENGTH_LONG).show();
                    String test = "Test";
                    byte[] bytes = test.getBytes();
                    mConnectedThread.write(bytes);
                }
//                // List of paired devices
                showPairedDevices();
            }
        });

//        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//        long [] pattern = {0, 100, 150, 100, 150, 100, 150, 100};
//        vibrator.vibrate(pattern, -1);
    }

    public void showPairedDevices() {
        mPairedArrayAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1);
        mPairedListView = (ListView) findViewById(R.id.paired_devices);
        mPairedListView.setAdapter(mPairedArrayAdapter);
        mSetPairedDevices = mBluetoothAdapter.getBondedDevices();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DISCOVERABLE:
                if (mBluetoothAdapter.getScanMode() ==
                        BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    AcceptThread server = new AcceptThread();
                    server.start();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showPairedDevices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        // Stop discovery
//        if (mBluetoothAdapter != null) {
//            mBluetoothAdapter.cancelDiscovery();
//        }
//        // Unregister broadcast listeners
//        this.unregisterReceiver(mReceiver);
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
            BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(address);
            ConnectThread client = new ConnectThread(remoteDevice);
            client.start();
        }
    };

    // Method used to save the selected card. This method will save a string of the suit and rank of the card
    // and save it to a file on the internal storage.
    public void saveSelectedCard(){

        // will have to use toString();
        String selectedCard = "";

        // file name
        String file_name = "Previously Selected Cards";

        //attempt to open and save file
        try {
            FileOutputStream fileOutputStream = openFileOutput(file_name, MODE_PRIVATE);
            fileOutputStream.write(selectedCard.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //**************************************************************************

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
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { /**/ }

            mmSocket = tmp;

            Toast.makeText(getApplicationContext(), "Connecting to server", Toast.LENGTH_SHORT).show();
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            //mBluetoothAdapter.cancelDiscovery();

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

//        public void manageConnectedSocket(BluetoothSocket socket) {
//            OutputStream out;
//            String message = "AD";
//            byte[] msg = message.getBytes();
//            try {
//                out = socket.getOutputStream();
//            } catch (IOException ioe) {
//                Log.e("Client", "IOException when opening outputStream");
//            }
//        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { /**/ }
        }
    }

    //**************************************************************************

    /**
     * Thread for a server component that accepts incoming connections.
     * Code borrowed and modified from android developer site.
     */
    private class AcceptThread extends Thread {

        private BluetoothServerSocket mmServerSocket = null;

        public AcceptThread() {

            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) { return; }
            mmServerSocket = tmp;

            Toast.makeText(getApplicationContext(), "Listening...", Toast.LENGTH_SHORT).show();
        }

        public void run() {
            BluetoothSocket socket;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    mConnectedThread = new ConnectedThread(socket);
                    mConnectedThread.start();
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) { /**/ }
                    break;
                }
            }
        }

//        public void manageConnectedSocket(BluetoothSocket socket) {
//            InputStream in;
//            final int nBytes;
//            byte [] msg = new byte[255]; //arbitrary size
//            try {
//                in = socket.getInputStream();
//                nBytes = in.read(msg);
//            } catch (IOException ioe) { /**/ }
//            try {
//                final String msgString = new String(msg, "UTF-8"); //convert byte array to string
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "" + msgString, Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } catch (UnsupportedEncodingException uee) { /**/ }
//            finally {
//                cancel();
//            }
//        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { /**/ }
        }
    }

    //**************************************************************************

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { /**/ }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[24];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    String string = new String(buffer, StandardCharsets.UTF_8);
                    //Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, string).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { /**/ }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { /**/ }
        }
    }
}