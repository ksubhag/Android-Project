package com.example.myapplicationdb;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Display extends Activity {

    EditText ID;
    String myJSON;
    String decryptedString1,decryptedString2,currentdatabase,seedValue,id;
    RadioGroup rg;
    RadioButton rb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
//        ID =(EditText)findViewById(R.id.editText11);
        rg =(RadioGroup)findViewById(R.id.rg_id);
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton:
                if (checked)
                    seedValue="YourKey";
                    currentdatabase="http://112.133.242.248/mypage/selectall.php";
                    id = "111";
                    Toast.makeText(getApplicationContext(), "DeviceID 111 Select",Toast.LENGTH_SHORT).show();
                    break;
            case R.id.radioButton2:
                if (checked)
                    seedValue="YourKey1";
                    currentdatabase="http://112.133.242.248/mypage1/selectall1.php";
                    id = "222";
                    Toast.makeText(getApplicationContext(), "DeviceID 222 Select",Toast.LENGTH_SHORT).show();
                    break;
        }
    }
    public void onButtonClick(View v) {

                class GetDataJSON extends AsyncTask<String, Void, String> {

                    @Override
                    protected String doInBackground(String... params) {
                        DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                        HttpPost httppost = new HttpPost(currentdatabase);
                      // HttpPost httppost = new HttpPost("http://112.133.242.248/mypage/selectall.php");
                        // Depends on your web service
                        httppost.setHeader("Content-type", "application/json");

                        InputStream inputStream = null;
                        String result = null;
                        try {
                            HttpResponse response = httpclient.execute(httppost);
                            HttpEntity entity = response.getEntity();

                            inputStream = entity.getContent();
                            // json is UTF-8 by default
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                            StringBuilder sb = new StringBuilder();

                            String line = null;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            result = sb.toString();
                        } catch (Exception e) {
                            // Oops
                        } finally {
                            try {
                                if (inputStream != null) inputStream.close();
                            } catch (Exception squish) {
                            }
                        }
                        return result;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        myJSON = result;
                        ShowMap();
                        Log.i("myresult", result);
                    }
                }
                GetDataJSON g = new GetDataJSON();
                g.execute();
                Intent i = new Intent(Display.this, MapsActivity.class);
                if (decryptedString1 != null || decryptedString2 != null) {
                    double d1 = Double.parseDouble(decryptedString1);
                    double d2 = Double.parseDouble(decryptedString2);

                    i.putExtra("e1", d1);
                    i.putExtra("e2", d2);
                    i.putExtra("e3",id);
                    startActivity(i);
                }
    }
    private void ShowMap() {

        try {
            JSONArray jArray = new JSONArray(myJSON);
            for(int i=0;i<jArray.length()-1;i++) {
                if (i == jArray.length() - 2) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    decryptedString1 = decryption("" +json_data.getString("Latitude"));
                    decryptedString2 = decryption(""+json_data.getString("Longitude"));
                    Log.i("Latitude Old:",decryptedString1);
                    Log.i("Longitude Old",decryptedString2);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private String decryption(String s) {
       // seedValue = "YourKey";
        String strDecryptedText="";
        String strEncryptedText =s;
        try {
            strDecryptedText = AESHelper.decrypt(seedValue, strEncryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDecryptedText;
    }
}
