package com.github.anastr.speedview;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.anastr.speedviewlib.Gauge;
import com.github.anastr.speedviewlib.SectionPointerSpeedometer;
import com.github.anastr.speedviewlib.util.OnSpeedChangeListener;

import java.util.ArrayList;
import java.util.Locale;

public class PointerActivity extends AppCompatActivity {

    SectionPointerSpeedometer pointerSpeedometer;
    SeekBar seekBarSpeed;
    Button ok;
    TextView textSpeed, textSpeedChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointer);

        pointerSpeedometer = findViewById(R.id.pointerSpeedometer);
        seekBarSpeed = findViewById(R.id.seekBarSpeed);
        ok = findViewById(R.id.ok);
        textSpeed = findViewById(R.id.textSpeed);
        textSpeedChange = findViewById(R.id.textSpeedChange);

        ArrayList<String> colorList = new ArrayList<>();
        colorList.add("#f58a80");
        colorList.add("#fad95d");
        colorList.add("#86ce5d");
        colorList.add("#3bbbc6");
        colorList.add("#2e9ec5");
        colorList.add("#2172bd");

        pointerSpeedometer.setColorList(colorList);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointerSpeedometer.speedTo(seekBarSpeed.getProgress());
            }
        });

        seekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        pointerSpeedometer.setOnSpeedChangeListener(new OnSpeedChangeListener() {
            @Override
            public void onSpeedChange(Gauge gauge, boolean isSpeedUp, boolean isByTremble) {
                textSpeedChange.setText(String.format(Locale.getDefault(), "onSpeedChange %d"
                        , gauge.getCurrentIntSpeed()));
            }
        });
    }
}
