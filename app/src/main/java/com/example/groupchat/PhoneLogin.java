package com.example.groupchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

import okio.Timeout;

public class PhoneLogin extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText edtPhone, edtOTP;
    Button verifyOTPBtn, generateOTPBtn;
    String verificationId;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);


        mAuth = FirebaseAuth.getInstance();
        edtPhone = findViewById(R.id.idEdtPhoneNumber);
        edtOTP = findViewById(R.id.idEdtOtp);
        verifyOTPBtn = findViewById(R.id.idBtnVerify);
        generateOTPBtn = findViewById(R.id.idBtnGetOtp);
        loadingBar=new ProgressDialog(this);

        generateOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPhone.setVisibility(View.INVISIBLE);
                generateOTPBtn.setVisibility(View.INVISIBLE);
                edtOTP.setVisibility(View.VISIBLE);
                verifyOTPBtn.setVisibility(View.VISIBLE);
                if(TextUtils.isEmpty(edtPhone.getText().toString())){
                    Toast.makeText(PhoneLogin.this, "Please enter phone number", Toast.LENGTH_SHORT).show();
                }
                else {
                    loadingBar.setTitle("Phone Verfication");
                    loadingBar.setMessage("Please wait, while we are authenticating your phone...");
                   loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    String phone = edtPhone.getText().toString();
                    sendVerificationCode(phone);
                }
            }
        });
        verifyOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(edtOTP.getText().toString())){
                    Toast.makeText(PhoneLogin.this, "OTP box must not be empty!", Toast.LENGTH_SHORT).show();
                }
                else {
                    loadingBar.setTitle(" Verfication Code ");
                    loadingBar.setMessage("Please wait, while we are Verifying Verfication Code ...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    verifyCode(edtOTP.getText().toString());
                }
            }
        });
    }

    private void sendVerificationCode(String phone) {

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBack)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack =  new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            loadingBar.dismiss();
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            final String code = phoneAuthCredential.getSmsCode();
            edtOTP.setText(code);

            verifyCode(code);

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
loadingBar.dismiss();
            Toast.makeText(PhoneLogin.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    };

    private void verifyCode(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        signInWithCrediatial(credential);
    }

    private void signInWithCrediatial(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();
                    Intent in = new Intent(PhoneLogin.this, MainActivity2.class);
                    startActivity(in);
                    Toast.makeText(PhoneLogin.this, "Congratulations, You're Logged in Successfully..", Toast.LENGTH_SHORT).show();

                    finish();
                }else {

                    Toast.makeText(PhoneLogin.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
