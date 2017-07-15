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

    String myJSON;
    String decryptedString1,decryptedString2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
    }
    public void onButtonClick(View v) {
                class GetDataJSON extends AsyncTask<String, Void, String> {

                    @Override
                    protected String doInBackground(String... params) {
                        DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                        HttpPost httppost = new HttpPost("http://112.133.242.248/mypage/selectall.php");
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
        String seedValue = "YourKey";
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
