package org.viper75.whatsappclone.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.viper75.whatsappclone.adapters.ContactsAdapter;
import org.viper75.whatsappclone.databinding.ContactsFragmentLayoutBinding;
import org.viper75.whatsappclone.models.Contact;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ContactsFragment extends Fragment {

    private ContactsAdapter mContactsAdapter;
    private DatabaseReference mContactsDBRef;
    private DatabaseReference mUserssDBRef;
    private FirebaseUser mCurrentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mContactsDBRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(mCurrentUser.getUid());
        mUserssDBRef = FirebaseDatabase.getInstance().getReference().child("Users");

        ContactsFragmentLayoutBinding contactsFragmentLayoutBinding = ContactsFragmentLayoutBinding
                .inflate(inflater, container, false);

        mContactsAdapter = new ContactsAdapter(requireActivity());

        contactsFragmentLayoutBinding.getRoot().setAdapter(mContactsAdapter);
        contactsFragmentLayoutBinding.getRoot().setLayoutManager(new LinearLayoutManager(requireActivity()));

        retrieveContacts();

        return contactsFragmentLayoutBinding.getRoot();
    }

    private void retrieveContacts() {

        mContactsDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String key = dataSnapshot.getKey();

                    mUserssDBRef.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String uid = (String) snapshot.child("uid").getValue();
                            String username = (String) snapshot.child("username").getValue();
                            String status = (String) snapshot.child("status").getValue();

                            Contact contact = Contact.builder()
                                    .username(username)
                                    .uid(uid)
                                    .status(status)
                                    .build();

                            if (snapshot.child("image").exists()) {
                                String image = (String) snapshot.child("image").getValue();
                                contact.setImage(image);
                            }

                            mContactsAdapter.addContact(contact);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}