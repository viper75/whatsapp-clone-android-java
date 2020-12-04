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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.viper75.whatsappclone.R;
import org.viper75.whatsappclone.databinding.RegisterActivityLayoutBinding;
import org.viper75.whatsappclone.utils.AlertDialogUtil;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private RegisterActivityLayoutBinding mRegisterActivityLayoutBinding;
    private TextInputEditText mEmailInput;
    private TextInputEditText mPasswordInput;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRegisterActivityLayoutBinding = RegisterActivityLayoutBinding.inflate(getLayoutInflater());
        setContentView(mRegisterActivityLayoutBinding.getRoot());

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        initializeViews();
    }

    private void initializeViews() {
        mEmailInput = mRegisterActivityLayoutBinding.registerEmailInput;
        mPasswordInput = mRegisterActivityLayoutBinding.registerPasswordInput;

        TextView alreadyHaveAccountTv = mRegisterActivityLayoutBinding.alreadyHaveAnAccountTv;
        alreadyHaveAccountTv.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });

        Button createAccountBtn = mRegisterActivityLayoutBinding.createAccountBtn;
        createAccountBtn.setOnClickListener(this::createAccount);
    }

    private void createAccount(View view) {
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
                R.string.creating_account_title,
                R.string.creating_account_description,
                false);

        progressDialog.show();
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();

                    if (task.isSuccessful()) {
                        String userId = Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getUid();

                        mDatabaseReference.child("Users").child(userId).setValue("");

                        sendUserToMainActivity();
                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = Objects.requireNonNull(task.getException()).getMessage();
                        assert message != null;
                        Snackbar.make(mRegisterActivityLayoutBinding.getRoot(), message,
                                BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                });
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}