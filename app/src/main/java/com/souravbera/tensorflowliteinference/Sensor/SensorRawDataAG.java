package com.souravbera.tensorflowliteinference.Sensor;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.souravbera.tensorflowliteinference.tensorflowLite.Classifier;
import com.souravbera.tensorflowliteinference.tensorflowLite.TensorflowClassifier;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SensorRawDataAG  {

    private static final String TAG = "SensorRawDataAG";
    private static final String MODEL_PATH = "model.tflite";
    private static final String LABEL_PATH = "labels.txt";
    private static final int[] INPUT_SIZE = {1, 20, 20, 3};

    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
    private final SensorManager mSensorManager;
    private Sensor mAccelerometer, mGyroscope;
    private float acc, acc1;
    private float gyro, gyro1;

    private float[][] result;

    private float[][][][] data= new float[1][20][20][3];
    private Activity mActivity;

    private TensorflowClassifier tensorFlowClassifier= new TensorflowClassifier();

    public SensorRawDataAG(Activity mActivity) {
        this.mActivity=mActivity;
        Log.d(TAG, "onCreate: Intialising Sensor Services");
        mSensorManager = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);

        assert mSensorManager != null;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        initTensorFlowAndLoadModel();
    }

    public float[][] onResume1() {
        mSensorManager.registerListener(new SensorEventListener() {
            private int countx=0,county=0;
            @Override
            public void onSensorChanged(final SensorEvent sAccEvent) {


                mSensorManager.registerListener(new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent sGyroEvent){

                        gyro = (float) Math.sqrt(sGyroEvent.values[0] * sGyroEvent.values[0] + sGyroEvent.values[1] * sGyroEvent.values[1] + sGyroEvent.values[2] * sGyroEvent.values[2]);

                        acc = (float) Math.sqrt(sAccEvent.values[0] * sAccEvent.values[0] + sAccEvent.values[1] * sAccEvent.values[1] + sAccEvent.values[2] * sAccEvent.values[2]);
                        if(countx<10){
                            if(county<10) {
                                data[0][countx][county][0]=sAccEvent.values[0];
                                data[0][countx][county][1]=sAccEvent.values[1];
                                data[0][countx][county][2]=sAccEvent.values[0];
                                data[0][countx+10][county+10][0]=sGyroEvent.values[0];
                                data[0][countx+10][county+10][1]=sGyroEvent.values[0];
                                data[0][countx+10][county+10][2]=sGyroEvent.values[0];
                                county += 1;
                            }
                            else{
                                county=0;
                                countx+=1;
                            }
                        }
                        else{
                            Log.d(TAG,"data: "+data[0][0][0][0]);
                            countx=0;
                        }
                        if (acc < 2.8) {
                            long start = System.currentTimeMillis();
                            do {
                                acc1 = (float) Math.sqrt(sAccEvent.values[0] * sAccEvent.values[0] + sAccEvent.values[1] * sAccEvent.values[1] + sAccEvent.values[2] * sAccEvent.values[2]);
                                gyro1= (float) Math.sqrt(sGyroEvent.values[0]*sGyroEvent.values[0]+sGyroEvent.values[1] * sGyroEvent.values[1] + sGyroEvent.values[2] * sGyroEvent.values[2]);

                                if(acc1 > 20 && gyro1 > 6){
                                    result = tensorFlowClassifier.recognizeImage(data);
                                    break;
                                }
                                else if(acc1>22){
                                    result=tensorFlowClassifier.recognizeImage(data);
                                    break;
                                }

                            } while ((System.currentTimeMillis() - start) < 500);
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                }, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        return result;
    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = tensorFlowClassifier.create(mActivity.getAssets(), MODEL_PATH, LABEL_PATH, INPUT_SIZE);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }
}


