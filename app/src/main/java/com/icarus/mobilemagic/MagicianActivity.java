package com.icarus.mobilemagic;

import android.app.Activity;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;

import java.io.IOException;

/**
 * Activity where the Magician is discoverable and waits for a remote device to
 * connect with a Bluetooth socket.
 */
public class MagicianActivity extends Activity {

    private AcceptThread mServer; // thread waiting for connection/volunteer
    private ConnectedThread mConnectedThread; // read/write thread

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magician);
        // wait for a connection/volunteer
        mServer = new AcceptThread();
        mServer.start();
    }

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
                tmp = MainActivity.mBluetoothAdapter.listenUsingRfcommWithServiceRecord
                        (MainActivity.NAME, MainActivity.MY_UUID);
            } catch (IOException e) { return; }
            mmServerSocket = tmp;
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
                    // Start new activity where magician waits for the
                    // volunteer to pick a card
                    Intent showCardIntent = new Intent
                            (getApplicationContext(), ShowCardActivity.class);
                    startActivity(showCardIntent);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) { /* not handled */ }
                    break;
                }
            }
        }
    }
}