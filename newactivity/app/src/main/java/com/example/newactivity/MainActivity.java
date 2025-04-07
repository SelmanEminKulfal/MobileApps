package com.example.newactivity;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    public int oran1=0,oran2=0,oran3=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        SeekBar sb1=(SeekBar) findViewById(R.id.seekBar1);
        SeekBar sb2=(SeekBar) findViewById(R.id.seekBar2);
        SeekBar sb3=(SeekBar) findViewById(R.id.seekBar3);

        TextView txt1=(TextView) findViewById(R.id.textView1);
        TextView txt2=(TextView) findViewById(R.id.textView2);
        TextView txt3=(TextView) findViewById(R.id.textView3);

        ConstraintLayout arkarenk=(ConstraintLayout) findViewById(R.id.arkarenk);

        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sb1.setMax(255);
                int oran = android.graphics.Color.rgb(oran3,oran2,i);
                arkarenk.setBackgroundColor(oran);
                txt1.setText(String.valueOf(i));
                oran1=i;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sb2.setMax(255);
                int oran = android.graphics.Color.rgb(oran1,oran3,i);
                arkarenk.setBackgroundColor(oran);
                txt2.setText(String.valueOf(i));
                oran2=i;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sb3.setMax(255);
                int oran = android.graphics.Color.rgb(oran1,oran2,i);
                arkarenk.setBackgroundColor(oran);
                txt3.setText(String.valueOf(i));
                oran3=i;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}