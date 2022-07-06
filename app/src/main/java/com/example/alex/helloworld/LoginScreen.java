package com.example.alex.helloworld;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.os.AsyncTask;

import android.widget.EditText;

import android.widget.Toast;

import org.json.JSONException;
import org.json.simple.JSONObject;

import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStream;

import java.io.InputStreamReader;

import android.app.ProgressDialog;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alex.helloworld.databaseConnection.Database;
import com.example.alex.helloworld.databaseConnection.UIthread;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LoginScreen extends AppCompatActivity implements UIthread {

    protected EditText username;
    private EditText password;
    protected String enteredUsername;
    private String dbReturn;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        username = (EditText) findViewById(R.id.eUsername);
        password = (EditText) findViewById(R.id.ePassword);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        Button registerButton = (Button) findViewById(R.id.registerButton);

        //try to read local database if a user is already logged in
        SharedPreferences sharedPrefs = getSharedPreferences("loginInformation", Context.MODE_PRIVATE);
        String loginJson = sharedPrefs.getString("islogin", "");
        String prevUsername = sharedPrefs.getString("username", "");
        System.out.println("logged in user is: " + prevUsername);

        //better check?
        if (!prevUsername.equals("") && loginJson.equalsIgnoreCase("1")) {
            System.out.println("session continued");
            Intent intent = new Intent(LoginScreen.this, Home.class);
            startActivity(intent);
            LoginScreen.this.finish();
        }

    }

    public void buttonClick(View v) throws ExecutionException, InterruptedException {
        switch (v.getId()){
            case R.id.loginButton: {
                enteredUsername = username.getText().toString();
                String enteredPassword = password.getText().toString();

                if (enteredUsername.equals("") || enteredPassword.equals("")) {
                    Toast.makeText(LoginScreen.this, "Username and password required", Toast.LENGTH_LONG).show();
                    return;
                }
                //request server authentication
                Intent intent = new Intent(LoginScreen.this, Home.class);
                startActivity(intent);
                LoginScreen.this.finish();
                //login();
                /*
                //request server authentification

                //new AsyncLogin().execute(enteredUsername, enteredPassword);
                String[] params = {"IndexAccounts.php", "function", "loginAccount", "username", enteredUsername, "password", enteredPassword};
                Database db = new Database(this, this.getApplicationContext());
                dbReturn = db.execute(params).get();
                System.out.println("CONNECTED TO DB");
                //
                //Getting nonsense from db currently
                //
                */
                break;
            }
            case R.id.registerButton: {
                Intent intent = new Intent(LoginScreen.this, RegisterActivity.class);
                startActivity(intent);
                LoginScreen.this.finish();
                break;
            }
            default: {
                break;
            }
        }
    }
    @Override
    public void updateUI(){
        try {
            ArrayList<Information> info = Database.jsonToArrayList(dbReturn);
            System.out.println("UPDATING UI");
            if(info.get(0).success == 0){
                Intent intent = new Intent(LoginScreen.this, Home.class);
                startActivity(intent);
                LoginScreen.this.finish();
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void login(){
            enteredUsername = username.getText().toString();
            String enteredPassword = password.getText().toString();

            String[] params = {"IndexAccounts.php", "function", "loginAccount", "username", enteredUsername, "password", enteredPassword };
            Database db = new Database(this, this.getApplicationContext());
            db.execute(params);
    }

    @Override
    public void updateUI(String successString){
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        String success ="";
        try {
            json = (JSONObject) parser.parse(successString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        success = Long.toString((Long) json.get("success"));
        System.out.println("SUCCESS: "+success);

        if(success.equalsIgnoreCase("1")){

            //save login information locally for session management
            //should the password be saved?
            SharedPreferences sharedPrefs = getSharedPreferences("loginInformation", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("username", enteredUsername);
            editor.putString("email","Korbi@korbi.de");
            // is already clear that success == 1
            editor.putString("islogin", success);
            editor.apply();

            //start home screen in case of successful login
            Intent intent = new Intent(LoginScreen.this, Home.class);
            startActivity(intent);
            LoginScreen.this.finish();
        }
        else if(success.equalsIgnoreCase("0")){
            Toast.makeText(LoginScreen.this, "Invalid username or password", Toast.LENGTH_LONG).show();
        }
        else if(success.equalsIgnoreCase("exception") || success.equalsIgnoreCase("unsuccessful")){
            Toast.makeText(LoginScreen.this, "Connection Problem", Toast.LENGTH_LONG).show();
        }
    }


/**not needed anymore
 *
    private class AsyncLogin extends AsyncTask<String, String, String>{
        ProgressDialog progressDialog = new ProgressDialog(LoginScreen.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //this method will be running on the UI thread
            progressDialog.setMessage("\tLoading");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params){
            try{
                //"http://10.0.2.2/android_user_api/Backend/index.php"
                url = new URL("http://sportsm8.bplaced.net:80/MySQLadmin/include/IndexAccounts.php");
            }
            catch(MalformedURLException e){
                e.printStackTrace();
                System.out.println("URL not found");
                return "exception";
            }

            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout((READ_TIMEOUT));
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                //setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                //Append parameters so they can be written
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);
                String query = builder.build().getEncodedQuery();

                //Open connection for sending data

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
            }
            catch(IOException e1){
                e1.printStackTrace();
                System.out.println("URL not found");
                return "exception";
            }
            try{
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder successString = new StringBuilder();
                String line;
                String success ="";

                while ((line = reader.readLine()) != null) {
                    successString.append(line);
                    System.out.println(successString.toString());
                }
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(successString.toString());
                    success = Long.toString((Long) json.get("success"));
                    System.out.println("SUCCESS: "+success);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return success;
            }
            catch(IOException e2) {
                e2.printStackTrace();
                return "exception";
            }
            finally{
                conn.disconnect();
            }

        }

        @Override
        protected void onPostExecute(String success){
            //this is running on the UI thread
            progressDialog.dismiss();

            if(success.equalsIgnoreCase("1")){
                Intent intent = new Intent(LoginScreen.this, Home.class);
                startActivity(intent);
                LoginScreen.this.finish();

            }
            else if(success.equalsIgnoreCase("")){
                Toast.makeText(LoginScreen.this, "Invalid username or password", Toast.LENGTH_LONG).show();

            }
            else if(success.equalsIgnoreCase("exception") || success.equalsIgnoreCase("unsuccessful")){
                Toast.makeText(LoginScreen.this, "Connection Problem", Toast.LENGTH_LONG).show();
            }
        }
    } */
}
