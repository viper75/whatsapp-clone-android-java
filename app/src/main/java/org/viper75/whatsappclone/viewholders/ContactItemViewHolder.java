package org.viper75.whatsappclone.viewholders;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.viper75.whatsappclone.databinding.ContactItemLayoutBinding;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Getter;

@Getter
public class ContactItemViewHolder extends RecyclerView.ViewHolder {
    private ConstraintLayout mContactItem;
    private CircleImageView mProfileImage;
    private TextView mUsername;
    private TextView mStatus;

    public ContactItemViewHolder(@NonNull ContactItemLayoutBinding contactItemLayoutBinding) {
        super(contactItemLayoutBinding.getRoot());

        mContactItem = contactItemLayoutBinding.getRoot();
        mProfileImage = contactItemLayoutBinding.friendUsernameCiv;
        mUsername = contactItemLayoutBinding.friendUsernameTv;
        mStatus = contactItemLayoutBinding.friendStatusTv;
    }
}
