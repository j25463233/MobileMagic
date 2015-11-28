package com.icarus.mobilemagic;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.widget.ImageView;

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

        Bundle bundle = getIntent().getExtras();
        try {
            String id = bundle.getString("chosen_card");
            showCard(id);   
        } catch (NullPointerException noe) { /**/ }

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                String id = inputMessage.obj.toString();
                showCard(id);
                vibeCardId(id);
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException ie) { /**/ }
//                vibeCardId(id);
            }
        };
    }

    public void showCard(String id) {
        mCardImage = (ImageView) findViewById(R.id.chosen_card_image);
        mCardImage.setBackgroundResource(getResources().getIdentifier(id, "drawable", getPackageName()));
    }

    public void vibeCardId(String vibeId) {
        vibrate(intValueOf(vibeId.substring(0,1)));
        vibrate(intValueOf(vibeId.substring(1)));
    }

    public void vibrate(int numVibes) {
        long[] pattern = new long[numVibes * 2 + 1]; // +1 for next statement
        pattern[0] = 0; // milliseconds before first vibration
        for (int i = 1; i < pattern.length; i++) {
            pattern[i] = VIBE;
            pattern[++i] = WAIT;
        }
        mVibrator.vibrate(pattern, -1);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ie) { /**/ }
    }

    /**
     * Provides an integer value for this suit.
     * @return the integer value corresponding to this suit
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