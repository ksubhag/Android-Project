package com.example.myapplicationdb;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MapControlActivity extends AppCompatActivity {

    private Button map_but;
    private  Button control_but;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_control);
        map_but = (Button)findViewById(R.id.button2);
        control_but = (Button)findViewById(R.id.button3);
    }

    public void onMap(View view)
    {
        startActivity( new Intent(this,Display.class));
    }

    public void oncontrol(View view)
    {
        startActivity( new Intent(this,Demotable.class));
    }

    public void setting(View view) {
        startActivity( new Intent(this,settingcontrol.class));
    }
}
