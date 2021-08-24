package fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import Holders.Contacts;
import com.example.groupchat.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContactFragment extends Fragment {
 private View ContactsView;
 private RecyclerView myContactsList;
 private DatabaseReference ContacsRef,UserRef;
 private FirebaseAuth firebaseAuth;
 private String currentUserId;

    public ContactFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

       ContactsView= inflater.inflate(R.layout.fragment_contact, container, false);
   myContactsList=ContactsView.findViewById(R.id.Contacts_list);
   myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));

   firebaseAuth=FirebaseAuth.getInstance();
   currentUserId=firebaseAuth.getCurrentUser().getUid();
   ContacsRef= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
   UserRef=FirebaseDatabase.getInstance().getReference().child("Users");
    return ContactsView;
    }

    @Override
    public void onStart() {

        super.onStart();
        FirebaseRecyclerOptions options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ContacsRef,Contacts.class)
                .build();

     final FirebaseRecyclerAdapter<Contacts,ContactsViewHolder>adapter
                =new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactsViewHolder holder, int position, @NonNull Contacts model) {
            String userIds=getRef(position).getKey();

            UserRef.child(userIds).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                   if(snapshot.exists()) {

                       if(snapshot.child("userState").hasChild("state")){
                           String state=snapshot.child("userState").child("state").getValue().toString();
                           String time=snapshot.child("userState").child("time").getValue().toString();
                           String date=snapshot.child("userState").child("date").getValue().toString();

                           if(state.equals("online"))
                           {
                               holder.onlineIcon.setVisibility(View.VISIBLE);

                           }
                           else   if(state.equals("offline"))
                           {
                               holder.onlineIcon.setVisibility(View.INVISIBLE);

                           }
                       }
                       else {
                           holder.onlineIcon.setVisibility(View.INVISIBLE);


                       }
                       if(snapshot.hasChild("image")){
                           String userImage=snapshot.child("image").getValue().toString();
                           String profileName=snapshot.child("name").getValue().toString();
                           String profileStatus=snapshot.child("status").getValue().toString();

                           holder.userName.setText(profileName);
                           holder.userStatus.setText(profileStatus);

                           Picasso.get().load(userImage).placeholder(R.drawable.avt).into(holder.profileImage);

                       }
                       else {
                           String profileName=snapshot.child("name").getValue().toString();
                           String profileStatus=snapshot.child("status").getValue().toString();

                           holder.userName.setText(profileName);
                           holder.userStatus.setText(profileStatus);
                       }
                   }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                ContactsViewHolder viewHolder=new ContactsViewHolder(view);
                return viewHolder;
            }
        };
     myContactsList.setAdapter(adapter);
     adapter.startListening();
    }
    public static class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView userName,userStatus;
        CircleImageView profileImage;
        ImageView onlineIcon;
        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.user_profile_name);
            userStatus=itemView.findViewById(R.id.user_status);

            profileImage=itemView.findViewById(R.id.users_Profile_pic);
onlineIcon=itemView.findViewById(R.id.user_online_status);
        }
    }
}