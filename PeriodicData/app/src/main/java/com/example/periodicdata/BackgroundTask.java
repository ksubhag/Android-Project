package com.example.periodicdata;

/**
 * Created by Subhag on 07-May-17.
 */
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class BackgroundTask extends AsyncTask<String,Void,String> {

    public Context context;
    public AlertDialog alertDialog;
    BackgroundTask (MainActivity ctx) {
        context = (Context) ctx;
    }
    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String send_url = "http://112.133.242.248/mypage/status.php";
        if (type.equals("senddata")) {
            try {
                String DeviceID =params[1];
                String Latitude = params[2];
                String Longitude = params[3];
                String LED = params[4];
                String Relay = params[5];
                String Batterystatus = params[6];
                String Time = params[7];

                URL url = new URL(send_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data =URLEncoder.encode("DeviceID", "UTF-16") + "=" + URLEncoder.encode(DeviceID, "UTF-16") + "&"+
                        URLEncoder.encode("Latitude", "UTF-16") + "=" + URLEncoder.encode(Latitude, "UTF-16") + "&"+
                        URLEncoder.encode("Longitude", "UTF-16") + "=" + URLEncoder.encode(Longitude, "UTF-16") + "&"+
                        URLEncoder.encode("LED", "UTF-8") + "=" + URLEncoder.encode(LED, "UTF-8") + "&"+
                        URLEncoder.encode("Relay", "UTF-8") + "=" + URLEncoder.encode(Relay, "UTF-8") + "&" +
                        URLEncoder.encode("Batterystatus", "UTF-8") + "=" + URLEncoder.encode(Batterystatus, "UTF-8") + "&" +
                        URLEncoder.encode("Time", "UTF-8") + "=" + URLEncoder.encode(Time, "UTF-8");
                Log.i("DATA","Send");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        Log.i("DATA","Baground Task");
    }

    @Override
    protected void onPostExecute(String result) {
        alertDialog.setMessage(result);
        alertDialog.show();
        alertDialog.cancel();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
