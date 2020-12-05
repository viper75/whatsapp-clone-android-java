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

import org.viper75.whatsappclone.R;
import org.viper75.whatsappclone.databinding.GroupsFragmentLayoutBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class GroupsFragment extends Fragment {

    private GroupsFragmentLayoutBinding mGroupsFragmentLayoutBinding;
    private DatabaseReference mDatabaseReference;
    private ArrayAdapter<String> mGroupsListAdapter;
    private ArrayList<String> mGroupsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mGroupsFragmentLayoutBinding = GroupsFragmentLayoutBinding.inflate(inflater, container, false);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Groups");

        initializeViews();

        retrieveGroups();

        return mGroupsFragmentLayoutBinding.getRoot();
    }

    private void initializeViews() {
        ListView groupsLv = mGroupsFragmentLayoutBinding.getRoot();

        mGroupsListAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                android.R.layout.simple_list_item_1, mGroupsList);
        groupsLv.setAdapter(mGroupsListAdapter);
    }

    private void retrieveGroups() {
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();

                Set<String> groupsSet = new HashSet<>();
                while (iterator.hasNext()) {
                    groupsSet.add(iterator.next().getKey());
                }

                mGroupsList.clear();
                mGroupsList.addAll(groupsSet);

                mGroupsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}