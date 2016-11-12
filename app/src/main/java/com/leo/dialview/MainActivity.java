package com.leo.dialview;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;

/**
 * Created by Administrator on 2016/11/12.
 */

public class MainActivity extends Activity {

    private DialView dial_view;
    private EditText et_angle;
    private SeekBar sb_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dial_view = (DialView) findViewById(R.id.dial_view);
        et_angle = (EditText) findViewById(R.id.et_angle);
        sb_value = (SeekBar) findViewById(R.id.sb_value);

        dial_view.setOnDialChangeListener(new DialView.OnDialChangeListener() {
            @Override
            public void onValueChanged(DialView dialView, int value,int angle) {
                et_angle.setText(angle+"");
                sb_value.setProgress(value);
            }
        });
        sb_value.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dial_view.setValue(progress);
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
