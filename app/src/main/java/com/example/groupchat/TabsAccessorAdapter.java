package com.example.groupchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import fragments.ChatsFragment;
import fragments.ContactFragment;
import fragments.GroupFragment;
import fragments.RequestFragment;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    public TabsAccessorAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public TabsAccessorAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                ChatsFragment chatsFragment=new ChatsFragment();
                return chatsFragment;

               case 1:
                GroupFragment groupFragment=new GroupFragment();
                return groupFragment;

                case 2:
                ContactFragment contactFragment=new ContactFragment();
                return contactFragment;

            case 3:
                RequestFragment requestFragment=new RequestFragment();
                return requestFragment;

                default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 4;
    }
    @Nullable
    @Override

    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "Chats";

            case 1:
                return "group";

            case 2:
                return "contact";
            case 3:
                return "Requests";

            default:
                return null;
        }
    }
}
