package com.example.sensorprogramming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    // mSensorManager: one interface for multiple types of sensors, registering is to obtain a reference to the relevant manager
    private SensorManager mSensorManager;
    // m___Sensor: registration for one type of sensor, a reference to the specific sensor you are interested in updates from
    private Sensor mAccelerometerSensor;
    private Sensor mGravitySensor;

    private int accelerometerConnection;
    private int gravityConnection;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    private class AccelerometerListener implements SensorEventListener {
        // called when a registered sensor changes value
        @Override
        public void onSensorChanged(SensorEvent event) {
            EditText field = (EditText) findViewById(R.id.editAccelerometerText);
            field.setText(event.values[0] + "/" + event.values[1] + "/" + event.values[2]);
        }

        // Called when a registered sensor's accuracy changes
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do something here if sensor accuracy changes.
            // You must implement this callback in your code
            if (sensor == mAccelerometerSensor) {
                switch (accuracy) {
                    case 0:
                        System.out.println("Unreliable");
                        accelerometerConnection = 0;
                        break;
                    case 1:
                        System.out.println("Low Accuracy");
                        accelerometerConnection = 1;
                        break;
                    case 2:
                        System.out.println("Medium Accuracy");
                        accelerometerConnection = 2;
                        break;
                    case 3:
                        System.out.println("High Accuracy");
                        accelerometerConnection = 3;
                        break;
                }
            }
        }
    }

    private class GravityListener implements SensorEventListener {
        // called when a registered sensor changes value
        @Override
        public void onSensorChanged(SensorEvent event) {
            EditText field = (EditText) findViewById(R.id.editGravityText);
            field.setText(event.values[0] + "/" + event.values[1] + "/" + event.values[2]);
        }

        // Called when a registered sensor's accuracy changes
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do something here if sensor accuracy changes.
            // You must implement this callback in your code
            if (sensor == mGravitySensor) {
                switch (accuracy) {
                    case 0:
                        System.out.println("Unreliable");
                        gravityConnection = 0;
                        break;
                    case 1:
                        System.out.println("Low Accuracy");
                        gravityConnection = 1;
                        break;
                    case 2:
                        System.out.println("Medium Accuracy");
                        gravityConnection = 2;
                        break;
                    case 3:
                        System.out.println("High Accuracy");
                        gravityConnection = 3;
                        break;
                }
            }
        }
    }

    private SensorEventListener accelerometerListener = new AccelerometerListener();
    private SensorEventListener gravityListener = new GravityListener();

    // The arguments passed into the registerListener method determine the sensor that you are connected to and the rate at which it will send you updates
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(accelerometerListener, mAccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(gravityListener, mGravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(accelerometerListener);
        mSensorManager.unregisterListener(gravityListener);
    }
}