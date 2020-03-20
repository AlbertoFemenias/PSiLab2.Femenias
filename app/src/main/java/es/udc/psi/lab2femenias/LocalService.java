package es.udc.psi.lab2femenias;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class LocalService extends Service {
    CounterThread thread;
    int count;
    int wait;
    private Handler serviceHandler;
    Context mainActivity;

    public void setCounter(int count, int wait){
        this.count = count;
        this.wait = wait;
    }

    public void getContext(Context mainActivity){
        this.mainActivity = mainActivity;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LocalService", "Received start id " + startId + ": " + intent);
        serviceHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 2)
                {
                    Intent intent = new Intent();
                    intent.setAction("es.udc.psi.lab2femenias.LOCAL_SERVICE_ENDED");
                    mainActivity.sendBroadcast(intent);
                }
            }
        };

        thread = new CounterThread(count, wait, serviceHandler);
        thread.start();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("LocalService", "onDestroy()");
        //IMPORTANTE
        //As of 4.2 android, stop() is deprecated so all I can do is end the run() by exisiting the loop:
        thread.reset(-1,-1);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Do not return IBinder ==> Local Service
    }
}
