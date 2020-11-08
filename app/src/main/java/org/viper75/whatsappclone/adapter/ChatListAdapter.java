package org.viper75.whatsappclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.viper75.whatsappclone.databinding.ChatItemBinding;
import org.viper75.whatsappclone.models.Chat;
import org.viper75.whatsappclone.viewholders.ChatItemViewHolder;

public class ChatListAdapter extends RecyclerView.Adapter<ChatItemViewHolder> {

    private LayoutInflater mLayoutInflater;
    private Chat[] chats = {
            new Chat("Ian", "Apa i was free hangu", "14:08", "1"),
            new Chat("Faith", "Howu", "13:47", "1"),
            new Chat("Mussa", "Nduda how to get away with murder kana unayo", "13:33", "2"),
            new Chat("Ian", "Apa i was free hangu", "14:08", "1"),
            new Chat("Faith", "Howu", "13:47", "1"),
            new Chat("Mussa", "Nduda how to get away with murder kana unayo", "13:33", "2"),
            new Chat("Ian", "Apa i was free hangu", "14:08", "1"),
            new Chat("Faith", "Howu", "13:47", "1"),
            new Chat("Mussa", "Nduda how to get away with murder kana unayo", "13:33", "2"),
            new Chat("Ian", "Apa i was free hangu", "14:08", "1"),
            new Chat("Faith", "Howu", "13:47", "1"),
            new Chat("Mussa", "Nduda how to get away with murder kana unayo", "13:33", "2")
    };

    public ChatListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ChatItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatItemViewHolder(ChatItemBinding.inflate(mLayoutInflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatItemViewHolder holder, int position) {
        Chat chat = chats[position];

        holder.getChatName().setText(chat.getName());
        holder.getChatMessage().setText(chat.getMessage());
        holder.getChatTime().setText(chat.getTime());
        holder.getChatMessageCount().setText(chat.getUnreadCount());
    }

    @Override
    public int getItemCount() {
        return chats.length;
    }
}
