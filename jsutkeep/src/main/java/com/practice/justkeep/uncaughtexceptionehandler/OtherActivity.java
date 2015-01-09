package com.practice.justkeep.uncaughtexceptionehandler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;

import com.practice.justkeep.R;
import com.practice.justkeep.logger.Logger;

/**
 * Created by taofu on 2015/1/9.
 */
public class OtherActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.d("OtherActivity oncrate");
        int i = 1/0;
       final Thread.UncaughtExceptionHandler exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Logger.d("OtherActivity getDefaultUncaughtExceptionHandler = %s",exceptionHandler.hashCode());
       Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Logger.d("OtherActivity  exception handler .... uncaughtException thread id = %s" ,thread.getId());
                //exceptionHandler.uncaughtException(thread,ex);
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    //TODO
                   // exceptionHandler.uncaughtException(thread,ex);
                }else{
                    if(exceptionHandler != null){
                        exceptionHandler.uncaughtException(thread,ex);
                    }
                }


            }
        });
      new Thread(new Runnable() {
          @Override
          public void run() {
              Logger.d("produce exception thread id = %s " ,Thread.currentThread().getId());
              int i = 100/0;
          }
      }).start();




    }
}
