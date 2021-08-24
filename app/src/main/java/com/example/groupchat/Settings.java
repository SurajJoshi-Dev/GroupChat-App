package com.example.groupchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {

TextInputEditText userName,userstatus;
Button updateAccountSettings;
CircleImageView userProfileImage;
String currentUserId;
FirebaseAuth firebaseAuth;
DatabaseReference RootRef;
ImageView backButton;
private static final int GalleryPick=1;
 private StorageReference UserProfileImagesRef;
private ProgressDialog loadinBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
//getSupportActionBar().hide();

firebaseAuth=FirebaseAuth.getInstance();
currentUserId=firebaseAuth.getCurrentUser().getUid();
RootRef=FirebaseDatabase.getInstance().getReference();
UserProfileImagesRef= FirebaseStorage.getInstance().getReference().child("Profile Images");
     Initializefield();

     //   userName.setVisibility(View.INVISIBLE);

        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                UpdateSettings();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Settings.this,MainActivity2.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
               // finish();
            }
        });
//**************show  username and user status in GroupChat app******************
        RetrievUserInfo();

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GalleryPick);


              Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");

                startActivityForResult(galleryIntent,GalleryPick);


             /*   Intent gIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
               // gIntent.setType("image/*");
                startActivityForResult(gIntent , GalleryPick );

              */
            }
        });
    }



    private void Initializefield() {
        updateAccountSettings=findViewById(R.id.Update_settings_btn);
        userName=findViewById(R.id.set_user_name);
        userstatus=findViewById(R.id.set_profile_status);
        userProfileImage=findViewById(R.id.profile_image);
        backButton=findViewById(R.id.BackButton_btn);
        loadinBar= new ProgressDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageuri=null;




if(requestCode==GalleryPick && resultCode==RESULT_OK && data!=null)
{
     imageuri=data.getData();
    CropImage.activity(imageuri)

            .setBackgroundColor(Color.parseColor("#FF000000"))
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1,1)
            .start(this);

}
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);


                if (resultCode == RESULT_OK) {
                    loadinBar.setTitle("set Profile Image");
                    loadinBar.setMessage("Please wait, your profile image is updating...");
                    loadinBar.setCanceledOnTouchOutside(false);
                    loadinBar.show();

                    Uri resultUri = result.getUri();
                    StorageReference filePath = UserProfileImagesRef.child(currentUserId + ".jpg");
                    UploadTask uploadTask = filePath.putFile(resultUri);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            loadinBar.dismiss();
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//            downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                            taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                                    new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            String fileLink = task.getResult().toString();
                                            Log.d("image=>", fileLink);
                                            RootRef.child("Users").child(currentUserId).child("image")
                                                    .setValue(fileLink)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(Settings.this, "Image Save in Database Successfully", Toast.LENGTH_SHORT).show();
                                                                loadinBar.dismiss();
                                                            } else {
                                                                String message = task.getException().toString();
                                                                Toast.makeText(Settings.this, "Error: " + message, Toast.LENGTH_LONG).show();
                                                                loadinBar.dismiss();
                                                            }
                                                        }
                                                    });


                                        }
                                    });

                        }
                    });
                }

        }
    }

    private void UpdateSettings() {
        String setUsername=userName.getText().toString();
        String setStatus=userstatus.getText().toString();

        if(TextUtils.isEmpty(setUsername))
        {
            Toast.makeText(this, "Please write your username first..", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setStatus)){
            Toast.makeText(this, "Please write your status..", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String,Object>profileMap=new HashMap<>();
            profileMap.put("uid",currentUserId);
            profileMap.put("name",setUsername);
            profileMap.put("status",setStatus);
            RootRef.child("Users").child(currentUserId).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                SendUserToMainActivity();

                                Toast.makeText(Settings.this, "profile update Successsfully", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(Settings.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();

                            } }
                    });
        }

    }
    //***************Show user name and status feature ***********
    private void RetrievUserInfo() {
        RootRef.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if ((snapshot.exists())&&(snapshot.hasChild("name"))&&(snapshot.hasChild("image"))) {

                            String retrieveUserName=snapshot.child("name").getValue().toString();
                            String retrieveStatus=snapshot.child("status").getValue().toString();
                            String retrieveProfileImage=snapshot.child("image").getValue().toString();
              userName.setText(retrieveUserName);
                  userstatus.setText(retrieveStatus);
                            Picasso.get().load(retrieveProfileImage).into(userProfileImage);
                        }

                        else if ((snapshot.exists())&&(snapshot.hasChild("name"))){
                            String retrieveUserName=snapshot.child("name").getValue().toString();
                            String retrieveStatus=snapshot.child("status").getValue().toString();
                            userName.setText(retrieveUserName);
                            userstatus.setText(retrieveStatus);
                        }

                        else {
                            userName.setVisibility(View.VISIBLE);

                            Toast.makeText(Settings.this, "Please Set & Update youre profile information....", Toast.LENGTH_LONG).show();
                        }

                    }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void SendUserToMainActivity() {
        Intent intent = new Intent(Settings.this, MainActivity2.class);
 //      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
       finish();
        Toast.makeText(Settings.this, "Logged in Successful....", Toast.LENGTH_SHORT).show();
    }
}