package org.viper75.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.viper75.whatsappclone.R;
import org.viper75.whatsappclone.databinding.PhoneLoginActivityLayoutBinding;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private PhoneLoginActivityLayoutBinding mPhoneLoginActivityLayoutBinding;
    private TextInputEditText mPhoneNumberInput;
    private TextInputEditText mVerificationCodeInput;
    private Button mVerifyPhoneNumberBtn;
    private boolean mVerificationCodeSent;
    private String mVerificationId;
    private FirebaseAuth mFirebaseAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
//            mVerificationCodeInput.setVisibility(View.INVISIBLE);
//            mPhoneNumberInput.setVisibility(View.VISIBLE);

            mVerifyPhoneNumberBtn.setText(getString(R.string.send_verification_code));
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            mVerificationCodeSent = true;
            mVerificationId = verificationId;
            mResendToken = token;
//
//            mVerificationCodeInput.setVisibility(View.VISIBLE);
//            mPhoneNumberInput.setVisibility(View.INVISIBLE);

            mVerifyPhoneNumberBtn.setText(getString(R.string.verify_number));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPhoneLoginActivityLayoutBinding = PhoneLoginActivityLayoutBinding.inflate(getLayoutInflater());
        setContentView(mPhoneLoginActivityLayoutBinding.getRoot());

        mFirebaseAuth = FirebaseAuth.getInstance();

        initializeViews();
    }

    private void initializeViews() {
        mPhoneNumberInput = mPhoneLoginActivityLayoutBinding.phoneNumberInput;
        mVerificationCodeInput = mPhoneLoginActivityLayoutBinding.verificationCodeInput;

        mVerifyPhoneNumberBtn = mPhoneLoginActivityLayoutBinding.verifyPhoneNoBtn;
        mVerifyPhoneNumberBtn.setOnClickListener(this::verifyPhoneNumber);
    }

    private void verifyPhoneNumber(View view) {
        if (!mVerificationCodeSent) {
            sendVerificationCode();
        } else {
            String code = Objects.requireNonNull(mVerificationCodeInput.getText()).toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void sendVerificationCode() {
        String phoneNumber = Objects.requireNonNull(mPhoneNumberInput.getText()).toString();

        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberInput.setError("Phone number is required.");
            return;
        }

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mFirebaseAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Account created successfully.", Toast.LENGTH_LONG).show();
                        sendUserToMainActivity();
                    } else {
                        String message = Objects.requireNonNull(task.getException()).getMessage();
                        assert message != null;
                        Snackbar.make(mPhoneLoginActivityLayoutBinding.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG);
                    }
                });
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}