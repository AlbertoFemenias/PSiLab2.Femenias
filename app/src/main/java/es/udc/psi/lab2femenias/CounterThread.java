package es.udc.psi.lab2femenias;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

class CounterThread extends Thread {
     int count;
     int wait;
    private Handler serviceHandler;
    private Handler threadHandler;

    @SuppressLint("HandlerLeak")
    CounterThread(int count, int wait, Handler handler) {

        this.count = count;
        this.wait = wait;
        this.serviceHandler = handler;

        threadHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1)
                {
                    Bundle bundle = msg.getData();
                     int newCount= bundle.getInt("newCount");
                     int newWait= bundle.getInt("newWait");
                     reset(newCount,newWait);
                }
                if(msg.what == 2)
                {
                    //IMPORTANTE
                    //As of 4.2 android, stop() is deprecated so all I can do is end the run() by exisiting the loop:
                    reset(-1,-1);
                }
            }
        };
    }


    public Handler getThreadHandler (){
        return threadHandler;
    }

    public void reset(int count, int wait){
        this.count = count;
        this.wait = wait;
    }

    public void run() {

        Log.d("bindThread", "A new bindThread appeared");
        for (count=count; count >= 0; count--) {
            Log.d("bindThread", "countDown: " + count);

            // Create a message in child thread.
            Message childThreadMessage = new Message();
            childThreadMessage.what = 1;
            Bundle bundle = new Bundle();
            bundle.putInt("count", count);
            bundle.putInt("wait", wait);
            childThreadMessage.setData(bundle);

            // Put the message in main thread message queue.
            serviceHandler.sendMessage(childThreadMessage);


            SystemClock.sleep(wait);
        }
        cancel();
    }

    public void cancel() {
        interrupt();
    }

}

