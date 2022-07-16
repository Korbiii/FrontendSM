package com.android.brogrammers.sportsm8.ZZOldClassers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 11/18/2016.
 */

/**
 * Contains same fundamental function as DBconnnection; Provides functionality for activities to access database
 * dynamically through the UIthread interface
 */

//#######################
    //should have static update method that only change sharedPreferences! (Database.updateSharedPrefs)
//######################
public class Database extends AsyncTask<String, String, String>{

    public static final int CONNECTION_TIMEOUT=5000;
    public static final int READ_TIMEOUT=5000;
    private UIthread uiThread;
    private Context context;
    private String filename,function;

    public Database(UIthread thread, Context context){
        uiThread = thread;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * params[1] = xyz.php, params[2] and params[3] and the rest are pairs of key & value
     * @param params
     * @return
     */
    protected String doInBackground(String... params){

        String success = "";
        HttpURLConnection conn;
        filename = params[0].substring(0,params[0].length()-4);
        function = params[2];
        Uri.Builder builder = new Uri.Builder();
        for(int i=1; i<params.length; i+=2){
            builder.appendQueryParameter(params[i], params[i+1]);
        }
        String query = builder.build().getEncodedQuery();

        try {
            //http://10.0.2.2:8080/android_user_api/Backend/
            //URL url = new URL("http://sportsm8.bplaced.net:80/MySQLadmin/include/"+params[0]);
            URL url = new URL("http://sportsm8.bplaced.net:80/php/dynamicphp/include/"+params[0]);
            System.out.println(url.toString());

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout((READ_TIMEOUT));
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            conn.connect();

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = reader.readLine()) != null){
                success += line;
                //System.out.println(line+"<--- line");
            }
            System.out.println(success);
            //JsonParser parser = new JsonParser();
            //JsonObject gson = (JsonObject) parser.parse(success);

            conn.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }
    protected void onPostExecute(String success){
        //save database information locally
        // updateUI(success)

        SharedPreferences sharedPrefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(filename+function+"JSON", success);
        editor.apply();


        uiThread.updateUI();
        JSONObject jsonObject = null;
        String phpSuccess="";
        try {
            jsonObject = new JSONObject(success);
            phpSuccess  = jsonObject.get("success").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        uiThread.updateUI(phpSuccess);
    }

    public static List<Information> jsonToArrayList(String json) throws JSONException, ParseException {
        List<Information> data = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        int i=0;
        while(jsonObject.has(""+i)) {
            // in the php return JSON each meeting object is identified by an Index. Now they will be turned into a String one after another
            String meetingString = jsonObject.get(""+i).toString();
            Gson gson = new Gson();
            // current Meeting is created from meetingString according to requirements of Information.class
            Information currentMeeting = gson.fromJson(meetingString, Information.class);
            data.add(currentMeeting);
            i++;
        }
        return data;
    }
}
