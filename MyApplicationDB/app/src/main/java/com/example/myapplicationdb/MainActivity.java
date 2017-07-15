package com.example.myapplicationdb;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    public EditText username;
    EditText password;
    Button login_but;
    Button register_but;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = (EditText)findViewById(R.id.editText);
        password = (EditText)findViewById(R.id.editText2);
        login_but = (Button)findViewById(R.id.button);
        register_but = (Button)findViewById(R.id.button5);
    }

    public void onclick(View view)
    {
        String Username = username.getText().toString();
        String Password = password.getText().toString();
        String type = "login";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, Username, Password);
        startActivity( new Intent(this,MapControlActivity.class));
        Toast.makeText(getApplicationContext(),"Login Successful" ,Toast.LENGTH_SHORT).show();
    }
    public void register(View view)
    {
        startActivity( new Intent(this,RegisterActivity.class));
    }
}
