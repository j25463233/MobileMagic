package com.icarus.mobilemagic;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Thread where a device can read/write bytes to/from a remote device.
 */
public class ConnectedThread extends Thread {

    /* The Bluetooth socket held by each device */
    private final BluetoothSocket mmSocket;

    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    /**
     * Constructor receives the connected Bluetooth socket.
     * @param socket the connected socket
     */
    public ConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { /* not handled */ }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[2];  // one byte for rank, one byte for suit
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read the card's ID from the InputStream
                bytes = mmInStream.read(buffer);
                String string = new String(buffer, StandardCharsets.UTF_8);
                //Send the obtained card ID to the Handler in the UI thread
                ShowCardActivity.mHandler.obtainMessage
                        (MainActivity.MESSAGE_READ, string).sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }

    /**
     * Call this from the main activity to send data to the remote device
     */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { /* not handled */ }
    }

    /**
     * Call this from the main activity to shutdown the connection
     */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { /* not handled */ }
    }
}