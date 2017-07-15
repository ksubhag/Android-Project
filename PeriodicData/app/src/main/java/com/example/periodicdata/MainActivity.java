package com.example.periodicdata;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    ImageView image1,image2;
    int High,Low,Timesettingvalue,Distancesettingvalue;
    public String logi1,lati1,myJSON,myJSON1;
    TextView txtLogitude = null;
    TextView txtLatitude = null,textView,textView16;
    Location myLocation = null;
    Button button1;
    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    public String Sensor_Status,Relay_Status,strDate,encryptedString1,encryptedString2,Battrystatus,IDdevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtLatitude = (TextView)findViewById(R.id.textView9);
        txtLogitude = (TextView)findViewById(R.id.textView11);
        textView = (TextView) findViewById(R.id.textView3);
        textView16 =(TextView) findViewById(R.id.textView16);
        button1 = (Button) findViewById(R.id.button);
        image2=(ImageView)findViewById(R.id.imageView2);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            textView.setText("Internet connection is ON");
        }
        else {
            Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
        }

        String command="sh /sdcard/gpio/initgpio.sh";
        String value = getSystemCommandOutput(command);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLocation = location;
                if(location!=null) {
                    Log.d("Location:","Not Null");
                    logi1 = String.valueOf(location.getLongitude());
                    lati1 = String.valueOf(location.getLatitude());
                    txtLatitude.setText("" + location.getLatitude());
                    txtLogitude.setText("" + location.getLongitude());
                }
                else {
                    Log.d("Location:","Null");
                }

            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(LOCATION_PERMS, 1340);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1L, 1f, locationListener);
        myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(myLocation!=null) {
            Log.d("Location:","Not Null-End");
            txtLatitude.setText("" + myLocation.getLatitude());
            txtLogitude.setText("" + myLocation.getLongitude());
            Toast.makeText(getApplicationContext(),"GPS gets Locked" ,Toast.LENGTH_LONG).show();
            button1.setEnabled(true);
        }
        else {
            Toast.makeText(getApplicationContext(),"GPS not Locked" ,Toast.LENGTH_LONG).show();
            button1.setEnabled(false);
        }

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Handler handler = new Handler();
                Runnable run = new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        strDate = sdf.format(calendar.getTime());
                        Relay_Status = Integer.toString(High);
                        Sensor_Status = Integer.toString(Low);
                        if (lati1 != null || logi1 != null) {
                            encryptedString1 = encryption(lati1);
                            encryptedString2 = encryption(logi1);
                            Log.i("Encrypt1", encryptedString1);
                            Log.i("Encrypt2", encryptedString2);
                        }

                        int level = (int) battaryLevel();
                        if (level >75)
                        {
                            image2.setImageResource(R.drawable.bat_100);
                            Battrystatus=String.valueOf(100);
                        }
                        else if (level > 50 && level <= 75)
                        {
                            image2.setImageResource(R.drawable.bat_75);
                            Battrystatus=String.valueOf(75);
                        }
                        else if (level > 25 && level <= 50)
                        {
                            image2.setImageResource(R.drawable.bat_50);
                            Battrystatus=String.valueOf(50);
                        }else if (level > 10 && level <= 25)
                        {
                            image2.setImageResource(R.drawable.bat_25);
                            Battrystatus=String.valueOf(25);
                        }else
                        {
                            image2.setImageResource(R.drawable.bat_0);
                            Battrystatus=String.valueOf(0);
                        }
                        IDdevice = String.valueOf(111);
                        String method = "senddata";
                        BackgroundTask backgroundTask = new BackgroundTask(MainActivity.this);
                        backgroundTask.execute(method, IDdevice, encryptedString1, encryptedString2, Relay_Status, Sensor_Status, Battrystatus, strDate);

                        Log.i("DATA", "Every 1 min");
                        handler.postDelayed(this,60*1000);

                    }
                };
                handler.post(run);
                Toast.makeText(getApplicationContext(),"Tracking Of Devices Gets Started" ,Toast.LENGTH_LONG).show();
            }
            private String encryption(String s) {
                String seedValue = "YourKey";
                String normalTextEnc="";
                String strNormalText =s;
                try {
                   normalTextEnc = AESHelper.encrypt(seedValue, strNormalText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return normalTextEnc;
            }
            private float battaryLevel(){
                Intent battaryTntent = registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                int level = battaryTntent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
                int scale = battaryTntent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);

                if (level == -1 || scale == -1){
                    return 50.00f;
                }
                return ((float) level/(float) scale)*100.f;
            }
        });
    }
    public static String getSystemCommandOutput(String command){
        BufferedReader reader = null;
        String result = "";
        System.out.println("the command to be executed is :"+command);
        try {
            Process p = Runtime.getRuntime().exec(command);
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null){
                result += line + "\n";
                System.out.println("This seems to be the result "+result);
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public void ledon(View view) {
        System.out.println("Turning On LED");
        String command="sh /sdcard/gpio/ledon.sh";
        String value = getSystemCommandOutput(command);
        High =1;
        ImageView image1 =(ImageView)findViewById(R.id.imageView);
        image1.setImageResource(R.drawable.ledon);
    }
    public void ledoff(View view) {
        System.out.println("Turning Off LED");
        String command="sh /sdcard/gpio/ledoff.sh";
        String value = getSystemCommandOutput(command);
        Low =0;
        ImageView image1 =(ImageView)findViewById(R.id.imageView);
        image1.setImageResource(R.drawable.ledoff);
    }
        public void readpin(View view) {
        String command="cat /sys/class/gpio/gpio914/value";
        String value = getSystemCommandOutput(command);
        System.out.println("This is value :"+value);
        textView16.setText(value);
    }
    public void refresh(View view) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httppost = new HttpPost("http://112.133.242.248/mypage/ledgetvalue.php");
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
                relaystatus();
                Log.i("string",myJSON);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();

        class GetDataSetting extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient1 = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httppost1 = new HttpPost("http://112.133.242.248/mypage/ledsettingvalue.php");
                // Depends on your web service
                httppost1.setHeader("Content-type", "application/json");
                InputStream inputStream1 = null;
                String result = null;
                try {
                    HttpResponse response = httpclient1.execute(httppost1);
                    HttpEntity entity = response.getEntity();

                    inputStream1 = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader1 = new BufferedReader(new InputStreamReader(inputStream1, "UTF-8"), 8);
                    StringBuilder sb1 = new StringBuilder();

                    String line = null;
                    while ((line = reader1.readLine()) != null) {
                        sb1.append(line + "\n");
                    }
                    result = sb1.toString();
                } catch (Exception e) {
                    // Oops
                } finally {
                    try {
                        if (inputStream1 != null) inputStream1.close();
                    } catch (Exception squish) {
                    }
                }
                return result;
            }
            @Override
            protected void onPostExecute(String result) {
                myJSON1 = result;
                settingstatus();
                Log.i("string",myJSON1);
            }
        }
        GetDataSetting g1 = new GetDataSetting();
        g1.execute();
    }

    private void settingstatus() {
        try {
            JSONArray jArray1 = new JSONArray(myJSON1);
            for(int i=0;i<jArray1.length()-1;i++) {
                if (i == jArray1.length() - 2) {
                    JSONObject json_data1 = jArray1.getJSONObject(i);
                    String Timevalue = "" + json_data1.getString("TimeInterval");
                    String Distancevalue = "" + json_data1.getString("DistanceInterval");
                    Log.i("Time",Timevalue);
                    Log.i("Distance",Distancevalue);
                    Timesettingvalue = Integer.valueOf(Timevalue);
                    Distancesettingvalue = Integer.valueOf(Distancevalue);
                    Toast.makeText(getApplicationContext(), "Time is set to " +Timevalue +" and Distance is set to " +Distancevalue,Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void relaystatus() {
        try {
            JSONArray jArray = new JSONArray(myJSON);
            for(int i=0;i<jArray.length()-1;i++) {
                if (i == jArray.length() - 2) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    String relaystatus = "" + json_data.getString("Relay");
                    Log.i("relay",relaystatus);
                    int relay = Integer.valueOf(relaystatus);
                    if (relay == 1)
                    {
                        System.out.println("Turning On LED");
                        String command="sh /sdcard/gpio/ledon.sh";
                        String value = getSystemCommandOutput(command);
                        High =1;
                        image1 =(ImageView)findViewById(R.id.imageView);
                        image1.setImageResource(R.drawable.ledon);
                    }
                    else
                    {
                        System.out.println("Turning Off LED");
                        String command="sh /sdcard/gpio/ledoff.sh";
                        String value = getSystemCommandOutput(command);
                        Low =0;
                        image1 =(ImageView)findViewById(R.id.imageView);
                        image1.setImageResource(R.drawable.ledoff);
                    }
                }
                Toast.makeText(getApplicationContext(), "Relay Status Updated",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
