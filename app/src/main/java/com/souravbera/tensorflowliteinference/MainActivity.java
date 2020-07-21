package com.souravbera.tensorflowliteinference;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.souravbera.tensorflowliteinference.Sensor.SensorRawDataAG;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private Button btn_result;
    private TextView text_output;
    private float[][] result;
    private MainActivity mainActivity= new MainActivity();
    private SensorRawDataAG sensorRawDataAG= new SensorRawDataAG(mainActivity);
    TextView xaValue,yaValue,zaValue,xgValue,ygValue,zgValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_output = (TextView) findViewById(R.id.text_out);
        btn_result = (Button) findViewById(R.id.btn_Result);


        btn_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    result= sensorRawDataAG.onResume1();
                text_output.setText(Arrays.deepToString(result));

            }
        });
    }
}
