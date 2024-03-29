package com.android.brogrammers.sportsm8.userClasses;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.brogrammers.sportsm8.dataBaseConnection.apiServices.APIService;
import com.android.brogrammers.sportsm8.dataBaseConnection.APIUtils;
import com.android.brogrammers.sportsm8.MainActivity;
import com.android.brogrammers.sportsm8.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class RegisterActivity extends AppCompatActivity{

    private static final String TAG = "";
    protected EditText username;
    private EditText password;
    private EditText email;
    protected String enteredUsername;
    private APIService apiService = APIUtils.getAPIService();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.eUsername);
        password = findViewById(R.id.ePassword);
        email = findViewById((R.id.eEmail));

        mAuth = FirebaseAuth.getInstance();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registerButton:
                enteredUsername = username.getText().toString();
                String enteredPassword = password.getText().toString();
                String enteredEmail = email.getText().toString();

                if (enteredUsername.equals("") || enteredPassword.equals("") || enteredEmail.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Username, password, and email required", Toast.LENGTH_LONG).show();
                    return;
                }

                //request server authentication
                register();
                break;
            case R.id.cancel_button:
                Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    public void register() {
        enteredUsername = username.getText().toString();
        final String enteredPassword = password.getText().toString();
        final String enteredEmail = email.getText().toString();
        Log.d(TAG, enteredEmail);
        mAuth.createUserWithEmailAndPassword(enteredEmail, enteredPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            syncDatabases(enteredUsername,enteredEmail);
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            RegisterActivity.this.finish();
                        }

                        // ...
                    }
                });


    }
    public void syncDatabases(String enteredUsername,String enteredEmail){
        apiService.createNewaccount(enteredEmail, enteredUsername)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                });
    }


}

