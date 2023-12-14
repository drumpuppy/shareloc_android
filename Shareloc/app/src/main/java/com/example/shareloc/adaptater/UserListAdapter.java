package com.example.shareloc.adaptater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.example.shareloc.Class.User;
import java.util.List;

public class UserListAdapter extends FriendListAdapter {

    public UserListAdapter(Context context, List<User> users, String currentUserId) {
        super(context, users, currentUserId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = super.getView(position, convertView, parent);
        // Additional customization can be done here if needed
        return listItemView;
    }
}