package com.souravbera.tensorflowliteinference;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;


//import com.souravbera.tensorflowliteinference.Models.SensorData;

import com.souravbera.tensorflowliteinference.tensorflowLite.Classifier;
import com.souravbera.tensorflowliteinference.tensorflowLite.TensorflowClassifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Button btn_result;
    private TextView text_output, Accelerometer, Gyro_data, Acc_data;

    private float[][] result;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor gyroscopeSensor;

    String[] label = {"Fall", "Walk", "Jog", "Jump", "up-stair", "down-stair", "stand2sit", "sit2stand", "No Activity Detected"};

    private TensorflowClassifier tensorflowClassifier = new TensorflowClassifier();
    private int cntAccx = 0, cntAccy = 0, cntGyrx = 10, cntGyry = 0;
    private float[][][][] data = new float[1][20][20][3];

    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();

    private static final String MODEL_PATH = "model_stride1.tflite";
    private static final String LABEL_PATH = "labels.txt";
    private static final int[] INPUT_SIZE = {1, 20, 20, 3};

    private float Accx, Accy, Accz, Gyrox, Gyroy, Gyroz;
    private float KFilteringFactor = 0.6f;
    private float x, y, z;
    private long startrun=0,currenttime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAndLoadModel();
        text_output = (TextView) findViewById(R.id.text_out);
        Acc_data = (TextView) findViewById(R.id.accel);
        Gyro_data = (TextView) findViewById(R.id.gyro);
        btn_result = (Button) findViewById(R.id.btn_Result);


// Initializing SensorManager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        assert sensorManager != null;
        if (checkSensorAvailability(Sensor.TYPE_ACCELEROMETER)) {

            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else {
            accelerometerSensor = null;
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("Alert Dialog");
            alert.setMessage("Required Accelerometer not found null Exception");
            alert.setIcon(R.drawable.ic_warning_black_24dp);
            alert.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    dialogInterface.cancel();
                }
            });
            alert.create();
            alert.show();
        }
        if (checkSensorAvailability(Sensor.TYPE_GYROSCOPE)) {
            gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        } else {
            gyroscopeSensor = null;
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("Alert Dialog");
            alert.setMessage("Required Gyroscope not found null Exception");
            alert.setIcon(R.drawable.ic_warning_black_24dp);
            alert.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    dialogInterface.cancel();
                }
            });
            alert.create();
            alert.show();
        }

        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);



        final CountDownTimer countDownTimer = new CountDownTimer(3600000, 2000) {

            @Override
            public void onTick(long millisUntilFinished) {

                Log.d("Entered Prediction:", "/////////////////////////////////////////////");
                int pos = 0;
                float max = 0.0f;
                float x1, y1, z1;
                result = tensorflowClassifier.Prediction(data);
                max = 0.0f;
                for (int i = 0; i < result[0].length; i++) {
                    if (max < result[0][i]) {
                        max = result[0][i];
                        pos = i;
                    }
                }

                if (label[pos].equals("Fall")) {

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Notification notification = new Notification.Builder(MainActivity.this)
                            .setContentTitle("|| Fall Detected ||")
                            .setContentText("MaxProbability Value: " + max)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.ic_sentiment_very_dissatisfied_black_24dp)
                            .build();
                    notificationManager.notify(4129, notification);
                    Log.wtf("Display Result:", "++++++++++++++++++++++++ " + max + label[pos] + " +++++++++++++++++++++++++");
                    Toast.makeText(MainActivity.this, "Activity: " + label[pos], Toast.LENGTH_SHORT).show();
                    text_output.setText(label[pos]);
                }
                else if ((!label[pos].equals("Fall")) && (!label[pos].equals("Jump")))
                {
                    if(Math.sqrt(x*x +y*y+ z*z)>10 && (currenttime-startrun)<500000000) {
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        Notification notification = new Notification.Builder(MainActivity.this)
                                .setContentTitle("|| Running  ||")
                                .setContentText("Running Detected Just Now")
                                .setAutoCancel(true)
                                .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
                                .build();
                        notificationManager.notify(4129, notification);
                        Toast.makeText(MainActivity.this, "Activity: Running" , Toast.LENGTH_SHORT).show();
                        text_output.setText("Running");
                        Log.wtf("PREDICTION:", "+++++++++++++++ Running Detected ++++++++++++++++++");
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Activity: " + label[pos], Toast.LENGTH_SHORT).show();
                        text_output.setText(label[pos]);
                        Log.d("Display Result:", "++++++++++++++++++++++++ " + max + label[pos] + " +++++++++++++++++++++++++");

                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Activity: " + label[pos], Toast.LENGTH_SHORT).show();
                    text_output.setText(label[pos]);
                    Log.d("Display Result:", "++++++++++++++++++++++++ " + max + label[pos] + " +++++++++++++++++++++++++");

                }
            }

            @Override
            public void onFinish() {

            }
        };
        btn_result.setOnClickListener(new View.OnClickListener() {
            @Override
            @SuppressLint("SetTextI18n")
            public void onClick(View v) {
                countDownTimer.start();
//                Log.d("Entered Prediction:","/////////////////////////////////////////////");
//                int pos=8;
//                float max=0.0f;
//                result=tensorflowClassifier.recognizeImage(data);
//                max = 0.0f;
//                for (int i=0;i<result[0].length;i++){
//                    if(max<result[0][i]){
//                        max=result[0][i];
//                        pos=i;
//                    }
//                }
//                Log.d("Display Result:","++++++++++++++++++++++++ "+max + label[pos]+" +++++++++++++++++++++++++");
//                text_output.setText(label[pos]);
//                Log.d("PREDICTION:",Arrays.deepToString(result));
            }
        });
