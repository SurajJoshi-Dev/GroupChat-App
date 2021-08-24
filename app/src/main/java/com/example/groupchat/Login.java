package com.example.groupchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;

import Holders.User;

public class Login extends AppCompatActivity {
    TextInputEditText emailadd, password;
    TextView forgetpassword, registernow;
    Button login ,PhoneLoginButton;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    FirebaseUser currentUser;
 //   DatabaseReference UserRef;

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(" Really Exit")
                .setCancelable(false)
                .setMessage("Do you really want to Exit the App?")
                .setPositiveButton("Yes Exit", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })

                .setNegativeButton("No Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.show();

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        getSupportActionBar().hide();


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login Account");
        progressDialog.setMessage("Please wait......");
        progressDialog.setCancelable(false);

        firebaseAuth = FirebaseAuth.getInstance();
     //   UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        currentUser=firebaseAuth.getCurrentUser();
        emailadd = findViewById(R.id.Email_txt);
        password = findViewById(R.id.Paswword_txt);
        forgetpassword = findViewById(R.id.ForgetPassword_txt);
        registernow = findViewById(R.id.RegistrNow_txt);
        login = findViewById(R.id.Login_btn);
        PhoneLoginButton=findViewById(R.id.Phone_Login_btn);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, pswrd;
                email = emailadd.getText().toString();
                pswrd = password.getText().toString();

                if (emailadd.length() == 0) {
                    emailadd.setError("Fill Enter Email Address");
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(emailadd.getText().toString()).matches()) {
                    emailadd.setError("Please Enter Valid Email");
                }
                if (pswrd.length() < 6) {
                    password.setError("Please Enter password minimum in 6 char");

                } else {
                    progressDialog.show();

                    firebaseAuth.signInWithEmailAndPassword(email, pswrd)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                            //            String currentUserId =firebaseAuth.getCurrentUser().getUid();
                                   //firebase Token
                                   //     Task<InstallationTokenResult> deviceToken= FirebaseInstallations.getInstance().getToken();

//                                        UserRef.child(currentUserId).child("device_token")
//                                                .setValue(deviceToken)
//                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                       if(task.isSuccessful())
//                                                       {
                                                       SendUserToMainActivity();

//                                                       }
//                                                    }
//                                                });


                                    } else {

                                        Log.i("Hello", "Error");

                                        Toast.makeText(Login.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }

                                }


                                private void SendUserToMainActivity() {
                                    Intent intent = new Intent(Login.this, MainActivity2.class);
                                   // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                  finish();
                                   // Log.i("Hello", "success");
                                    progressDialog.dismiss();
                                    Toast.makeText(Login.this, "Logged in Successful  ..", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        PhoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Login.this,PhoneLogin.class);
                startActivity(intent);
            }
        });

        registernow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
            }
        });

/*

        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Login.this,SignUp.class);
                startActivity(intent);
            }
        });



 */

    }
    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser!=null)
        {
            Intent intent = new Intent(Login.this, MainActivity2.class);
            startActivity(intent);
            finish();
        }
    }
}