package com.icarus.mobilemagic;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.widget.ImageView;

/**
 * Activity where the card chosen by a volunteer is displayed while a
 * corresponding pattern is vibrated, allowing the magician to guess the card
 * without seeing the screen.
 */
public class ShowCardActivity extends Activity {

    protected static Handler mHandler;
    private ImageView mCardImage;
    private Vibrator mVibrator;
    private final long WAIT = 150; // milliseconds between vibrations
    private final long VIBE = 120; // duration of one vibration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_card);

        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // The bundle will contain a card ID if this activity was started on
        // a volunteer's device. If so, get the card ID and display that card.
        Bundle bundle = getIntent().getExtras();
        try {
            String id = bundle.getString("chosen_card");
            showCard(id);   
        } catch (NullPointerException noe) { /* not handled */ }

        // Handles messages received from the Bluetooth connection
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                // the message should contain a card ID
                String id = inputMessage.obj.toString();
                showCard(id);
                vibeCardId(id);
            }
        };
    }

    /**
     * Displays a specified card.
     * @param id the ID of the card to be displayed
     */
    public void showCard(String id) {
        mCardImage = (ImageView) findViewById(R.id.chosen_card_image);
        mCardImage.setBackgroundResource
                (getResources().getIdentifier(id, "drawable", getPackageName()));
    }

    /**
     * Uses the devices vibrator to vibrate one pattern based on the first
     * character of a string, and again based on the second character.
     * @param vibeId first character = suit, second character = rank.
     */
    public void vibeCardId(String vibeId) {
        vibrate(intValueOf(vibeId.substring(0,1)));
        vibrate(intValueOf(vibeId.substring(1)));
    }

    /**
     * Vibrates the vibrator a specified number of times.
     * @param numVibes number of vibrations in the sequence
     */
    public void vibrate(int numVibes) {
        long[] pattern = new long[numVibes * 2 + 1]; // +1 for next statement
        pattern[0] = 0; // milliseconds before first vibration
        for (int i = 1; i < pattern.length; i++) {
            pattern[i] = VIBE; // vibration duration in milliseconds
            pattern[++i] = WAIT; // milliseconds between vibrations
        }
        mVibrator.vibrate(pattern, -1);
        // forced pause in case there is another vibration sequence to follow
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ie) { /**/ }
    }

    /**
     * Provides an integer value for a suit.
     * @return the integer value corresponding to a suit
     */
    public int intValueOf(String s) {
        switch (s) {
            case "c":
                return Suit.CLUBS.value();
            case "h":
                return Suit.HEARTS.value();
            case "s":
                return Suit.SPADES.value();
            case "d":
                return Suit.DIAMONDS.value();
            case "a":
                return Rank.ACE.value();
            case "2":
                return Rank.DEUCE.value();
            case "3":
                return Rank.TREY.value();
            case "4":
                return Rank.FOUR.value();
            case "5":
                return Rank.FIVE.value();
            case "6":
                return Rank.SIX.value();
            case "7":
                return Rank.SEVEN.value();
            case "8":
                return Rank.EIGHT.value();
            case "9":
                return Rank.NINE.value();
            case "t":
                return Rank.TEN.value();
            case "j":
                return Rank.JACK.value();
            case "q":
                return Rank.QUEEN.value();
            case "k":
                return Rank.KING.value();
            default: return 0;
        }
    }
}