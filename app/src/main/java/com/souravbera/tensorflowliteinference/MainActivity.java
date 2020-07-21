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
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.souravbera.tensorflowliteinference.Models.SensorData;
import com.souravbera.tensorflowliteinference.Sensor.SensorRawDataAG;

import java.util.ArrayList;
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
    private Sensor gyroscopeSensor;
    private ArrayList<SensorData> accSensorEventData = new ArrayList<>();
    private ArrayList<SensorData> gyroSensorEventData = new ArrayList<>();

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
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);


        CountDownTimer count = new CountDownTimer(5000, 4900) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                rawData.setText(
                        "Acc0: " + Arrays.toString(accSensorEventData.get(0).getValues()) + " || " + accSensorEventData.get(0).getTimestamp() + "\n" +
                                "Acc1: " + Arrays.toString(accSensorEventData.get(1).getValues()) + " || " + accSensorEventData.get(1).getTimestamp() + "\n" +
                                "Acc2: " + Arrays.toString(accSensorEventData.get(2).getValues()) + " || " + accSensorEventData.get(2).getTimestamp() + "\n" +
                                "Acc3: " + Arrays.toString(accSensorEventData.get(3).getValues()) + " || " + accSensorEventData.get(3).getTimestamp() + "\n\n\n" +

                                "Gyr0: " + Arrays.toString(gyroSensorEventData.get(0).getValues()) + " || " + gyroSensorEventData.get(0).getTimestamp() + "\n" +
                                "Gyr1: " + Arrays.toString(gyroSensorEventData.get(1).getValues()) + " || " + gyroSensorEventData.get(1).getTimestamp() + "\n" +
                                "Gyr2: " + Arrays.toString(gyroSensorEventData.get(2).getValues()) + " || " + gyroSensorEventData.get(2).getTimestamp() + "\n" +
                                "Gyr3: " + Arrays.toString(gyroSensorEventData.get(3).getValues()) + " || " + gyroSensorEventData.get(3).getTimestamp() + "\n"
                );
            }
        }.start();

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

                SensorData accData = new SensorData();
                accData.setValues(event.values);
                accData.setTimestamp(event.timestamp);

                Log.wtf("Acc", String.valueOf(event.timestamp));
                accSensorEventData.add(accData);

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

//                rawData.setText("X: " + x +
//                        "\nY: " + y +
//                        "\nZ: " + z);

                break;

            case Sensor.TYPE_GYROSCOPE:

                SensorData gyroData = new SensorData();
                gyroData.setValues(event.values);
                gyroData.setTimestamp(event.timestamp);

                Log.d("Acc", String.valueOf(event.timestamp));
                gyroSensorEventData.add(gyroData);


                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
