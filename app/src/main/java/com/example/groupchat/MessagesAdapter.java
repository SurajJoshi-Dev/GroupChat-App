package com.example.groupchat;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Messages> userMessagesList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;

    public MessagesAdapter( List<Messages> userMessagesList){
        this.userMessagesList=userMessagesList;
    }
    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView sendermMessageText,receiverMessageText;
        CircleImageView receiverUserProfileImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sendermMessageText=itemView.findViewById(R.id.sender_message_text);
            receiverMessageText=itemView.findViewById(R.id.receiver_message_text);
            receiverUserProfileImage=itemView.findViewById(R.id.message_profile_image);

        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view= LayoutInflater.from(parent.getContext())
        .inflate(R.layout.custom_messages_layout,parent,false);

firebaseAuth=FirebaseAuth.getInstance();
return  new MessageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
String messageSenderId=firebaseAuth.getCurrentUser().getUid();
Messages messages=userMessagesList.get(position);

String fromUserId=messages.getFrom();
String fromMessageType=messages.getType();

usersRef=FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
usersRef.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        if(snapshot.hasChild("image"))
        {
            String receiverImage=snapshot.child("image").getValue().toString();
            Picasso.get().load(receiverImage).placeholder(R.drawable.avt).into(holder.receiverUserProfileImage);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});
if ((fromMessageType.equals("text")))
{
    holder.receiverMessageText.setVisibility(View.INVISIBLE);
    holder.receiverUserProfileImage.setVisibility(View.INVISIBLE);
    holder.sendermMessageText.setVisibility(View.INVISIBLE);


}
if ((fromUserId.equals(messageSenderId)))
{
    holder.sendermMessageText.setVisibility(View.VISIBLE);

    holder.sendermMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
    holder.sendermMessageText.setTextColor(Color.BLACK);
    holder.sendermMessageText.setText(messages.getMessage());
}
else {

    holder.receiverMessageText.setVisibility(View.VISIBLE);
    holder.receiverUserProfileImage.setVisibility(View.VISIBLE);

    holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
    holder.receiverMessageText.setTextColor(Color.BLACK);
    holder.receiverMessageText.setText(messages.getMessage());
}
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


}
