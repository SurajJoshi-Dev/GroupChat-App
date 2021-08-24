package fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import Holders.Contacts;
import com.example.groupchat.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestFragment extends Fragment {

    private View RequestFragmentView;
    private RecyclerView myReqList;
    private DatabaseReference chatReqRef ,UsersRef,ContactRef;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RequestFragmentView = inflater.inflate(R.layout.fragment_request, container, false);
       ContactRef=FirebaseDatabase.getInstance().getReference().child("Contacts");
        chatReqRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        firebaseAuth = FirebaseAuth.getInstance();
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        myReqList = RequestFragmentView.findViewById(R.id.chat_request_list);
        myReqList.setLayoutManager(new LinearLayoutManager(getContext()));

        return RequestFragmentView;
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(chatReqRef.child(currentUserId), Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, RequestsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull RequestsViewHolder holder, int position, @NonNull Contacts model) {
                        holder.itemView.findViewById(R.id.request_accept_button).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.request_cancel_button).setVisibility(View.VISIBLE);

final String list_user_id=getRef(position).getKey();
DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();

getTypeRef.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
       if(snapshot.exists())
       {
           String type=snapshot.getValue().toString();
           if(type.equals("received")){
               UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                      if(snapshot.hasChild("image")){
//                        final String requestUserName=snapshot.child("name").getValue().toString();
//                          final String requestUserStatus=snapshot.child("status").getValue().toString();
                          final String requestProfileImage=snapshot.child("image").getValue().toString();

//                          holder.userName.setText(requestUserName);
//                          holder.userStatus.setText(requestUserStatus);
                          Picasso.get().load(requestProfileImage).into(holder.profileImage);
                      }

                       final String requestUserName=snapshot.child("name").getValue().toString();
                       final String requestUserStatus=snapshot.child("status").getValue().toString();
                       holder.userName.setText(requestUserName);
                       holder.userStatus.setText("Wants to Connect With you.");

                      holder.itemView.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {
                              CharSequence options[]=new CharSequence[]{
                                    "Accept",
                                    "Cancel"
                              };
                              AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                              builder.setTitle( requestUserName +" Chat Request");

                              builder.setItems(options, new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialogInterface, int i) {
                                    if(i==0){


                                        ContactRef.child(currentUserId).child(list_user_id).child("Contact") //contact display
                                   .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {
                                         if(task.isSuccessful()){
                                             Log.i("Accept","Buton");
                                             ContactRef.child(list_user_id).child(currentUserId).child("Contact")
                                                     .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<Void> task) {
                                                     if(task.isSuccessful()){

                                                chatReqRef.child(currentUserId).child(list_user_id)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {

                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                             if(task.isSuccessful()){

                                                                 chatReqRef.child(list_user_id).child(currentUserId)
                                                                         .removeValue()
                                                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                             @Override
                                                                             public void onComplete(@NonNull Task<Void> task) {
                                                                                 if(task.isSuccessful()){
                                                                                     Toast.makeText(getContext(), " New Contact Saved", Toast.LENGTH_SHORT).show();
                                                                                 }
                                                                             }
                                                                         });
                                                             }
                                                            }
                                                        });
                                                     }
                                                 }
                                             });
                                         }
                                          }
                                      });
                                    }
                                    if(i==1){ //cancel button

                                        chatReqRef.child(currentUserId).child(list_user_id)
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if(task.isSuccessful()){
                                                            Log.i("cancel","Buton");
                                                            chatReqRef.child(list_user_id).child(currentUserId)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if(task.isSuccessful()){
                                                                                Toast.makeText(getContext(), " Contact Deleted", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                    }
                                  }
                              });
                              builder.show();
                          }
                      });
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });
           }
           else if(type.equals("sent"))
           {
               Button request_sent_btn=holder.itemView.findViewById(R.id.request_accept_button);
               request_sent_btn.setText("Request Sent");
               holder.itemView.findViewById(R.id.request_cancel_button).setVisibility(View.INVISIBLE);
               UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       if(snapshot.hasChild("image")){
//                        final String requestUserName=snapshot.child("name").getValue().toString();
//                          final String requestUserStatus=snapshot.child("status").getValue().toString();
                           final String requestProfileImage=snapshot.child("image").getValue().toString();

//                          holder.userName.setText(requestUserName);
//                          holder.userStatus.setText(requestUserStatus);
                           Picasso.get().load(requestProfileImage).into(holder.profileImage);
                       }

                       final String requestUserName=snapshot.child("name").getValue().toString();
                       final String requestUserStatus=snapshot.child("status").getValue().toString();
                       holder.userName.setText(requestUserName);
                       holder.userStatus.setText("You have to request to." +requestUserName);

                       holder.itemView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               CharSequence options[]=new CharSequence[]{
                                      "Cancel Chat Request"
                               };
                               AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                               builder.setTitle( "Already sent Request");

                               builder.setItems(options, new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialogInterface, int i) {

                                       if(i==0){ //cancel button

                                           chatReqRef.child(currentUserId).child(list_user_id)
                                                   .removeValue()
                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {

                                                           if(task.isSuccessful()){
                                                              // Log.i("cancel","Buton");
                                                               chatReqRef.child(list_user_id).child(currentUserId)
                                                                       .removeValue()
                                                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                           @Override
                                                                           public void onComplete(@NonNull Task<Void> task) {

                                                                               if(task.isSuccessful()){
                                                                                   Toast.makeText(getContext(), " You have cancelled the Chat request..", Toast.LENGTH_SHORT).show();
                                                                               }
                                                                           }
                                                                       });
                                                           }
                                                       }
                                                   });
                                       }
                                   }
                               });
                               builder.show();
                           }
                       });
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });
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
                    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                        RequestsViewHolder holder = new RequestsViewHolder(view);
                        return holder;
                    }
                };
        myReqList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class RequestsViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userStatus;
        CircleImageView profileImage;
        Button AcceptBtn, CancelBtn;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_Profile_pic);
            AcceptBtn = itemView.findViewById(R.id.request_accept_button);
            CancelBtn = itemView.findViewById(R.id.request_cancel_button);
        }
    }
}