//            CountDownTimer count = new CountDownTimer(3500, 3000) {
//
//                @Override
//                public void onTick(long millisUntilFinished) {
//
//                }
//
//                @SuppressLint("SetTextI18n")
//                @Override
//                public void onFinish() {
////                rawData.setText(
////                        "Acc0: " + Arrays.toString(accSensorEventData.get(0).getValues()) + " || " + accSensorEventData.get(0).getTimestamp() + "\n" +
////                                "Acc1: " + Arrays.toString(accSensorEventData.get(1).getValues()) + " || " + accSensorEventData.get(1).getTimestamp() + "\n" +
////                                "Acc2: " + Arrays.toString(accSensorEventData.get(2).getValues()) + " || " + accSensorEventData.get(2).getTimestamp() + "\n" +
////                                "Acc3: " + Arrays.toString(accSensorEventData.get(3).getValues()) + " || " + accSensorEventData.get(3).getTimestamp() + "\n\n\n" +
////
////                                "Gyr0: " + Arrays.toString(gyroSensorEventData.get(0).getValues()) + " || " + gyroSensorEventData.get(0).getTimestamp() + "\n" +
////                                "Gyr1: " + Arrays.toString(gyroSensorEventData.get(1).getValues()) + " || " + gyroSensorEventData.get(1).getTimestamp() + "\n" +
////                                "Gyr2: " + Arrays.toString(gyroSensorEventData.get(2).getValues()) + " || " + gyroSensorEventData.get(2).getTimestamp() + "\n" +
////                                "Gyr3: " + Arrays.toString(gyroSensorEventData.get(3).getValues()) + " || " + gyroSensorEventData.get(3).getTimestamp() + "\n"
////                );
//
//                }
//            }.start();
    }

    //To check availability of Sensors
    public boolean checkSensorAvailability(int SensorType) {
        boolean isSensor = false;
        Log.d("CHECKING", "-----------------------------------------------");
        Log.d("Sensors Availability: ", "Check Sensor Availability: " + (sensorManager.getDefaultSensor(SensorType) != null));
        if (sensorManager.getDefaultSensor(SensorType) != null) {
            isSensor = true;
        }
        return isSensor;
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
                if (cntAccx < 10) {
                    if (cntAccy < 20) {
                        Log.d("ACCELEROMETER", String.valueOf(event.timestamp));

//                         double accRMS=Math.sqrt(event.values[0]*event.values[0]+event.values[1]*event.values[1]+event.values[2]*event.values[2]);
                        x = event.values[0];
                        y = event.values[1];
                        z = event.values[2];
                        currenttime=event.timestamp;
                        if(Math.sqrt(event.values[0]*event.values[0]+event.values[1]*event.values[1]+event.values[2]*event.values[2])<2.8){
                            Log.d("Start Run","Triggered");
                            startrun=event.timestamp;
                        }
                        Accx = (int) (255 * (event.values[0] + 16)) / 32.0f;
                        Accy = (int) (255 * (event.values[1] + 16)) / 32.0f;
                        Accz = (int) (255 * (event.values[2] + 16)) / 32.0f;
                        Accx = (float) (Accx / 127.5) - 1;
                        Accy = (float) (Accy / 127.5) - 1;
                        Accz = (float) (Accz / 127.5) - 1;
                        Acc_data.setText("ACCELEROMETER :|| X: " + Accx + "|| Y: " + Accy + "|| Z : " + Accz);
                        data[0][cntAccx][cntAccy][0] = Accx;
                        data[0][cntAccx][cntAccy][1] = Accy;
                        data[0][cntAccx][cntAccy][2] = Accz;

                        cntAccy += 1;
                    } else {
                        cntAccy = 0;
                        cntAccx += 1;
                    }
                } else {
                    cntAccx = 0;
                    cntAccy = 0;
                }
//                Log.d("SENSOR VALUES: ", Arrays.toString(event.values));

//                if((Math.sqrt(event.values[0]*event.values[0]+event.values[1]*event.values[1]+event.values[2]*event.values[2]))>2.8){
//                    result=tensorflowClassifier.recognizeImage(data);
//
//                }
//                SensorData accData = new SensorData();
//                accData.setValues(event.values);
//                accData.setTimestamp(event.timestamp);

//                Log.wtf("Acc", String.valueOf(event.timestamp));
//                accSensorEventData.add(accData);
//
//                float x = event.values[0];
//                float y = event.values[1];
//                float z = event.values[2];

//                rawData.setText("X: " + x +
//                        "\nY: " + y +
//                        "\nZ: " + z);

                break;

            case Sensor.TYPE_GYROSCOPE:
                if (cntGyrx < 20) {
                    if (cntGyry < 20) {
                        Log.wtf("GYROSCOPE:", String.valueOf(event.timestamp));
                        Gyrox = (255 * (event.values[0] + 34)) / 68.0f;
                        Gyroy = (255 * (event.values[0] + 34)) / 68.0f;
                        Gyroz = (255 * (event.values[0] + 34)) / 68.0f;
                        Gyrox = (float) (Gyrox / 127.5) - 1;
                        Gyroy = (float) (Gyroy / 127.5) - 1;
                        Gyroz = (float) (Gyroz / 127.5) - 1;
                        Gyro_data.setText("GYROSCOPE :|| X: " + Gyrox + "|| Y: " + Gyroy + "|| Z: " + Gyroz);
                        data[0][cntGyrx][cntGyry][0] = Gyrox;
                        data[0][cntGyrx][cntGyry][1] = Gyroy;
                        data[0][cntGyrx][cntGyry][2] = Gyroz;

                        cntGyry += 1;
                    } else {
                        cntGyry = 0;
                        cntGyrx += 1;
                    }
                } else {
                    cntGyry = 0;
                    cntGyrx = 10;
                }
//                SensorData gyroData = new SensorData();
//                gyroData.setValues(event.values);
//                gyroData.setTimestamp(event.timestamp);
//
//`                 Log.d("Acc", String.valueOf(event.timestamp));
//                gyroSensorEventData.add(gyroData);
                Log.wtf("SENSOR VALUES: ", Arrays.toString(event.values));
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void initAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = tensorflowClassifier.create(getAssets(), MODEL_PATH, LABEL_PATH, INPUT_SIZE);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }
}
