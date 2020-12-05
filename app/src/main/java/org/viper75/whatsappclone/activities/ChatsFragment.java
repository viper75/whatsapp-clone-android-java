package org.viper75.whatsappclone.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.viper75.whatsappclone.databinding.ChatsFragmentLayoutBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class ChatsFragment extends Fragment {

    private ChatsFragmentLayoutBinding mChatsFragmentLayoutBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mChatsFragmentLayoutBinding = ChatsFragmentLayoutBinding.inflate(inflater, container, false);

        return mChatsFragmentLayoutBinding.getRoot();
    }
}