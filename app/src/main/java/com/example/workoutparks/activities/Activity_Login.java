package com.example.workoutparks.activities;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.workoutparks.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Activity_Login extends Activity_Base {

    private enum LOGIN_STATE{
        ENTERING_NUMBER,
        ENTERING_CODE
    }
    private TextInputLayout login_EDT_phone;
    private MaterialButton login_BTN_continue;
    private LOGIN_STATE login_state = LOGIN_STATE.ENTERING_NUMBER;
    private String phone_input = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findView();
        initViews();
    }

    private void findView() {
        login_BTN_continue = findViewById(R.id.login_BTN_continue);
        login_EDT_phone = findViewById(R.id.login_EDT_phone);
    }

    private void initViews() {
        login_BTN_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueClicked();
            }
        });
    }

    private void continueClicked() {
        if(login_state == LOGIN_STATE.ENTERING_NUMBER){
            startLoginProcess();
        } else if(login_state == LOGIN_STATE.ENTERING_CODE){
            codeEntered();
        }
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks OnVerificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            login_state = LOGIN_STATE.ENTERING_CODE;
            updateUI();
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            e.printStackTrace();
            Toast.makeText(Activity_Login.this, "VerificationFailed" + e.getMessage(), Toast.LENGTH_SHORT).show();
            login_state = LOGIN_STATE.ENTERING_NUMBER;
            login_EDT_phone.setError("Invalid number!");
            updateUI();
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            super.onCodeAutoRetrievalTimeOut(s);
        }
    };

    private void codeEntered() {
        login_EDT_phone.setError(null);
        String smsVerificationCode = login_EDT_phone.getEditText().getText().toString();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuthSettings firebaseAuthSettings = firebaseAuth.getFirebaseAuthSettings();
        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phone_input, smsVerificationCode);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone_input)
                .setTimeout(10L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(OnVerificationStateChangedCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void startLoginProcess() {
        login_EDT_phone.setError(null);
        phone_input = login_EDT_phone.getEditText().getText().toString();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone_input)
                .setTimeout(10L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(OnVerificationStateChangedCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent myIntent = new Intent(Activity_Login.this, Activity_Home.class);
                            startActivity(myIntent);
                            finish();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                login_EDT_phone.setError("Wrong code!");
                                updateUI();
                            }
                        }
                    }
                });
    }

    private void updateUI() {
        if(login_state == LOGIN_STATE.ENTERING_NUMBER){
            login_EDT_phone.setHint(getString(R.string.phone_number));
            login_EDT_phone.setPlaceholderText("+972 55 1234567");
            login_BTN_continue.setText(getString(R.string.continue_));
        } else if (login_state == LOGIN_STATE.ENTERING_CODE){
            login_EDT_phone.getEditText().setText("");
            login_EDT_phone.setHint(getString(R.string.enter_code));
            login_EDT_phone.setPlaceholderText("******");
            login_BTN_continue.setText(getString(R.string.login));
        }
    }

}