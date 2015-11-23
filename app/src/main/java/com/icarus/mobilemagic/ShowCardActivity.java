package com.icarus.mobilemagic;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;

import java.util.ArrayList;
import java.util.List;

public class ShowCardActivity extends Activity {

    protected static Handler mHandler;
    private Vibrator vibrator;
    private final long WAIT = 120; // milliseconds between vibrations
    private final long VIBE = 50; // duration of one vibration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_card);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                String s = inputMessage.obj.toString();
                int numVibes = Integer.valueOf(s);
                vibe(numVibes);
            }
        };
    }

    public void vibe(int times) {
        long[] pattern = new long[times * 2 + 1];
        pattern[0] = 0; // milliseconds before first vibration
        for (int i = 1; i < pattern.length; i++) {
            pattern[i] = VIBE;
            pattern[++i] = WAIT;
        }
        vibrator.vibrate(pattern, -1);
    }
}