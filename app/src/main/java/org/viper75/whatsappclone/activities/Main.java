package org.viper75.whatsappclone.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

import org.viper75.whatsappclone.R;
import org.viper75.whatsappclone.adapter.OptionsFragmentPagerAdapter;
import org.viper75.whatsappclone.databinding.MainBinding;

import java.util.Objects;

public class Main extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainBinding mainBinding = MainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        Toolbar mainToolbar = mainBinding.toolbar.getRoot();
        setSupportActionBar(mainToolbar);

        ViewPager fragmentsViewPager = mainBinding.fragmentsViewPager;
        OptionsFragmentPagerAdapter pagerAdapter = new OptionsFragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        fragmentsViewPager.setAdapter(pagerAdapter);

        TabLayout optionsTab = mainBinding.tabs;
        optionsTab.setupWithViewPager(fragmentsViewPager);
        Objects.requireNonNull(optionsTab.getTabAt(0)).setIcon(R.drawable.photo_camera_icon);

        LinearLayout linearLayout = (LinearLayout) ((LinearLayout) optionsTab.getChildAt(0)).getChildAt(0);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.weight = 0f;
        layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        linearLayout.setLayoutParams(layoutParams);

        optionsTab.selectTab(optionsTab.getTabAt(1));
    }
}