package com.practice.justkeep.logmodule;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Logger.v("onCreate");
        ///Logger.setAllowReportBug("http://183.232.129.66:8083/ads/bugreport");
        Logger.configBugReport("http://183.232.129.66:8083/ads/bugreport",getExternalCacheDir().getAbsolutePath());
        Logger.sendBugReport(new Logger.BugReport(new NullPointerException("")){

            @Override
            Map<String, String> getAdditionalParams() {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("time",System.currentTimeMillis()+"");
                return hashMap;
            }

            @Override
            String getBugKey() {
                return "stackTrace";
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
