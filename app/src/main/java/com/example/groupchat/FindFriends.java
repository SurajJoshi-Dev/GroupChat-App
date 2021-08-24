package com.example.groupchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import Holders.Contacts;
import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriends extends AppCompatActivity {

 private RecyclerView findFriendRecyclerView;
Toolbar mtoolbar;
private DatabaseReference UserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        findFriendRecyclerView=findViewById(R.id.find_friends_recyclerView);
        findFriendRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mtoolbar=findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(UserRef,Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts,FindFriendViewHolder> adapter=
        new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, int position, @NonNull Contacts model) {
               holder.username.setText(model.getName());
               holder.userStatus.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.avt).into(holder.profileImage);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String visit_user_id=getRef(position).getKey();
                        Intent intent=new Intent(FindFriends.this,Profile.class);
                        intent.putExtra("visit_user_id",visit_user_id);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
          FindFriendViewHolder viewHolder=new FindFriendViewHolder(view);
          return viewHolder;
            }
        };

       findFriendRecyclerView.setAdapter(adapter);
       adapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder{
TextView username,userStatus;
CircleImageView profileImage;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.user_profile_name);
            userStatus=itemView.findViewById(R.id.user_status);
            profileImage=itemView.findViewById(R.id.users_Profile_pic);
        }
    }

}