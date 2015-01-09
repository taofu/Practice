package com.practice.justkeep.browser;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by taofu on 2014/12/19.
 */
public class SubTabActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.i("Test", getClass().getName() + "onCreate");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Test",getClass().getName() + "onDestroy");
    }
}
