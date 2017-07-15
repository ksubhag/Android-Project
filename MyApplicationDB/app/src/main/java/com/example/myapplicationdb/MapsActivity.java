package com.example.myapplicationdb;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private Polyline route = null;
    private PolylineOptions routeOpts = null;
    ArrayList<LatLng> MarkerPoints;
    GoogleApiClient mGoogleApiClient;
    TextView DistanceDuration;
    String myJSON;
    String decryptedString1,decryptedString2;
    double num1,num2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        DistanceDuration = (TextView) findViewById(R.id.textViewtime);
        MarkerPoints = new ArrayList<>();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    String.valueOf(Manifest.permission.class))
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                if (MarkerPoints.size() > 5) {
                    MarkerPoints.clear();
                    mMap.clear();
                }
                MarkerPoints.add(point);

                MarkerOptions options = new MarkerOptions();

                options.position(point);

                if (MarkerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    options.title("Milestone 1");
                    Toast.makeText(getApplication(), "Your Milestone 1 Set", Toast.LENGTH_LONG).show();

                } else if (MarkerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    options.title("Milestone 2");
                    Toast.makeText(getApplication(), "Your Milestone 2 Set", Toast.LENGTH_LONG).show();

                } else if (MarkerPoints.size() == 3) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    options.title("Milestone 3");
                    Toast.makeText(getApplication(), "Your Milestone 3 Set", Toast.LENGTH_LONG).show();

                } else if (MarkerPoints.size() == 4) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    options.title("Milestone 4");
                    Toast.makeText(getApplication(), "Your Milestone 4 Set", Toast.LENGTH_LONG).show();

                }
                mMap.addMarker(options);
            }
        });
        Intent i = getIntent();
        num1 = i.getDoubleExtra("e1",0);
        num2 = i.getDoubleExtra("e2",0);

        LatLng latLng = new LatLng(num1,num2);
        mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(1000)
                .strokeColor(Color.GREEN)
                .fillColor(Color.TRANSPARENT));
        MarkerOptions markerOptions = new MarkerOptions();
        mMap.addMarker(markerOptions
                .position(latLng)
                .title("Your Location is This")
                 .snippet(""));
    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
       mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

    }
    public void Start(View view) {

        EditText start_ad = (EditText) findViewById(R.id.editText3);
        String start_st = start_ad.getText().toString();
        List<android.location.Address> adressList = null;

        if (!start_st.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                adressList = geocoder.getFromLocationName(start_st, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        android.location.Address address = adressList.get(0);
        LatLng latlngstart = new LatLng(address.getLatitude(), address.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latlngstart).title("Starting Point"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlngstart));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        EditText end_ad = (EditText) findViewById(R.id.editText4);
        String end_st = end_ad.getText().toString();
        List<android.location.Address> adressList2 = null;

        if (!end_st.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                adressList2 = geocoder.getFromLocationName(end_st, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        android.location.Address address2 = adressList2.get(0);
        LatLng latlngend = new LatLng(address2.getLatitude(), address2.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latlngend).title("Ending Point"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlngend));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        String url = getUrl(latlngstart, latlngend);
        Log.d("onMapClick", url.toString());
        FetchUrl FetchUrl = new FetchUrl();

        FetchUrl.execute(url);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlngstart));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }
    public void getcordinate(View view) {
        final Handler handler = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
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
                if (decryptedString1 != null || decryptedString2 != null) {
                    num1 = Double.parseDouble(decryptedString1);
                    num2 = Double.parseDouble(decryptedString2);
                    Log.i("Latitude d1:", String.valueOf(num1));
                    Log.i("Longitude d2", String.valueOf(num2));
                }

                LatLng currentlocation = new LatLng(num1,num2);
                onMyLocationChange(currentlocation);

                double m1 = MarkerPoints.get(0).latitude;
                double m2 = MarkerPoints.get(0).longitude;
                double m3 = MarkerPoints.get(1).latitude;
                double m4 = MarkerPoints.get(1).longitude;
                double m5 = MarkerPoints.get(2).latitude;
                double m6 = MarkerPoints.get(2).longitude;
                double m7 = MarkerPoints.get(3).latitude;
                double m8 = MarkerPoints.get(3).longitude;

                // lat1 and lng1 are the values of a previously stored location
                if (distance(num1, num2, m1, m2) < 0.01) {
                    Toast.makeText(getApplicationContext(), "You have reached to MileStone 1", Toast.LENGTH_LONG).show();
                }
                if (distance(num1, num2, m3, m4) < 0.01) {
                    Toast.makeText(getApplicationContext(), "You have reached to MileStone 2", Toast.LENGTH_LONG).show();
                }
                 if (distance(num1, num2, m5, m6) < 0.01) {
                    Toast.makeText(getApplicationContext(), "You have reached to MileStone 3", Toast.LENGTH_LONG).show();
                }
                if (distance(num1, num2, m7, m8) < 0.01) {
                    Toast.makeText(getApplicationContext(), "You have reached to MileStone 4", Toast.LENGTH_LONG).show();
                }
                Log.i("DATA", "Every 1 min");
                handler.postDelayed(this, 60 * 1000);
                Toast.makeText(getApplicationContext(), "Status Refresh", Toast.LENGTH_SHORT).show();

            }
        };
        handler.post(run);
    }
    public void onMyLocationChange(LatLng location) {
        if (routeOpts != null) {
            LatLng myLatLng = new LatLng(location.latitude, location.longitude);
            List<LatLng> points = route.getPoints();
            points.add(myLatLng);
            route.setPoints(points);
           // route.Inv;
        }
    }

    /** calculates the distance between two locations in MILES */
    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        return dist;
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

    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }
    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }



    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {

                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                //Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }


        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            String distance = "";
            String duration = "";
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
                DistanceDuration.setText("Distance:" + distance + ", Duration:" + duration);
                Log.d("onPostExecute", "onPostExecute lineoptions decoded");
            }
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}