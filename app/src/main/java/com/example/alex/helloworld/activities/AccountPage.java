package com.example.alex.helloworld.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.alex.helloworld.R;
import com.example.alex.helloworld.dialogs.EditUserInformation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

public class AccountPage extends AppCompatActivity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initiating the buttons
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Button editUserInformationButton = (Button) findViewById(R.id.edit_userinformation);
        fab.setOnClickListener(this);
        editUserInformationButton.setOnClickListener(this);

        //initiating the userInformation TextViews
        TextView usernameText = (TextView) findViewById(R.id.username);
        TextView email = (TextView) findViewById(R.id.email);

        //load userInformation from local database
        SharedPreferences sharedPrefs = getSharedPreferences("loginInformation", Context.MODE_PRIVATE);
        String username = sharedPrefs.getString("username","");
        usernameText.setText(username);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case R.id.edit_userinformation:
                //start new alert dialog
                System.out.println("edit user information");
                FragmentManager fm = getSupportFragmentManager();
                EditUserInformation editUserInformationDialog = new EditUserInformation();
                editUserInformationDialog.show(fm, "editUserInformation");
                break;
        }
    }

}
