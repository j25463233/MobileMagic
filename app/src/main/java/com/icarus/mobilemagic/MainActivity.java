package com.icarus.mobilemagic;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();
            BluetoothComFragment fragment = new BluetoothComFragment();
            transaction.replace(R.id.setup_fragment, fragment);
            transaction.commit();
        }
    }


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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }



    }
}


