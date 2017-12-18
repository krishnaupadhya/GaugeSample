package com.sample.food.gaugesample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sample.food.gaugesample.Indicators.ImageIndicator;

import java.util.Locale;

public class ControlActivity extends AppCompatActivity {

    SpeedView speedView;
    SeekBar seekBar;
    TextView textSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        speedView = (SpeedView) findViewById(R.id.awesomeSpeedometer);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        textSpeed = (TextView) findViewById(R.id.textSpeed);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textSpeed.setText(String.format(Locale.getDefault(), "%d", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        speedView.speedTo(50);
        if (speedView.getWidth() > 0 && speedView.getHeight() > 0) {
            ImageIndicator imageIndicator = new ImageIndicator(this, R.drawable.group_2
                    , (int) speedView.dpTOpx(speedView.getWidth()), (int) speedView.dpTOpx(speedView.getHeight()));
            speedView.setIndicator(imageIndicator);
        }
    }

    public void setSpeed(View view) {
        speedView.speedTo(seekBar.getProgress());
    }




}
