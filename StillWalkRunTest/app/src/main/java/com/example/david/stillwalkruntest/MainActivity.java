package com.example.david.stillwalkruntest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    int speed = 0;//0 is still, 1 is walking and 2 is running
    TextView moveText;
    ImageView moveImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moveText = findViewById(R.id.MovementText);
        moveImage = findViewById(R.id.MovementImage);
        /*
        switch(speed){
            case 0:
                moveText.setText(R.string.move_still);
                moveImage.setImageResource(R.mipmap.still);
                break;
            case 1:
                moveText.setText(R.string.move_walk);
                moveImage.setImageResource(R.mipmap.walking);
                break;
            case 2:
                moveText.setText(R.string.move_run);
                moveImage.setImageResource(R.mipmap.running);
                break;
        }
        */

        final Button StillButton = findViewById(R.id.StillButton);
        StillButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                speed = 0;
                moveText.setText(R.string.move_still);
                moveImage.setImageResource(R.mipmap.still);
            }
        });

        final Button WalkButton = findViewById(R.id.WalkButton);
        WalkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                speed = 1;
                moveText.setText(R.string.move_walk);
                moveImage.setImageResource(R.mipmap.walking);
            }
        });

        final Button RunButton = findViewById(R.id.RunButton);
        RunButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                speed = 2;
                moveText.setText(R.string.move_run);
                moveImage.setImageResource(R.mipmap.running);
            }
        });

    }
}
