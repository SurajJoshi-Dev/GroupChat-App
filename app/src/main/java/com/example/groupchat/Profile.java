package com.example.groupchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
private String receiverUserId,SenderUserId,Current_State;
CircleImageView userProfileImage;
TextView userProfName,userProfileStatus;
Button sendMessageReqBtn,declineMessageReqBtn;
DatabaseReference UserRef,ChatreqRef,contactsRef,NotificationRef;
FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth=FirebaseAuth.getInstance();
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        ChatreqRef=FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsRef=FirebaseDatabase.getInstance().getReference().child("Contacts");
     //   NotificationRef=FirebaseDatabase.getInstance().getReference().child("Notifications");

        receiverUserId=getIntent().getExtras().get("visit_user_id").toString();
        SenderUserId=firebaseAuth.getCurrentUser().getUid();

      //  Toast.makeText(this, "User ID: " +receiverUserId, Toast.LENGTH_SHORT).show();

        userProfileImage=findViewById(R.id.Visit_profile_Image);
        userProfName=findViewById(R.id.visit_user_username);
        userProfileStatus=findViewById(R.id.visit_profile_starus);
        sendMessageReqBtn=findViewById(R.id.send_message_request_BTn);
        declineMessageReqBtn=findViewById(R.id.decline_message_request_BTn);

        Current_State="new";

        RetrieveUserInfo();

    }

    private void RetrieveUserInfo() {

        UserRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.exists())&&(snapshot.hasChild("image"))){

                 String userImage=snapshot.child("image").getValue().toString();
                    String userName=snapshot.child("name").getValue().toString();
                    String userStatus=snapshot.child("status").getValue().toString();
                    Picasso.get().load(userImage).placeholder(R.drawable.avt).into(userProfileImage);//Display image

               userProfName.setText(userName);
               userProfileStatus.setText(userStatus);

               ManageChatReq();

                }
                else {  //user status and name because image is optional
                    String userName=snapshot.child("name").getValue().toString();
                    String userStatus=snapshot.child("status").getValue().toString();

                    userProfName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    ManageChatReq();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    private void ManageChatReq() {

        ChatreqRef.child(SenderUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(receiverUserId)){
                            String request_type=snapshot.child(receiverUserId)
                                    .child("request_type").getValue().toString();
                            if(request_type.equals("sent"))
                            {
                                Current_State="request_sent";
                                sendMessageReqBtn.setText("Cancel Chat Req");

                            }
                            else if(request_type.equals("received"))
                            {
                                Current_State="request received";
                                sendMessageReqBtn.setText("Accept Chat Request");
                               Log.i("accept","error");
                                declineMessageReqBtn.setVisibility(View.VISIBLE);
                                declineMessageReqBtn.setEnabled(true);

                                declineMessageReqBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        CancelChatReq();
                                    }
                                });
                            }
                        }
                        else
                            {
                            contactsRef.child(SenderUserId)
                                   .addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                                           if(snapshot.hasChild(receiverUserId)){
                                               Current_State="friends";
                                               sendMessageReqBtn.setText("Remove this Contact");
                                           }
                                       }
                                       @Override
                                       public void onCancelled(@NonNull DatabaseError error) {

                                       }
                                   });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
       if(!SenderUserId.equals(receiverUserId))
       {
        sendMessageReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              sendMessageReqBtn.setEnabled(false);
                Log.i("error",Current_State);
              if(Current_State.equals("new"))
              {
                  Log.i("error","new");
                  sendChatReq();

              }
             else if(Current_State.equals("request_sent"))
              {
                  CancelChatReq();
                  Log.i("error","requSent");
              }
              else if(Current_State.equals("request received"))
                {
                    AcceptChatReq();
                    Log.i("error","requestReceived");
                }
              else if (Current_State.equals("friends"))
              {
                RemoveSpecificContact();
              }
            }
        });
       }
       else {
           sendMessageReqBtn.setVisibility(View.INVISIBLE);//our id sendreqbutton invisible
       }
    }


    private void RemoveSpecificContact() {
        contactsRef.child(SenderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            contactsRef.child(receiverUserId).child(SenderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                sendMessageReqBtn.setEnabled(true);
                                                Current_State="new";
                                                sendMessageReqBtn.setText("Send Message");

                                                declineMessageReqBtn.setVisibility(View.INVISIBLE);
                                                declineMessageReqBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    //contacts (acceptChatReq)method
    private void AcceptChatReq() {
Log.i("accept",SenderUserId);
Log.i("accept",receiverUserId);

contactsRef.child(SenderUserId).child(receiverUserId)

     .child("Contacts").setValue("Saved")
     .addOnCompleteListener(new OnCompleteListener<Void>() {
         @Override
         public void onComplete(@NonNull Task<Void> task) {
             Log.d("ValueSaved","saved");
          if(task.isSuccessful())
          {
              contactsRef.child(receiverUserId).child(SenderUserId)
                      .child("Contacts").setValue("Saved")
                      .addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                              if(task.isSuccessful())
                              {
                            ChatreqRef.child(SenderUserId).child(receiverUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                ChatreqRef.child(receiverUserId).child(SenderUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                sendMessageReqBtn.setEnabled(true);
                                                                Current_State = "friends";
                                                                sendMessageReqBtn.setText("Remove this Contact");

                                                                declineMessageReqBtn.setVisibility(View.INVISIBLE);
                                                                declineMessageReqBtn.setEnabled(false);
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

    private void CancelChatReq() {

        ChatreqRef.child(SenderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful())
                      {
                          ChatreqRef.child(receiverUserId).child(SenderUserId)
                                  .removeValue()
                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful())
                         {
                             sendMessageReqBtn.setEnabled(true);
                             Current_State="new";
                             sendMessageReqBtn.setText("Send Message");

                             declineMessageReqBtn.setVisibility(View.INVISIBLE);
                             declineMessageReqBtn.setEnabled(false);
                         }
                                      }
                                  });
                      }
                    }
                });
    }


    private void sendChatReq() {
        ChatreqRef.child(SenderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if ((task.isSuccessful()))
                       {
                           ChatreqRef.child(receiverUserId).child(SenderUserId)
                                   .child("request_type").setValue("received")
                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if(task.isSuccessful()){

//                                               HashMap<String,String>chatNotificationMap=new HashMap<>();
//                                               chatNotificationMap.put("from",SenderUserId);
//                                               chatNotificationMap.put("type", "request");
//
//                                               NotificationRef.child(receiverUserId).push()
//                                                       .setValue(chatNotificationMap)
//                                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                           @Override
//                                                           public void onComplete(@NonNull Task<Void> task) {
//                                                             if(task.isSuccessful()){
//
//
//                                                             }
//                                                           }
//                                                       });
                           sendMessageReqBtn.setEnabled(true);
                           Current_State="request_sent";
                           sendMessageReqBtn.setText("Cancel Chat Request");
                                           }
                                       }
                                   });
                       }
                    }
                });
    }
}