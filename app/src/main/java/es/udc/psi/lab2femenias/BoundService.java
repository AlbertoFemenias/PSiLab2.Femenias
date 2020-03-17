package es.udc.psi.lab2femenias;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Binder;
import android.os.Message;
import android.util.Log;


public class BoundService extends Service {
    private static String LOG_TAG = "BoundService";
    private IBinder mBinder = new MyBinder();

    int currentCount = 0;
    int currentWait = 0;

    private Handler serviceHandler;
    private Handler threadHandler;


    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "in onCreate");
        serviceHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1)
                {
                    Bundle bundle = msg.getData();
                    currentCount = bundle.getInt("count");
                    currentWait = bundle.getInt("wait");
                    // Update view component text, this is allowed.
                    Log.v(LOG_TAG, "I have received a message, count: "+ currentCount + " wait: " + currentWait);
                }
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "in onBind");
        return mBinder;
    }
    @Override
    public void onRebind(Intent intent) {
        Log.v(LOG_TAG, "in onRebind");
        super.onRebind(intent);
    }
    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "in onUnbind");
        Message msg = new Message();
        msg.what = 2;
        threadHandler.sendMessage(msg);
        return true;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "in onDestroy");
    }
    public void startCount(int count, int wait) {
        CounterThread thread = new CounterThread(count, wait, serviceHandler);
        thread.start();
        threadHandler = thread.getThreadHandler();
    }

    public void resetThread(int newCount, int newWait) {
        Message msg = new Message();
        msg.what = 1;
        Bundle bundle = new Bundle();
        bundle.putInt("newCount", newCount);
        bundle.putInt("newWait", newWait);
        msg.setData(bundle);
        // Put the message in main thread message queue.
        threadHandler.sendMessage(msg);

    }

    public int getWait() {
        return currentWait;
    }

    public int getCount() {
        return currentCount;
    }

    public class MyBinder extends Binder {
        BoundService getService() {
            return BoundService.this;
        }
    }
    }
