package org.viper75.whatsappclone.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.viper75.whatsappclone.activities.ChatsFragment;
import org.viper75.whatsappclone.activities.ContactsFragment;
import org.viper75.whatsappclone.activities.GroupsFragment;

public class OptionsTabAdapter extends FragmentPagerAdapter {
    public OptionsTabAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ChatsFragment();
            case 1:
                return new GroupsFragment();
            case 2:
                return new ContactsFragment();
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Chats";
            case 1:
                return "Groups";
            case 2:
                return "Contacts";
            default:
                return super.getPageTitle(position);
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
