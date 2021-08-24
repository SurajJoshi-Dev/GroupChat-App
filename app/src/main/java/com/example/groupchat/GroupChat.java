package com.example.groupchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class GroupChat extends AppCompatActivity {

    Toolbar mtoolbar;
ImageView SendMessagebtn;
EditText userMessageInput;
TextView displayTextMessage;
ScrollView mscrollView;
    String currentGroupName,currentUserId,currentUserName,currentDate,currentTime;
    FirebaseAuth firebaseAuth;
    DatabaseReference UserRef,GroupNameRef,GroupMessageKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);


  //   getSupportActionBar().hide();
        currentGroupName=getIntent().getExtras().get("groupName").toString();

      Toast.makeText(GroupChat.this, currentGroupName, Toast.LENGTH_SHORT).show();

      firebaseAuth=FirebaseAuth.getInstance();
      currentUserId=firebaseAuth.getCurrentUser().getUid();
      UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef=FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        mtoolbar=findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(currentGroupName);

        InitializeFields();
        GetUserInfo();

        SendMessagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveMessageInfoDatabase();
                userMessageInput.setText("");
                mscrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


 */
    @Override
    protected void onStart() {

        super.onStart();
        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    DisplayMessages(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    DisplayMessages(snapshot);
                }
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



    private void InitializeFields() {
        // *********for Group name show in app bar****

        SendMessagebtn=findViewById(R.id.send_message_btn);
        userMessageInput=findViewById(R.id.input_group_message);
        displayTextMessage=findViewById(R.id.group_chat_text_display);
        mscrollView=findViewById(R.id.my_scroll_view);


    }
    private void GetUserInfo() {

        UserRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             if(snapshot.exists()){
                 currentUserName=snapshot.child("name").getValue().toString();
             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void SaveMessageInfoDatabase() {
        String message=userMessageInput.getText().toString();
        String messageKey=GroupNameRef.push().getKey();
        if (TextUtils.isEmpty(message)) {

            Toast.makeText(this, "Please Write Messagr First..", Toast.LENGTH_SHORT).show();
        }
        else {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDataFormat=new SimpleDateFormat("MMM dd,yyyy");
            currentDate=currentDataFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm a");
            currentTime=currentTimeFormat.format(calForTime.getTime());


            HashMap<String,Object> groupMessageKey=new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);

            GroupMessageKeyRef=GroupNameRef.child(messageKey);
            HashMap<String,Object>messageInfoMap=new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);

            GroupMessageKeyRef.updateChildren(messageInfoMap);

        }
    }
    private void DisplayMessages(DataSnapshot snapshot) {
        Iterator iterator=snapshot.getChildren().iterator();
        while (iterator.hasNext()){
            String chatDate=(String)((DataSnapshot)iterator.next()).getValue();
            String chatMessage=(String)((DataSnapshot)iterator.next()).getValue();
            String chatName=(String)((DataSnapshot)iterator.next()).getValue();
            String chatTime=(String)((DataSnapshot)iterator.next()).getValue();

            displayTextMessage.append(chatName + ":\n"+ chatMessage +"\n"+chatTime+ " "+chatDate +"\n\n\n");

            mscrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }
}