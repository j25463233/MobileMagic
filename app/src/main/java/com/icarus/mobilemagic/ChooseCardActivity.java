package com.icarus.mobilemagic;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import java.io.IOException;

/**
 * Activity where a volunteer can choose a card from a deck. When a card is
 * chosen, it is displayed on the screen and its ID is sent to the magician's
 * device where it vibrates in a pattern according to the chosen card's rank
 * and suit. The card is also displayed on the magician's device.
 */
public class ChooseCardActivity extends Activity {

    /* Connection to the Magician */
    private ConnectedThread mConnectedThread;

    private Deck mDeck;                         // all of the cards
    private Card[] mCardArray;                  // given to the adapter
    private GridViewCustomAdapter mCardAdapter; // holds the array of cards
    private GridView mGridView;                 // displays the cards
    private Button mShuffleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_card);

        /* This Activity is started by an Intent that contains the MAC address
         * of a remote Bluetooth device. Get that MAC address and attempt to
         * connect to the socket. */
        Bundle bundle = getIntent().getExtras();
        String address = bundle.getString("remote_device_address");
        BluetoothDevice remoteDevice =
                MainActivity.mBluetoothAdapter.getRemoteDevice(address);
        ConnectThread client = new ConnectThread(remoteDevice);
        client.start();

        // Fill an array with Cards from a Deck
        mCardArray = new Card[Deck.DECK_SIZE];
        mDeck = new Deck();
        int i = 0;
        for (Card card : mDeck) {
            mCardArray[i] = card;
            i++;
        }
        // Give the array of Cards to the Adapter for a GridView
        mGridView = (GridView) findViewById(R.id.grid_cards);
        mCardAdapter = new GridViewCustomAdapter(this, mCardArray);
        mGridView.setAdapter(mCardAdapter);

        // SHUFFLE button
        mShuffleButton = (Button) findViewById(R.id.shuffle_button);
        mShuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleDeck();
            }
        });
    }

    /**
     * Gives the adapter a shuffled deck instead of an ordered deck.
     */
    public void shuffleDeck() {
        mDeck = new Deck(Deck.SHUFFLED_DECK);
        int i = 0;
        for (Card card : mDeck) {
            mCardArray[i] = card;
            i++;
        }
        mCardAdapter = new GridViewCustomAdapter(this, mCardArray);
        mGridView.setAdapter(mCardAdapter);
    }

    /**
     * Thread that initiates a Bluetooth connection, as a client.
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

    /**
     * Custom ArrayAdapter for the GridView that displays the cards
     */
    public class GridViewCustomAdapter extends ArrayAdapter {
        Context context;
        Card[] cards;

        public GridViewCustomAdapter(Context context, Card[] cards) {
            super(context, 0);
            this.context = context;
            this.cards = cards;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final String id = "" + cards[position].toString(); // card id
            final ImageButton buttonView; // a card the user can tap on
            View card = convertView;

            if (card == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                card = inflater.inflate(R.layout.gridview_card, parent, false);
                buttonView = (ImageButton) card.findViewById(R.id.card_button);
                // display the image of the card with this id
                buttonView.setBackgroundResource(getResources()
                        .getIdentifier(id, "drawable", getPackageName()));
                buttonView.setTag(id);
                // if user taps on this card, send the id to remote device
                // and start a new activity that displays this card
                buttonView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        byte[] bytes = id.getBytes();
                        mConnectedThread.write(bytes);
                        Intent showCardIntent = new Intent
                                (getApplicationContext(), ShowCardActivity.class);
                        showCardIntent.putExtra("chosen_card", id);
                        startActivity(showCardIntent);
                    }
                });
            }
            return card;
        }

        /**
         * Overriding these three methods to prevent view recycling because the
         * card being displayed depends on the position variable in getView(),
         * which resets to zero for views not displayed yet.
         */
        @Override
        public int getItemViewType(int position) {
            return position;
        }
        @Override
        public int getViewTypeCount() {
            return getCount();
        }
        @Override
        public int getCount() {
            return Deck.DECK_SIZE;
        }
    }
}