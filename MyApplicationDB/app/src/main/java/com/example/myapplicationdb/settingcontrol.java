package com.example.myapplicationdb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class settingcontrol extends AppCompatActivity {

    public EditText edit1,edit2;
    String time1,distance1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingcontrol);
        edit1 =(EditText) findViewById(R.id.editText9);
        edit2 =(EditText) findViewById(R.id.editText10);

    }

    public void sendvalue(View view) {
        time1=edit1.getText().toString();
        distance1=edit2.getText().toString();
        String method = "senddatato";
        BackgroundTask backgroundTask = new BackgroundTask(settingcontrol.this);
        backgroundTask.execute(method,time1,distance1);
        Toast.makeText(getApplicationContext(), "Time is set to " +time1 +" and Distance is set to " +distance1,Toast.LENGTH_LONG).show();
    }
}
