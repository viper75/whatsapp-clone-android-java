package org.viper75.whatsappclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.viper75.whatsappclone.R;
import org.viper75.whatsappclone.databinding.LoginActivityLayoutBinding;
import org.viper75.whatsappclone.utils.AlertDialogUtil;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private LoginActivityLayoutBinding mLoginActivityLayoutBinding;
    private FirebaseAuth mFirebaseAuth;
    private TextInputEditText mEmailInput;
    private TextInputEditText mPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginActivityLayoutBinding = LoginActivityLayoutBinding.inflate(getLayoutInflater());
        setContentView(mLoginActivityLayoutBinding.getRoot());

        mFirebaseAuth = FirebaseAuth.getInstance();

        initializeViews();
    }

    private void initializeViews() {
        mEmailInput = mLoginActivityLayoutBinding.emailInput;
        mPasswordInput = mLoginActivityLayoutBinding.passwordInput;

        TextView needAnAccount = mLoginActivityLayoutBinding.needAnAccountTv;
        needAnAccount.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        Button loginBtn = mLoginActivityLayoutBinding.loginBtn;
        loginBtn.setOnClickListener(this::login);
    }

    private void login(View view) {
        String email = Objects.requireNonNull(mEmailInput.getText()).toString();
        String password = Objects.requireNonNull(mPasswordInput.getText()).toString();

        if (TextUtils.isEmpty(email)) {
            mEmailInput.setError("Email is required.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordInput.setError("Password is required.");
            return;
        }

        AlertDialog progressDialog = AlertDialogUtil.getProgressDialog(this,
                R.string.signing_in,
                R.string.please_wait,
                false);

        progressDialog.show();
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();

                    if (task.isSuccessful()) {
                        startActivity(new Intent(this, MainActivity.class));
                        Toast.makeText(this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = Objects.requireNonNull(task.getException()).getMessage();
                        assert message != null;
                        Snackbar.make(mLoginActivityLayoutBinding.getRoot(), message,
                                BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                });
    }
}