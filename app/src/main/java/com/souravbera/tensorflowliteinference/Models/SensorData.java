package com.souravbera.tensorflowliteinference.Models;

import java.io.Serializable;

public class SensorData implements Serializable {

    private float[] values;
    private long timestamp;

    public float[] getValues() {
        return values;
    }

    public void setValues(float[] values) {
        this.values = values;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
