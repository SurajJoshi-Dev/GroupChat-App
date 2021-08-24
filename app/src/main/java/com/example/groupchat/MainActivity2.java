package com.example.groupchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.common.base.Verify;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity2 extends AppCompatActivity {

private Toolbar mtoolbar;
private ViewPager myViewPager;
private TabLayout myTabLayout;
private TabsAccessorAdapter myTabsAccessorAdapter;
private FirebaseAuth firebaseAuth;
DatabaseReference RootRef;
String currentUserId;

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(" Really Exit")
                .setCancelable(false)
                .setMessage("Do you really want to Exit the App?")
                .setPositiveButton("Yes Exit", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                    }
                })
                .setNegativeButton("No Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //jwenfejrn
                    }
                });
        builder.show();

    }



@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

      mtoolbar=findViewById(R.id.main_page_toolbar);
       setSupportActionBar(mtoolbar);

     //  getSupportActionBar().hide();
        getSupportActionBar().setTitle("GroupChat");

        firebaseAuth=FirebaseAuth.getInstance();
        RootRef= FirebaseDatabase.getInstance().getReference();

        myViewPager=findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter=new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);

        myTabLayout=findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

    }

  @Override
    protected void onStart() {
        super.onStart();
      FirebaseUser currentUser=firebaseAuth.getCurrentUser();

      if(currentUser==null){
            SendUserToLoginActivity();
        }
        else {
            updateUserStatus("online");
            VerifyUserexistence();
        }

    }

    @Override
    protected void onStop() {

        super.onStop();
        FirebaseUser currentUser=firebaseAuth.getCurrentUser();

        if(currentUser!=null){
            updateUserStatus("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser currentUser=firebaseAuth.getCurrentUser();

        if(currentUser!=null){
            updateUserStatus("offline");
        }
    }


    private void VerifyUserexistence() {
        String currentUserId=firebaseAuth.getCurrentUser().getUid();
        RootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("name").exists()){
                  //  Toast.makeText(MainActivity2.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else {
                    SendUserToSettingsActivity();

                  //  Toast.makeText(MainActivity2.this, "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu,menu);
return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

    switch (item.getItemId()){
    case  R.id.menu_Logout:
        updateUserStatus("offline");
        firebaseAuth.signOut();
       SendUserToLoginActivity();
     break;

    case R.id.menu_Find_Friends:
        SendUserToFindFriendsActivity();
        break;

    case R.id.Settings:
        SendUserToSettingsActivity();
        break;

        case R.id.main_create_group_option:
            RequestNewGroup();
          break;
    }
    return true;
}

    public void RequestNewGroup() {

        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity2.this,R.style.AlertDialog);
   builder.setTitle("Enter Group Name:");
final EditText groupNameField=new EditText(MainActivity2.this);

groupNameField.setHint("e.g Joshi Brothers");
builder.setView(groupNameField);
 builder .setCancelable(false);
//*********When user click on create button  than what will happend ************

builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        String groupName=groupNameField.getText().toString();

        if (TextUtils.isEmpty(groupName)) {
            Toast.makeText(MainActivity2.this, "Please  Write Group Name...", Toast.LENGTH_SHORT).show();

        }
        else{
            CreateNewGroup(groupName);
         //   finish();
        }
    }
});

builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
       dialogInterface.cancel();

    }
});

          builder.show();
    }


    private void CreateNewGroup(String groupName) {
        RootRef.child("Groups").child(groupName).setValue(" ")
                .addOnCompleteListener(new OnCompleteListener<Void> () {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity2.this,groupName+"Group is Created Successfully....", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SendUserToLoginActivity() {
        Intent intent=new Intent(MainActivity2.this,Login.class);
     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }
    private void SendUserToSettingsActivity() {
        Intent intent=new Intent(MainActivity2.this,Settings.class);
              startActivity(intent);
    }
    private void SendUserToFindFriendsActivity() {
        Intent intent=new Intent(MainActivity2.this,FindFriends.class);

        startActivity(intent);
    }
    private void updateUserStatus(String state){
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap=new HashMap<>();
        onlineStateMap.put("time",saveCurrentTime);
        onlineStateMap.put("date",saveCurrentDate);
        onlineStateMap.put("state", state);
        currentUserId=firebaseAuth.getCurrentUser().getUid();

RootRef.child("Users").child(currentUserId).child("userState")
        .updateChildren(onlineStateMap);
    }
}