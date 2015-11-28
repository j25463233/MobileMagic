package com.icarus.mobilemagic;

import android.app.Activity;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

public class MagicianActivity extends Activity {

    private AcceptThread server;
    private ConnectedThread mConnectedThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magician);

        server = new AcceptThread();
        server.start();
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
                    Intent showCardIntent = new Intent(getApplicationContext(), ShowCardActivity.class);
                    startActivity(showCardIntent);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) { /**/ }
                    break;
                }
            }
        }
    }
}