package es.udc.psi.lab2femenias;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    LocalService ls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToggleButton toggle = (ToggleButton) findViewById(R.id.but_local_serv);
        final EditText waitTime = findViewById(R.id.et_time);
        final EditText count = findViewById(R.id.et_count);



        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("2","The toggle is enabled");
                    int countInt = Integer.parseInt(count.getText().toString());
                    int waitInt = Integer.parseInt(waitTime.getText().toString());

                    //final AsyncTask localService =
                    ls = new LocalService();
                    ls.execute(countInt,waitInt);
                            //new  LocalService().execute(countInt,waitInt);
                } else {
                    Log.d("2","The toggle is disabled");
                    ls.cancel(true);
                    //localService.cancel(true);
                }
            }
        });

    }



}
