package org.viper75.whatsappclone.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.viper75.whatsappclone.adapter.ChatListAdapter;
import org.viper75.whatsappclone.databinding.ChatsFragmentBinding;

public class ChatsFragment extends Fragment {

    private ChatListAdapter chatListAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        chatListAdapter = new ChatListAdapter(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ChatsFragmentBinding chatsFragmentBinding = ChatsFragmentBinding
                .inflate(inflater, container, false);

        chatsFragmentBinding.chatListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        chatsFragmentBinding.chatListRv.setAdapter(chatListAdapter);
        return chatsFragmentBinding.getRoot();
    }
}