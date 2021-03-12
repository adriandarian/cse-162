package com.example.idlenessmonitor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private TextView mTextView;
    private TextView mCountDown;
    private Button mButton;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    // obtain the views, and initiate the sensors
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);
        mCountDown = (TextView) findViewById(R.id.count);
        mButton = (Button) findViewById(R.id.button);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        // Enables Always-on
        setAmbientEnabled();
    }

    // change the texts, make it un-clickable, and begin sensor data monitoring
    public void start_countdown(View view) {
        Log.d("TAG", "Entered function");
        mButton.setText("Monitoring");
        mButton.setEnabled(false);
        mSensorManager.registerListener(this, mSensor, 20);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float maxValue = 1;
        // When sensor data changes, check if the motion is greater than a threshold
        if (Math.abs(event.values[0]) + Math.abs(event.values[1]) + Math.abs(event.values[2]) > maxValue) {
            Log.d("DEBUG","Rotated");
            maxValue = event.values[0] + event.values[1] + event.values[2];

            // display the texts
            mTextView.setText("large movements");

            // obtain the permission of using vibration
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] vibrationPattern = {0, 500, 50, 300};

            // -1 - don't repeat
            final int indexInPatternToRepeat = -1;

            // vibrates the watch
            vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);

            mSensorManager.unregisterListener(this);

            // 10 seconds in total, update the display every second
            CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
                public void onTick(long millisUntilFinished) {
                    Log.d("TAG", "TICK");
                    mCountDown.setText("seconds remaining: " + millisUntilFinished / 1000);
                }

                // update the texts and enable the button again after the time is finished
                public void onFinish() {
                    mCountDown.setText("done!");
                    mButton.setText("Start");
                    mButton.setEnabled(true);

                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    long[] vibrationPattern = {0, 500, 50, 300};

                    // -1 - don't repeat
                    final int indexInPatternToRepeat = -1;
                    vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
                }
            }.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
