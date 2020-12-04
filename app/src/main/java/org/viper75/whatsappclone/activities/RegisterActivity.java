package org.viper75.whatsappclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.viper75.whatsappclone.databinding.RegisterActivityLayoutBinding;

public class RegisterActivity extends AppCompatActivity {

    private RegisterActivityLayoutBinding mRegisterActivityLayoutBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRegisterActivityLayoutBinding = RegisterActivityLayoutBinding.inflate(getLayoutInflater());
        setContentView(mRegisterActivityLayoutBinding.getRoot());
    }
}