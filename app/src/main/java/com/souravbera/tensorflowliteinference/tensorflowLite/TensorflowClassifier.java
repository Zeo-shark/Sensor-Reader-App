package com.souravbera.tensorflowliteinference.tensorflowLite;


import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;


import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import java.util.List;



public class TensorflowClassifier implements Classifier {


    private static TensorflowClassifier classifier = new TensorflowClassifier();

    private Interpreter interpreter;
    private int[] inputSize;
    private List<String> labelList;


    public TensorflowClassifier() {

    }

    public Classifier create(AssetManager assetManager, String modelPath, String labelPath, int[] inputSize) throws IOException {
        classifier.interpreter = new Interpreter(classifier.loadModelFile(assetManager, modelPath), new Interpreter.Options());
        classifier.labelList = classifier.loadLabelList(assetManager, labelPath);
        classifier.inputSize = inputSize;
        return classifier;
    }


    public float[][] Prediction(float[][][][] input_data) {
        List<String> labelList = classifier.labelList;
        Interpreter interpreter = classifier.interpreter;
        float[][][][] input = input_data;
        float[][] result = new float[1][labelList.size()];
        interpreter.run(input, result);
        return result;
    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private List<String> loadLabelList(AssetManager assetManager, String labelPath) throws IOException {
        List<String> labelList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(labelPath)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }


    @Override
    public void close() {
        interpreter.close();
        interpreter = null;
    }
}
