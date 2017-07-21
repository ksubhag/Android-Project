package com.example.myapplicationdb;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.util.Log;

import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Demotable extends Activity {
    String myJSON;
    String decryptedString2;
    String decryptedString1,currentdatabase,seedValue;
    String sendLEDvalue;
    int ledstsatus,devicesatus;
    EditText ID;
    TextView txtView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demotable);
        txtView = (TextView) findViewById(R.id.textView23);
      //  ID =(EditText)findViewById(R.id.editTextID);
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton3);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ImageView image1 =(ImageView)findViewById(R.id.imageView3);
                    image1.setImageResource(R.drawable.ledon);
                    sendLEDvalue= String.valueOf(1);
                    String method = "senddata";
                    BackgroundTask backgroundTask = new BackgroundTask(Demotable.this);
                    backgroundTask.execute(method,sendLEDvalue);
                    Toast.makeText(getApplicationContext(), "Relay is set to " +sendLEDvalue,Toast.LENGTH_SHORT).show();
                } else {
                    ImageView image1 =(ImageView)findViewById(R.id.imageView3);
                    image1.setImageResource(R.drawable.ledoff);
                    sendLEDvalue= String.valueOf(0);
                    String method = "senddata";
                    BackgroundTask backgroundTask = new BackgroundTask(Demotable.this);
                    backgroundTask.execute(method,sendLEDvalue);
                    Toast.makeText(getApplicationContext(), "Relay is set to " +sendLEDvalue,Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void onRadioButtonClicked111(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton111:
                if (checked)
                    seedValue="YourKey";
                currentdatabase="http://112.133.242.248/mypage/selectall.php";
                Toast.makeText(getApplicationContext(), "DeviceID 111 Select",Toast.LENGTH_SHORT).show();
                break;
            case R.id.radioButton222:
                if (checked)
                    seedValue="YourKey1";
                currentdatabase="http://112.133.242.248/mypage1/selectall1.php";
                Toast.makeText(getApplicationContext(), "DeviceID 222 Select",Toast.LENGTH_SHORT).show();
                break;
        }

    }
    public void get(View view) {

        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httppost = new HttpPost(currentdatabase);
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
                showtable();
                Log.i("myresult", result);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }
    protected void showtable() {
        try
        {
            JSONArray jArray = new JSONArray(myJSON);
            String re =jArray.getString(jArray.length()-1);
            TableLayout tv=(TableLayout) findViewById(R.id.table);
            tv.removeAllViewsInLayout();
            int flag=1;
            for(int i=0;i<jArray.length()-1;i++)
            {
                String[] headers = new String[]{"DeviceID","Address","LED","Relay","Battery Status","Time"};
                TableRow tr=new TableRow(Demotable.this);
                tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                JSONObject json_data = jArray.getJSONObject(i);
                if(flag==1){
                    TableRow tr1=new TableRow(Demotable.this);
                    tr1.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    for(int x=0;x<6;x++) {
                        TextView textHeader = new TextView(this);
                        textHeader.setPadding(10,10,10,10);
                        textHeader.setTextColor(Color.BLUE);
                        textHeader.setTextSize(15);
                        textHeader.setGravity(Gravity.CENTER);
                        textHeader.setText(headers[x]);
                        tr1.addView(textHeader);
                    }
                    final View vline = new View(Demotable.this);
                    vline.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 2));
                    vline.setBackgroundColor(Color.RED);

                    tv.addView(tr1);
                }
                flag=0;
                TextView textViewID = new TextView(this);
                textViewID.setText(""+json_data.getInt("DeviceID"));
                tr.addView(textViewID);

                TextView textViewAdd = new TextView(this);
                    decryptedString1 = decryption("" + json_data.getString("Latitude"));
                    decryptedString2 = decryption("" + json_data.getString("Longitude"));
                    String Add = getAddress(Double.parseDouble(decryptedString1), Double.parseDouble(decryptedString2));
                    textViewAdd.setText(Add);
                    tr.addView(textViewAdd);
                    System.out.println(Add);

                TextView textViewLED = new TextView(this);
                textViewLED.setText(""+json_data.getInt("LED"));
                ledstsatus = +json_data.getInt("LED");
                tr.addView(textViewLED);

                TextView textViewRelay = new TextView(this);
                textViewRelay.setText(""+json_data.getInt("Relay"));
                devicesatus =+json_data.getInt("Relay");
                tr.addView(textViewRelay);

                TextView textViewBattery = new TextView(this);
                textViewBattery.setText(""+json_data.getInt("Batterystatus"));
                tr.addView(textViewBattery);

                TextView textViewTime = new TextView(this);
                textViewTime.setText(""+json_data.getString("Time"));
                tr.addView(textViewTime);

                tv.addView(tr);
                if(i==jArray.length()-2) {
                    ledstsatus = +json_data.getInt("LED");
                    devicesatus =+json_data.getInt("Relay");
                    if (ledstsatus == 1){
                        ImageView image1 =(ImageView)findViewById(R.id.imageView3);
                        image1.setImageResource(R.drawable.ledon);
                    }
                    else{
                        ImageView image1 =(ImageView)findViewById(R.id.imageView3);
                        image1.setImageResource(R.drawable.ledoff);
                    }
                    if (devicesatus == 1)
                        txtView.setText("ON");
                    else
                        txtView.setText("OFF");

                }
            }
        }
        catch(JSONException e)
        {
            Log.e("log_tag", "Error parsing data "+e.toString());
            Toast.makeText(getApplicationContext(), "JsonArray fail", Toast.LENGTH_SHORT).show();
        }
        Log.i("myresult", "showtable");
        Toast.makeText(getApplicationContext(),"Your Previous Tracking Record \n And sensor Status" ,Toast.LENGTH_LONG).show();
    }

    private String decryption(String s) {
       // String seedValue = "YourKey";
        String strDecryptedText="";
        String strEncryptedText =s;
        try {
            strDecryptedText = AESHelper.decrypt(seedValue, strEncryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDecryptedText;
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getSubLocality()).append(",");
                result.append(address.getLocality()).append(",");
                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }


}