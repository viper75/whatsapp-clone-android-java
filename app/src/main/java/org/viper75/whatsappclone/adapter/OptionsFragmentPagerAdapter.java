package org.viper75.whatsappclone.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.viper75.whatsappclone.fragments.CallsFragment;
import org.viper75.whatsappclone.fragments.CameraFragment;
import org.viper75.whatsappclone.fragments.ChatsFragment;
import org.viper75.whatsappclone.fragments.StatusFragment;

public class OptionsFragmentPagerAdapter extends FragmentPagerAdapter {

    public OptionsFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CameraFragment();
            case 1:
                return new ChatsFragment();
            case 2:
                return new StatusFragment();
            case 3:
                return new CallsFragment();
        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 1:
                return "CHATS";
            case 2:
                return "STATUS";
            case 3:
                return "CALLS";
        }
        return super.getPageTitle(position);
    }

    @Override
    public int getCount() {
        return 4;
    }
}
