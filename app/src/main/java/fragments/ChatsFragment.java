package fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import Holders.Contacts;

import com.example.groupchat.Chat;
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


public class ChatsFragment extends Fragment {
private View PrivateChatsView;
   private RecyclerView chatsList;
   private DatabaseReference ChatsRef,UserRef;
   private FirebaseAuth firebaseAuth;
   private String currentUserId;



    public ChatsFragment() {
        // Required empty public constructor

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        PrivateChatsView= inflater.inflate(R.layout.fragment_chats, container, false);
  chatsList=PrivateChatsView.findViewById(R.id.chats_list);
  chatsList.setLayoutManager(new LinearLayoutManager(getContext()));
  firebaseAuth=FirebaseAuth.getInstance();
  currentUserId=firebaseAuth.getCurrentUser().getUid();
  ChatsRef= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
UserRef=FirebaseDatabase.getInstance().getReference().child("Users");
   return PrivateChatsView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options=
        new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ChatsRef,Contacts.class)
                .build();

FirebaseRecyclerAdapter<Contacts,ChatsViewHolder> adapter=
        new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull ChatsViewHolder holder, int position, @NonNull Contacts model) {

         final String usersIds=getRef(position).getKey();
             final String[] retImage = {"default_image"};

         UserRef.child(usersIds).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists())
               {
                   if(snapshot.hasChild("image"))
                   {
                       retImage[0] =snapshot.child("image").getValue().toString();
                       Picasso.get().load(retImage[0]).into(holder.profileImage);
                   }
                   final String retName=snapshot.child("name").getValue().toString();
                   final String retStatus=snapshot.child("status").getValue().toString();
                   holder.userName.setText(retName);
                   holder.userStatus.setText("Last Seen: " + "\n" + "Date" + "Time");


                   if(snapshot.child("userState").hasChild("state")){
                       String state=snapshot.child("userState").child("state").getValue().toString();
                       String time=snapshot.child("userState").child("time").getValue().toString();
                       String date=snapshot.child("userState").child("date").getValue().toString();

                       if(state.equals("online"))
                       {
                           holder.userStatus.setText( "online");

                       }
                     else   if(state.equals("offline"))
                       {
                           holder.userStatus.setText("Last Seen: " + date + " "+ time);

                       }
                   }
                   else {
                       holder.userStatus.setText("offline");


                   }

                   holder.itemView.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           Intent intent=new Intent(getContext(), Chat.class);
                          intent.putExtra("visit_user_id",usersIds);
                          intent.putExtra("visit_user_name",retName);
                          intent.putExtra("visit_image",retImage[0]);
                           startActivity(intent);
                       }
                   });
               }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
               return new ChatsViewHolder(view);
            }
        };

   chatsList.setAdapter(adapter);
   adapter.startListening();
    }
    public static class ChatsViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView profileImage;
        TextView userName,userStatus;
        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage=itemView.findViewById(R.id.users_Profile_pic);
            userName=itemView.findViewById(R.id.user_profile_name);
            userStatus=itemView.findViewById(R.id.user_status);
        }
    }
}