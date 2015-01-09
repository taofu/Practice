package com.practice.justkeep.uncaughtexceptionehandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.practice.justkeep.MainActivity;
import com.practice.justkeep.R;
import com.practice.justkeep.logger.Logger;

/**
 * Created by taofu on 2015/1/9.
 */
public class TextUnCaughtExceptionHandler extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("TextUnCaughtExceptionHandler oncrate");
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.activity_main ,null);
        setContentView(relativeLayout);
        Logger.d("TextUnCaughtExceptionHandler getDefaultUncaughtExceptionHandler = %s",Thread.getDefaultUncaughtExceptionHandler().hashCode());
        Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Logger.d("TextUnCaughtExceptionHandler exception handler .... uncaughtException");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(),"很抱歉程序出现异常",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }).start();


                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
              /*  Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);*/
            }
        };



      //  Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
        Logger.d("TextUnCaughtExceptionHandler set setDefaultUncaughtExceptionHandler = %s",exceptionHandler.hashCode());
        Button button = new Button(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OtherActivity.class));
            }
        });

        button.setText("start");

        relativeLayout.addView(button);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
