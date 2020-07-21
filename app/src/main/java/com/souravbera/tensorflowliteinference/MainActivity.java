package com.souravbera.tensorflowliteinference;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.souravbera.tensorflowliteinference.Sensor.SensorRawDataAG;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Button btn_result;
    private TextView text_output, rawData;
    private float[][] result;
    //    private MainActivity mainActivity = new MainActivity();
    private Activity mActivity;
    private SensorRawDataAG sensorRawDataAG;
    TextView xaValue, yaValue, zaValue, xgValue, ygValue, zgValue;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
//        sensorRawDataAG = new SensorRawDataAG(mActivity);

        text_output = (TextView) findViewById(R.id.text_out);
        rawData = findViewById(R.id.rawData);
        btn_result = (Button) findViewById(R.id.btn_Result);


        btn_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                result = sensorRawDataAG.onResume1();
                text_output.setText(Arrays.deepToString(result));

            }
        });


// Initializing SensorManager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                rawData.setText("X: " + x +
                        "\nY: " + y +
                        "\nZ: " + z);

                break;

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
