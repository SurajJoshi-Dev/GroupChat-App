package com.example.groupchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {

private String messageReceiverId, messageReceiverName, messageReceiverImage, messageSenderId;
private TextView userName, userLastSeen;
private CircleImageView userImage;
private Toolbar chatToolBar;
private ImageButton sendMessageButton;
private EditText MessageInputText;
FirebaseAuth firebaseAuth;
DatabaseReference RootRef;

private final List<Messages> messagesList=new ArrayList<>();

LinearLayoutManager linearLayoutManager;
MessagesAdapter messagesAdapter;
RecyclerView userMessageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseAuth=FirebaseAuth.getInstance();
        messageSenderId=firebaseAuth.getCurrentUser().getUid();
        RootRef=FirebaseDatabase.getInstance().getReference();

        messageReceiverId=getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName=getIntent().getExtras().get("visit_user_name").toString();
       messageReceiverImage=getIntent().getExtras().get("visit_image").toString();

       IntializeControllers();

       userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.avt).into(userImage);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              SendMessage();
            }
        });
    }

    private void IntializeControllers() {

       chatToolBar=findViewById(R.id.chat_toolbar);
       setSupportActionBar(chatToolBar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater= (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView=layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBarView);

        userImage=findViewById(R.id.custom_profile_IMage);
        userName=findViewById(R.id.custom_profile_name);
        userLastSeen=findViewById(R.id.custom_last_seen);

        sendMessageButton=findViewById(R.id.send_message_button);
        MessageInputText=findViewById(R.id.input_message);

        messagesAdapter=new MessagesAdapter(messagesList);
        userMessageList=findViewById(R.id.Pvt_Message_list_of_users);
        linearLayoutManager=new LinearLayoutManager(this);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messagesAdapter);

    }

    private void DisplayLastSeen()
    {
        RootRef.child("Users").child(messageSenderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.child("userState").hasChild("state")){
                            String state=snapshot.child("userState").child("state").getValue().toString();
                            String time=snapshot.child("userState").child("time").getValue().toString();
                            String date=snapshot.child("userState").child("date").getValue().toString();

                            if(state.equals("online"))
                            {
                                userLastSeen.setText( "online");

                            }
                            else   if(state.equals("offline"))
                            {
                                userLastSeen.setText("Last Seen: " + date + " "+ time);

                            }
                        }
                        else {
                            userLastSeen.setText("offline");


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        RootRef.child("Messages").child(messageSenderId).child(messageReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                       Messages messages=snapshot.getValue(Messages.class);
                       messagesList.add(messages);
                       messagesAdapter.notifyDataSetChanged();
                       userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void SendMessage(){
       String messageText=MessageInputText.getText().toString();
       if(TextUtils.isEmpty(messageText))
       {
           Toast.makeText(this, "First Write youre message.....", Toast.LENGTH_SHORT).show();
       }
       else {
           String messageSenderRef="Messages/" + messageSenderId + "/" + messageReceiverId;
           String messageReceiverRef="Messages/" + messageReceiverId +"/" + messageSenderId;

           DatabaseReference userMessageKeyRef=RootRef.child("Messages")
                   .child(messageSenderId).child(messageReceiverId).push();
           String messagePushId=userMessageKeyRef.getKey();
           Map messageTextBody=  new HashMap();
           messageTextBody.put("message",messageText);
           messageTextBody.put("type","text");
           messageTextBody.put("from",messageSenderId);

           Map messageBodyDetails = new HashMap();
           messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageTextBody);
           messageBodyDetails.put(messageReceiverRef + "/" + messagePushId, messageTextBody);

           RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
               @Override
               public void onComplete(@NonNull Task task) {
                   if(task.isSuccessful())
                   {
                       Toast.makeText(Chat.this, "Message Sent Successfully..", Toast.LENGTH_SHORT).show();
                   }
                   else {
                       Toast.makeText(Chat.this, "Error", Toast.LENGTH_SHORT).show();
                   }
                   MessageInputText.setText(" ");
               }
           });

       }
    }
}