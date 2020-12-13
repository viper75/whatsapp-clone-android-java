package org.viper75.whatsappclone.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.viper75.whatsappclone.R;
import org.viper75.whatsappclone.databinding.ContactItemLayoutBinding;
import org.viper75.whatsappclone.models.Contact;
import org.viper75.whatsappclone.viewholders.ContactItemViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactItemViewHolder> {

    private List<Contact> mContacts = new ArrayList<>();
    private final LayoutInflater mLayoutInflater;

    public ContactsAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ContactItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactItemViewHolder(ContactItemLayoutBinding.inflate(mLayoutInflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactItemViewHolder holder, int position) {
        Contact contact = mContacts.get(position);

        holder.getMUsername().setText(contact.getUsername());
        holder.getMStatus().setText(contact.getStatus());

        if (contact.getImage() != null && !TextUtils.isEmpty(contact.getImage())) {
            Picasso.get()
                    .load(contact.getImage())
                    .placeholder(R.drawable.default_profile_image)
                    .into(holder.getMProfileImage());
        }
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public void setContacts(List<Contact> contacts) {
        this.mContacts = contacts;
        notifyDataSetChanged();
    }

    public void addContact(Contact contact) {
        this.mContacts.add(contact);
        notifyDataSetChanged();
    }
}
