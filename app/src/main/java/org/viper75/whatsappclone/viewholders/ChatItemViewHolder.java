package org.viper75.whatsappclone.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.viper75.whatsappclone.databinding.ChatItemBinding;

import lombok.Getter;

public class ChatItemViewHolder extends RecyclerView.ViewHolder {

    @Getter
    private ConstraintLayout chatItem;
    @Getter
    private TextView chatName;
    @Getter
    private TextView chatMessage;
    @Getter
    private TextView chatTime;
    @Getter
    private TextView chatMessageCount;

    public ChatItemViewHolder(@NonNull ChatItemBinding chatItemBinding) {
        super(chatItemBinding.getRoot());

        chatItem = chatItemBinding.getRoot();
        chatName = chatItemBinding.chatName;
        chatMessage = chatItemBinding.chatLastMsg;
        chatTime = chatItemBinding.chatTime;
        chatMessageCount = chatItemBinding.chatMsgsCount;
    }
}
