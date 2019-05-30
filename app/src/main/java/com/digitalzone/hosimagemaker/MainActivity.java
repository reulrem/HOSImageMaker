package com.digitalzone.hosimagemaker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ImageView hosImage;

    SensorManager sm;
    Sensor mGgyroSensor;
    CheckDegreeOfRotationClass checkRotation;

    String TAG = "MainActivity";

    Context thisActivity;

    Handler rotationStateDetector = new Handler(){

        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
                double isTurn = msg.getData().getDouble("isTurn",0);
                if(isTurn > 2){
                    sm.unregisterListener(checkRotation);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Animation hosAnimation = AnimationUtils.loadAnimation(thisActivity, R.anim.hos);
                            hosImage.startAnimation(hosAnimation);
                            hosImage.setVisibility(View.VISIBLE);
                        }
                    });
                    Log.d(TAG,"hos");
                }
                else if(isTurn < -2){
                    sm.unregisterListener(checkRotation);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Animation hosAnimation = AnimationUtils.loadAnimation(thisActivity, R.anim.hos_reverse);
                            hosImage.startAnimation(hosAnimation);
                            hosImage.setVisibility(View.VISIBLE);
                        }
                    });
                    Log.d(TAG,"hos");
                }

                Log.d(TAG,String.valueOf(msg.getData().getDouble("rotationX",0)));
                Log.d(TAG,String.valueOf(msg.getData().getDouble("rotationY",0)));
                Log.d(TAG,String.valueOf(msg.getData().getDouble("rotationZ",0)));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thisActivity = this;

        hosImage = (ImageView) findViewById(R.id.hos);

        try{

            sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

            mGgyroSensor = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

            checkRotation = new CheckDegreeOfRotationClass(rotationStateDetector);
            sm.registerListener(checkRotation,mGgyroSensor,SensorManager.SENSOR_DELAY_UI);
        }catch (NullPointerException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(sm != null && checkRotation != null){
            sm.unregisterListener(checkRotation);
        }

    }


}
