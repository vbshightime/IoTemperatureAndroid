package com.example.webczar.takenotes;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.webczar.takenotes.Model.User;
import com.example.webczar.takenotes.SharedPreferanceHelper.SharedPreferanceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.widget.Toast.LENGTH_SHORT;


public class MainActivity extends AppCompatActivity implements View.OnClickListener  {
    private TextView tvTemp,tvHumid,tvSensor,tvDevice;
    private ProgressBar progress;
    private View viewTemp,viewHumid;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int TIMEOUT = 10000;
    public static final int CONN_TIMEOUT = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         tvTemp = findViewById(R.id.tv_temp_value);
         tvHumid = findViewById(R.id.tv_humid_value);
         tvSensor =findViewById(R.id.tv_sensor);
         tvDevice =findViewById(R.id.tv_device);
         viewHumid = findViewById(R.id.view_humid);
         viewTemp = findViewById(R.id.view_temp);
         viewTemp.setOnClickListener(this);
         viewHumid.setOnClickListener(this);
         progress =findViewById(R.id.progress_bar);
         progress.setVisibility(View.GONE);
         SharedPreferanceHelper sharedPreferance = SharedPreferanceHelper.getInstance(MainActivity.this);
         User user1 = new User();
         /*if (user1 != null){
             Log.d(TAG,"user not null");
             tvTemp.setText(sharedPreferance.getUserSavedValue().temp_value);
             tvHumid.setText(sharedPreferance.getUserSavedValue().humid_value);
             tvSensor.setText(sharedPreferance.getUserSavedValue().sensor);
             tvDevice.setText(sharedPreferance.getUserSavedValue().device);
         }*/
        //new Async().execute();
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        if (viewID == R.id.view_humid || viewID ==R.id.view_temp){
            refresh();
        }
    }

    private void refresh() {
        new Async().execute();
        Log.d(TAG,"Starting Async Request");
        Toast.makeText(MainActivity.this,"Starting AsyncRequest",LENGTH_SHORT).show();
    }


    private class Async extends AsyncTask<Void,String,String>{

        HttpURLConnection connection;
        URL url;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            progress.setVisibility(View.VISIBLE);
            try{
                url =new URL("http://192.168.1.102/Feed");

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d(TAG,"Malformed URL");
                return e.toString();
            }
            try{

                connection = (HttpURLConnection)url.openConnection();
                connection.setReadTimeout(TIMEOUT);
                connection.setConnectTimeout(CONN_TIMEOUT);
                connection.setRequestMethod("GET");
//                String content_type = connection.getContentType();
                String request_meathod = connection.getRequestMethod();
             //   Log.d(TAG,"content:"+ content_type);
                Log.d(TAG,"request:"+ request_meathod);
                //connection is taken as output(reading from JSON)
                //connection.setDoOutput(false);
            } catch (IOException e1) {
                e1.printStackTrace();
                return e1.toString();
            }

            try{
                int response_code =connection.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK ){

                    InputStream inputStream = connection.getInputStream();
                    //InputStream errorStream = connection.getErrorStream();
                    BufferedReader bufferedReader =new BufferedReader( new InputStreamReader(inputStream));
                   // BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                    StringBuilder jResult = new StringBuilder();
                    StringBuilder jError = new StringBuilder();
                    String line;
                    String error;

                    while((line = bufferedReader.readLine())!=null){
                        jResult.append(line);
                    }

                    /*while((error = errorReader.readLine()) != null){
                        jError.append(error);
                    }*/

                    //Log.d(TAG,jError.toString());
                    return (jResult.toString());
                }else {
                    Log.d(TAG,"no http Connection");
                    return ("unsuccessful");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            }finally {
                connection.disconnect();
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.setVisibility(View.GONE);
            if(s== null){
                Log.d(TAG,"String is null");
            }
            try{
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("temp");
                for (int i=0; i<jsonArray.length();i++){
                    tvTemp.setText(jsonArray.getString(0));
                }
                JSONArray jsonArray1 = jsonObject.getJSONArray("humid");
                for (int i=0; i<jsonArray1.length();i++){
                    tvHumid.setText(jsonArray1.getString(0));
                }
                tvDevice.setText(jsonObject.getString("device"));
                tvSensor.setText(jsonObject.getString("sensorType"));
                User user =new User();
                /*for (int i=0; i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    tvDevice.setText(jsonObject.getString("device"));
                    tvSensor.setText(jsonObject.getString("sensorType"));
                    tvTemp.setText(jsonObject.getString("temp"));
                    tvHumid.setText(jsonObject.getString("humid"));
                }*/

                user.device = tvDevice.getText().toString();
                user.sensor = tvSensor.getText().toString();
                user.temp_value = tvTemp.getText().toString();
                user.humid_value = tvHumid.getText().toString();

               SharedPreferanceHelper sharedPreferanceHelper = SharedPreferanceHelper.getInstance(MainActivity.this);
               sharedPreferanceHelper.saveUserInfo(user);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG,"JSON Exeption");
            }
        }
    }

}
