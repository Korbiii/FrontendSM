package com.example.alex.helloworld.Unused_Inactive;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alex.helloworld.LoginScreen;
import com.example.alex.helloworld.R;

public class MainActivity extends AppCompatActivity {

    private TextView text4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showDate();
        createTable();
    }
    private void showDate(){
        /*String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        text4 = (TextView)findViewById(R.id.textView4);

        text4.setText(currentDateTimeString);*/
    }
    private void createTable(){
        //LinearLayout dayOfWeek;
        //int resID;

        for(int j=0; j<2; j++) {  //causes problems, apparently
            //creating the days as vertically oriented linear layouts


            //day = "day"+j;
            //resID = getResources().getIdentifier(day, "id", getPackageName());
            //dayOfWeek = (LinearLayout)findViewById(resID);


            LinearLayout day = new LinearLayout(this);
            day.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);  //
            params.weight = 1;
            day.setLayoutParams(params);
            day.setId(1);
            day.setBackgroundColor(Color.CYAN);

            for (int i = 0; i < 13; i++) {
                /*LayoutParams parameters =
                        new LinearLayout.LayoutParams(
                                LayoutParams.FILL_PARENT,
                                LayoutParams.WRAP_CONTENT
                        );*/
                Button hour = new Button(this);
                hour.setText(i + ":00");
                //hour.setLayoutParams(params);
                day.addView(hour);
            }
            ((LinearLayout) findViewById(R.id.weekLayout)).addView(day);
        }
        Button logoutButton = new Button(this);
        logoutButton.setText("Logout");
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                startActivity(intent);
            }
        });
        ((LinearLayout) findViewById(1)).addView(logoutButton);
    }
}
