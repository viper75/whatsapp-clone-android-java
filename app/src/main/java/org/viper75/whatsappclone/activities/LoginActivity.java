package org.viper75.whatsappclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import org.viper75.whatsappclone.databinding.LoginActivityLayoutBinding;

public class LoginActivity extends AppCompatActivity {

    private LoginActivityLayoutBinding mLoginActivityLayoutBinding;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginActivityLayoutBinding = LoginActivityLayoutBinding.inflate(getLayoutInflater());
        setContentView(mLoginActivityLayoutBinding.getRoot());

        initializeViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mCurrentUser != null) {
            sendUserToMainActivity();
        }
    }

    private void initializeViews() {
        TextView needAnAccount = mLoginActivityLayoutBinding.needAnAccountTv;
        needAnAccount.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}