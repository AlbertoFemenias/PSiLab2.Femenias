package es.udc.psi.lab2femenias;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

public class LocalService extends AsyncTask<Integer, Integer,Void> {
    protected Void doInBackground(Integer... params) {
        Log.d("localserv","A new localService appeared");
        int count = params[0];
        int wait = params[1];

        for (int i = count; i > 0; i--) {
            if (isCancelled())
                    return null;
            publishProgress(i);
             //Log.d("localserv", "countDown: " + i);
            SystemClock.sleep(wait);
        }
        return null;
    }

    protected void onProgressUpdate(Integer... count) {
        Log.d("localserv", "countDown: " + count[0]);
    }

    protected void onPostExecute(Void result) {
        Log.d("localserv", "countDown ended");
    }
}