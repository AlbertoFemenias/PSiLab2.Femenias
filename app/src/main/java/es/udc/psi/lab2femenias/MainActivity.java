package es.udc.psi.lab2femenias;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;


public class MainActivity extends AppCompatActivity  {

    AsyncCounter asyncCounter;
    LocalService localService;
    BoundService mBoundService;
    boolean mServiceBound = false;

    BroadcastReceiver boundServiceReceiver;
    BroadcastReceiver localServiceReceiver;
    IntentFilter boundFilter;
    IntentFilter localFilter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button askButton = findViewById(R.id.but_ask);
        Button clearButton = findViewById(R.id.but_clear);
        final ToggleButton localToggle =  findViewById(R.id.but_local_serv);
        ToggleButton asyncToggle =  findViewById(R.id.but_AsyncTask);
        final ToggleButton boundToggle =  findViewById(R.id.but_bind_serv);
        final EditText waitTimeEdit = findViewById(R.id.et_time);
        final EditText countEdit = findViewById(R.id.et_count);
        final TextView waitTimeTv = findViewById(R.id.tv_data);
        final TextView countTv = findViewById(R.id.tv_count);


        boundServiceReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("BROADCAST","RECEIVED");
                boundToggle.setChecked(false);
            }
        };

        localServiceReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("BROADCAST","RECEIVED");
                localToggle.setChecked(false);
            }
        };

        boundFilter = new IntentFilter();
        boundFilter.addAction("es.udc.psi.lab2femenias.BOUND_SERVICE_ENDED");
        registerReceiver(boundServiceReceiver,boundFilter);

        localFilter = new IntentFilter();
        localFilter.addAction("es.udc.psi.lab2femenias.LOCAL_SERVICE_ENDED");
        registerReceiver(localServiceReceiver,localFilter);


        asyncToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("2","The asyncToggle is enabled");
                    int countInt = Integer.parseInt(countEdit.getText().toString());
                    int waitInt = Integer.parseInt(waitTimeEdit.getText().toString());

                    asyncCounter = new AsyncCounter();
                    asyncCounter.execute(countInt,waitInt);
                } else {
                    Log.d("2","The asyncToggle is disabled");
                    asyncCounter.cancel(true);
                }
            }
        });

        localToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("2","The localToggle is enabled");
                    int countInt = Integer.parseInt(countEdit.getText().toString());
                    int waitInt = Integer.parseInt(waitTimeEdit.getText().toString());

                    localService = new LocalService();
                    Intent intent = new Intent(MainActivity.this, LocalService.class);
                    localService.setCounter(countInt, waitInt);
                    localService.onStartCommand(intent, Service.START_FLAG_RETRY,1);

                } else {
                    Log.d("2","The localToggle is disabled");
                    localService.onDestroy();
                }
            }
        });

        boundToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("2","The boundToggle is enabled");

                    if (!mServiceBound) {
                        Intent intent = new Intent(MainActivity.this, BoundService.class);
                        startService(intent);
                        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
                        int countInt = Integer.parseInt(countEdit.getText().toString());
                        int waitInt = Integer.parseInt(waitTimeEdit.getText().toString());

                        mBoundService.startCount(countInt,waitInt);
                    }else {
                        int countInt = Integer.parseInt(countEdit.getText().toString());
                        int waitInt = Integer.parseInt(waitTimeEdit.getText().toString());

                        mBoundService.startCount(countInt,waitInt);
                    }

                } else {
                    Log.d("2","The boundToggle is disabled");
                    if (mServiceBound) {
                        unbindService(mServiceConnection);
                        mServiceBound = false;
                    }

                }
            }
        });

        askButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mServiceBound) {
                    String count = Integer.toString(mBoundService.getCount());
                    String wait = Integer.toString(mBoundService.getWait());
                    countTv.setText(count);
                    waitTimeTv.setText(wait);
                }


            }
        });


        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (mServiceBound) {
                    int countInt = Integer.parseInt(countEdit.getText().toString());
                    int waitInt = Integer.parseInt(waitTimeEdit.getText().toString());
                    mBoundService.resetThread(countInt, waitInt);
                }

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MainActivity.this, BoundService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }
    protected ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BoundService.MyBinder myBinder = (BoundService.MyBinder) service;
            mBoundService = myBinder.getService();
            mServiceBound = true;
        }
    };

}



