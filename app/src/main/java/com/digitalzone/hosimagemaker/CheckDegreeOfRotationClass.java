package com.digitalzone.hosimagemaker;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CheckDegreeOfRotationClass implements SensorEventListener {

    private double pitch;
    private double roll;
    private double yaw;

    private double defaultAlgle = 0;

    double rotationX;
    double rotationY;
    double rotationZ;


    private double RAD2DGR = 180 / Math.PI;
    private double DGR2RAD = Math.PI;
    private static final float NS2S = 1.0f/1000000000.0f;

    private long timestamp;
    private double dt;

    private Handler stateHandler;

    CheckDegreeOfRotationClass(Handler handler){
        stateHandler = handler;
        timestamp = System.currentTimeMillis();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Bundle setData = new Bundle();

        double gyroX = event.values[0];
        double gyroY = event.values[1];
        double gyroZ = event.values[2];

        if(defaultAlgle == 0){
            defaultAlgle = gyroZ;
        }

//        if(rotationX < Math.abs(gyroX)){
//            rotationX =  gyroX;
//        }
//
//        if(rotationY < Math.abs(gyroY)){
//            rotationY =  gyroY;
//        }
//
//        if(defaultAlgle < Math.abs(gyroZ)){
//            rotationZ =  gyroZ;
//        }

        setData.putDouble("isTurn",defaultAlgle - gyroZ);



        dt = (event.timestamp - timestamp) * NS2S;
        timestamp = event.timestamp;

        if (dt - timestamp*NS2S != 0) {

            pitch = pitch + gyroY*dt;
            roll = roll + gyroX*dt;
            yaw = yaw + gyroZ*dt;

            //시계방향으로 돌렸을 때 29.5 -> 23.2가 됨
            //반올림해서 6이 한바퀴라고 가정하면
            //1.5 ~ 2 사이 변하면 ㅇㅋ
            Log.d("LOG", "GYROSCOPE"
                    + "           [P]: " + String.format("%.1f", pitch)
                    + "           [R]: " + String.format("%.1f", roll)
                    + "           [Y]: " + String.format("%.1f", yaw)
                    + "           [X]:" + String.format("%.4f", event.values[0])
                    + "           [Y]:" + String.format("%.4f", event.values[1])
                    + "           [Z]:" + String.format("%.4f", event.values[2])
                    + "           [Pitch]: " + String.format("%.1f", pitch*RAD2DGR)
                    + "           [Roll]: " + String.format("%.1f", roll*RAD2DGR)
                    + "           [Yaw]: " + String.format("%.1f", yaw*RAD2DGR)
                    + "           [dt]: " + String.format("%.4f", dt));

            setData.putDouble("rotationX",rotationX);
            setData.putDouble("rotationY",rotationY);
            setData.putDouble("rotationZ",rotationZ);


            Message degreeOfRotation = stateHandler.obtainMessage();
            degreeOfRotation.setData(setData);

            stateHandler.sendMessage(degreeOfRotation);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("testCode sensor", sensor.getName());
    }
}
