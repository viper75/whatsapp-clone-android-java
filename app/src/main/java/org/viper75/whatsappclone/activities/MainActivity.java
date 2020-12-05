package org.viper75.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.viper75.whatsappclone.R;
import org.viper75.whatsappclone.adapters.OptionsTabAdapter;
import org.viper75.whatsappclone.databinding.MainActivityLayoutBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private MainActivityLayoutBinding mMainActivityLayoutBinding;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainActivityLayoutBinding = MainActivityLayoutBinding.inflate(getLayoutInflater());
        setContentView(mMainActivityLayoutBinding.getRoot());

        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUser = mFirebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //Initialize layout views
        initializeViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mCurrentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            checkIfUserProfileSet();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_find_friends_action:
                break;
            case R.id.main_settings_action:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.main_logout_action:
                mFirebaseAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkIfUserProfileSet() {
        String uid = mCurrentUser.getUid();
        mDatabaseReference.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("username").exists()) {
                    sendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendUserToSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void initializeViews() {
        //Add AppBar
        Toolbar toolbar = mMainActivityLayoutBinding.mainActivityAppBar.mainAppBar;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);
        //Setup ViewPager
        OptionsTabAdapter optionsTabAdapter = new OptionsTabAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        ViewPager optionsViewPager = mMainActivityLayoutBinding.mainViewPager;
        optionsViewPager.setAdapter(optionsTabAdapter);
        //Setup TabLayout with ViewPager
        TabLayout optionsTabLayout = mMainActivityLayoutBinding.tabLayout;
        optionsTabLayout.setupWithViewPager(optionsViewPager);
    }
}