package com.example.shareloc.adaptater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.shareloc.Class.User;

import java.util.List;

public class UserListAdapter extends FriendListAdapter {

    public UserListAdapter(Context context, List<User> users, String currentUserId, Class<?> sourceActivityClass) {
        super(context, users, currentUserId, sourceActivityClass);
    }

    public void updateData(List<User> newUsers) {
        clear();
        addAll(newUsers);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
