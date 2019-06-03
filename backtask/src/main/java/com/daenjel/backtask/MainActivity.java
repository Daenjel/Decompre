package com.daenjel.backtask;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MEMORY";
    TextView textView ;
    ProcessBuilder processBuilder;
    String Holder = "";
    String[] DATA = {"/system/bin/cat", "/proc/cpuinfo"};
    InputStream inputStream;
    Process process;
    byte[] byteArry;
    Button button;
    IntentFilter intentfilter;
    int deviceStatus;
    String currentBatteryStatus="Battery Info";
    int batteryLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        button = findViewById(R.id.buttonBatteryStatus);

        byteArry = new byte[1024];

        try{
            processBuilder = new ProcessBuilder(DATA);

            process = processBuilder.start();

            inputStream = process.getInputStream();

            while(inputStream.read(byteArry) != -1){

                Holder = Holder + new String(byteArry);
            }

            inputStream.close();

        } catch(IOException ex){

            ex.printStackTrace();
        }

        //textView.setText(Holder);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                doSomethingMemoryIntensive();
            }
        });
    }
    public void BatteryLife(){

        intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        MainActivity.this.registerReceiver(broadcastreceiver,intentfilter);

    }

    private BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            deviceStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS,-1);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryLevel=(int)(((float)level / (float)scale) * 100.0f);

            if(deviceStatus == BatteryManager.BATTERY_STATUS_CHARGING){

                textView.setText(currentBatteryStatus+" = Charging at "+batteryLevel+" %");

            }

            if(deviceStatus == BatteryManager.BATTERY_STATUS_DISCHARGING){

                textView.setText(currentBatteryStatus+" = Discharging at "+batteryLevel+" %");

            }

            if (deviceStatus == BatteryManager.BATTERY_STATUS_FULL){

                textView.setText(currentBatteryStatus+"= Battery Full at "+batteryLevel+" %");

            }

            if(deviceStatus == BatteryManager.BATTERY_STATUS_UNKNOWN){

                textView.setText(currentBatteryStatus+" = Unknown at "+batteryLevel+" %");
            }


            if (deviceStatus == BatteryManager.BATTERY_STATUS_NOT_CHARGING){

                textView.setText(currentBatteryStatus+" = Not Charging at "+batteryLevel+" %");

            }

        }
    };

    @SuppressLint("NewApi")
    public void doSomethingMemoryIntensive() {

        // Before doing something that requires a lot of memory,
        // check to see whether the device is in a low memory state.
        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();

        if (!memoryInfo.lowMemory) {
            //Get VM Heap Size by calling:

            long total = Runtime.getRuntime().totalMemory();
            Log.i(TAG, " memoryInfo.RunTimeTotal " +total + "\n" );

            //Get Allocated VM Memory by calling:
            long allocated = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            Log.i(TAG, " memoryInfo.Allocated " + allocated + "\n" );

            //Get VM Heap Size Limit by calling:
            long limit = Runtime.getRuntime().maxMemory();
            Log.i(TAG, " memoryInfo.Limit " + limit + "\n" );

            //Get Native Allocated Memory by calling:
            long nativeMe = Debug.getNativeHeapAllocatedSize();
            Log.i(TAG, " memoryInfo.Native " + nativeMe + "\n" );

            textView.setText(String.valueOf(total));

            Log.i(TAG, " memoryInfo.availMem " + memoryInfo.availMem + "\n" );
            Log.i(TAG, " memoryInfo.lowMemory " + memoryInfo.lowMemory + "\n" );
            Log.i(TAG, " memoryInfo.threshold " + memoryInfo.threshold + "\n" );
            Log.i(TAG, " memoryInfo.total " + memoryInfo.totalMem + "\n" );
        }
    }

    // Get a MemoryInfo object for the device's current memory status.
    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

}
