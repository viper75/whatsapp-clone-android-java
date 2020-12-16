package org.viper75.whatsappclone.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.viper75.whatsappclone.R;
import org.viper75.whatsappclone.adapters.ContactsAdapter;
import org.viper75.whatsappclone.databinding.ContactItemLayoutBinding;
import org.viper75.whatsappclone.databinding.ContactsFragmentLayoutBinding;
import org.viper75.whatsappclone.models.Contact;
import org.viper75.whatsappclone.viewholders.ContactItemViewHolder;

public class ContactsFragment extends Fragment {

    private ContactsAdapter mContactsAdapter;
    private RecyclerView mContactsRv;
    private DatabaseReference mContactsDBRef;
    private DatabaseReference mUsersDBRef;
    private FirebaseUser mCurrentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mContactsDBRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(mCurrentUser.getUid());
        mUsersDBRef = FirebaseDatabase.getInstance().getReference().child("Users");

        ContactsFragmentLayoutBinding contactsFragmentLayoutBinding = ContactsFragmentLayoutBinding
                .inflate(inflater, container, false);

        mContactsRv = contactsFragmentLayoutBinding.getRoot();
        mContactsRv.setLayoutManager(new LinearLayoutManager(requireActivity()));

        return contactsFragmentLayoutBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(mContactsDBRef, Contact.class)
                .build();

        FirebaseRecyclerAdapter<Contact, ContactItemViewHolder> adapter = new FirebaseRecyclerAdapter<Contact, ContactItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactItemViewHolder holder, int position, @NonNull Contact model) {
                String contactId = getRef(position).getKey();

                Log.i(ContactsFragment.class.getCanonicalName(), contactId);
                mUsersDBRef.child(contactId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String uid = snapshot.child("uid").getValue().toString();
                        String username = snapshot.child("username").getValue().toString();
                        String status = snapshot.child("status").getValue().toString();

                        holder.getMUsername().setText(username);
                        holder.getMStatus().setText(status);

                        if (snapshot.child("image").exists()) {
                            String image = (String) snapshot.child("image").getValue();

                            Picasso.get()
                                    .load(image)
                                    .placeholder(R.drawable.default_profile_image)
                                    .into(holder.getMProfileImage());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ContactItemLayoutBinding contactItemLayoutBinding = ContactItemLayoutBinding
                        .inflate(getLayoutInflater(), parent, false);
                return new ContactItemViewHolder(contactItemLayoutBinding);
            }
        };

        mContactsRv.setAdapter(adapter);

        adapter.startListening();
    }
}