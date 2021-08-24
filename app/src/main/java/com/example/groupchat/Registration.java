package com.example.groupchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;

import Holders.User;

public class Registration extends AppCompatActivity {
    TextInputEditText fullname,email,password,cpassword;
    Button signupBtn;
    TextView login;
    ImageView backbutton;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore  database;
    ProgressDialog progressDialog;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

     //   getSupportActionBar().hide();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Creating New Account");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        firebaseAuth= FirebaseAuth.getInstance();
        database=FirebaseFirestore.getInstance();
        RootRef= FirebaseDatabase.getInstance().getReference();
        fullname=findViewById(R.id.name_txt);
        email=findViewById(R.id.RegisterEmail_txt);
        password=findViewById(R.id.RegisterPassword_txt);
        cpassword=findViewById(R.id.RgstrConfrmPassword_txt);

        signupBtn=findViewById(R.id.Signup_btn);
        login=findViewById(R.id.Reg_loginBtn);
        backbutton=findViewById(R.id.BackButton_btn);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registration.this,Login.class));
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2=new Intent(Registration.this,Login.class);
                startActivity(intent2);
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name, emailadd, pass, cpass;
                emailadd = email.getText().toString();
                pass = password.getText().toString();
                cpass = cpassword.getText().toString();
                name = fullname.getText().toString();

                User user = new User();
                user.setEmail(emailadd);
                user.setPass(pass);
                user.setCpass(cpass);
                user.setName(name);

                String pswrd = password.getText().toString();
                String cPass = cpassword.getText().toString();


                if (email.length() == 0) {
                    email.setError("Fill Enter Email Address");

                    //  Toast.makeText(this, "Please Enter Email Address", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                    email.setError("Please Enter Valid Email");
                } else if (!fullname.getText().toString().matches("^[\\p{L} .'-]+$")) {
                    fullname.setError("Please enter a valid character");
                }
                else if (pswrd.length() < 6) {
                    password.setError("Please Enter password minimum in 6 char");
                }
                else if (!pass.equals(cPass)) {
                    cpassword.setError("Both password are not Matched");
                }
                else {
                    progressDialog.show();

                    firebaseAuth.createUserWithEmailAndPassword(emailadd, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

//String deviceToken=FirebaseInstallations.getInstance().getToken();
//                                FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener {
//                                    fbToken = it.result!!.token
//                                    // DO your thing with your firebase token
//                                }
                                String currentUserID=firebaseAuth.getCurrentUser().getUid();
                                RootRef.child("Users").child(currentUserID).setValue("");
                               // Log.i("Hello", "success");

//                                RootRef.child("Users").child(currentUserID).child("device_token")
//                                        .setValue(deviceToken);

                                database.collection("Users")
                                        .document().set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {

                                            @Override

                                            public void onSuccess(Void aVoid) {

                                                SendUserToMainActivity2();

                                                Intent intent = new Intent(Registration.this, Login.class);
                                                startActivity(intent);
                                                Log.i("Hello", "create");
                                                progressDialog.dismiss();


                                            }

                                            private void SendUserToMainActivity2() {
                                                Intent intent = new Intent(Registration.this, MainActivity2.class);
                                               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }

                                        });
                                Toast.makeText(Registration.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                //success
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(Registration.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }


                        }
                    });
                }
            }
           // }
        });

    }
}

/*
    String pswrd = password.getText().toString();
    String cPass = cpassword.getText().toString();

                if (email.length() == 0) {
                        email.setError("Fill Enter Email Address");

                        //  Toast.makeText(this, "Please Enter Email Address", Toast.LENGTH_SHORT).show();
                        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                        email.setError("Please Enter Valid Email");
                        } else if (!fullname.getText().toString().matches("^[\\p{L} .'-]+$")) {
                        fullname.setError("Please enter a valid character");
                        } else if (pswrd.length() < 6) {
        password.setError("Please Enter password minimum in 6 char");
        } else if (!pass.equals(cPass)) {
        cpassword.setError("Both password are not Matched");
        } else {


 */