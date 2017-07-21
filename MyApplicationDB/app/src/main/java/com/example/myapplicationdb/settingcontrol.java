package com.example.myapplicationdb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class settingcontrol extends AppCompatActivity {
    EditText ID;
    public EditText edit1,edit2;
    String time1,distance1,id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingcontrol);
        edit1 =(EditText) findViewById(R.id.editText9);
        edit2 =(EditText) findViewById(R.id.editText10);
 //       ID =(EditText)findViewById(R.id.editText12);
    }

    public void sendvalue(View view) {
        time1=edit1.getText().toString();
        distance1=edit2.getText().toString();
        String method = "senddatato";
        BackgroundTask backgroundTask = new BackgroundTask(settingcontrol.this);
        backgroundTask.execute(method,time1,distance1,id);
        Toast.makeText(getApplicationContext(), "Time is set to " +time1 +" and Distance is set to " +distance1,Toast.LENGTH_LONG).show();
    }

    public void onRadioButtonClickedsett(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton11:
                if (checked)
                id = "111";
                Toast.makeText(getApplicationContext(), "DeviceID 111 Select",Toast.LENGTH_SHORT).show();
                break;
            case R.id.radioButton22:
                if (checked)
                id = "222";
                Toast.makeText(getApplicationContext(), "DeviceID 222 Select",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
