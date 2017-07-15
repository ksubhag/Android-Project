package com.example.myapplicationdb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    public EditText ET_name,ET_surname,ET_age,ET_user,ET_pass;
    String nam,snam,age,use,pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        ET_name=(EditText)findViewById(R.id.editText4);
        ET_surname =(EditText)findViewById(R.id.editText5);
        ET_age =(EditText)findViewById(R.id.editText6);
        ET_user =(EditText)findViewById(R.id.editText7);
        ET_pass =(EditText)findViewById(R.id.editText8);

    }
    public void userReg(View view)
    {
         nam=ET_name.getText().toString();
        snam =ET_surname.getText().toString();
        age =ET_age.getText().toString();
        use =ET_user.getText().toString();
        pass =ET_pass.getText().toString();

        String type ="register";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type,nam,snam,age,use,pass);
        Toast.makeText(getApplicationContext(),"Registration Completed. \n You can Login with Username and Password." ,Toast.LENGTH_LONG).show();
    }
}
