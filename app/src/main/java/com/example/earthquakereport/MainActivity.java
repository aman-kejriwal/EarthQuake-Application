package com.example.earthquakereport;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    ArrayList<String > a=new ArrayList<>();
    ArrayList<HashMap<String, String>> contactList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contactList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
        new GetContacts().execute();
     }
    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"Json Data is downloading",Toast.LENGTH_LONG).show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?starttime=2022-06-20&endtime=2022-11-20&format=geojson&minmagnitude=4.5";
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("features");
                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        JSONObject properties = c.getJSONObject("properties");
                        String place=properties.getString("place");
                        int ind=place.indexOf("of");
                        String place1="",place2="";
                        if(ind!=-1){
                            place1=place.substring(0,ind+2);
                            place2=place.substring(ind+2,place.length());}
                        else {
                            place1="";
                            place2=place;
                          }
                        double mag=properties.getDouble("mag");
                        String magnitude=""+mag;
                        String alert="Alert: "+properties.getString("alert");
                        String url1=properties.getString("url");
                        a.add(url1);
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String url =a.get(position);
                                Uri uri = Uri.parse(url);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                // Verify that the intent will resolve to an activity
                                if (intent.resolveActivity(getPackageManager()) != null) {
                                    // Here we use an intent without a Chooser unlike the next example
                                    startActivity(intent);
                                }}
                        });
                        //finding time  yyyy-MM-dd HH:mm
                        long time=properties.getLong("time");
                        SimpleDateFormat dateformatter = new SimpleDateFormat("LLL dd, yyyy");
                        Date date = new Date(time);
                        String dt=dateformatter.format(date);
                        SimpleDateFormat timeformatter = new SimpleDateFormat("hh:mm a");
                        Date Time=new Date(time);
                        String tm=timeformatter.format(Time);
                        //
                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();
                        // adding each child node to HashMap key => value
                        contact.put("magnitude", magnitude);
                        contact.put("place", place2);
                        contact.put("date",dt);
                        contact.put("alert",alert);
                        contact.put("time",tm);
                        contact.put("place1",place1);
                        contactList.add(contact);
                    }
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String url =a.get(position);
                            Uri uri = Uri.parse(url);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            // Verify that the intent will resolve to an activity
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                // Here we use an intent without a Chooser unlike the next example
                                startActivity(intent);
                            }
                        }
                    });
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;}
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(MainActivity.this, contactList,
                    R.layout.list_item, new String[]{ "place","magnitude","date","alert","time","place1"},
                    new int[]{R.id.place2, R.id.magnitude,R.id.Date,R.id.Alert,R.id.time,R.id.place1});
            lv.setAdapter(adapter);
        }
    }
}
