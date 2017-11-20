package com.commandcenter.devchat.Controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.commandcenter.devchat.Helper.FriendRequestHelper;
import com.commandcenter.devchat.R;
import com.commandcenter.devchat.Utils.DevChat_Alert_Builder;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.io.ByteArrayOutputStream;

public class Main_Settings_Profile extends AppCompatActivity {

    //==========Firebase==========//
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsers;
    private FirebaseUser mCurUser;
    private StorageReference mStorage;

    private static final int GALLERY_PICK = 1;
    int requestCount;

    //==========Create user Controls==========//
    Button btn_Edit, btn_setName, btn_Accept_Request;
    TextView tv_displayName, tv_Rank, tv_Friends, tv_status;
    ImageView iv_profile_image;
    ProgressDialog mUploadProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__settings__profile);

        init();
        //========== Get Request Count ==========//
        mUsers.child(mCurUser.getUid()).child("requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot request : children) {
                    requestCount++;
                }
                tv_Friends.setText("Requests : " + requestCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //==========Edit the User Profile Image==========//
        btn_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                   Intent galleryIntent = new Intent();
                   galleryIntent.setType("image/*");
                   galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                   startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
                }catch (Exception e) {
                    Toast.makeText(Main_Settings_Profile.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        //==========Change The User Display Name==========//
        btn_setName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevChat_Alert_Builder alert = new DevChat_Alert_Builder("EDIT DISPLAY NAME",Main_Settings_Profile.this);
                alert.showAlert("EDIT DISPLAY NAME");
                tv_displayName.setText(alert.getMessage());
            }
        });


        //==========ACCEPT FRIEND REQUEST==========//
        btn_Accept_Request.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               //TODO create activity to list all user friend requests
            }
        });
        //==========END ACCEPT FRIEND REQUEST==========//

    }

    //==========INITIALIZE FIREBASE/CONTROLS==========//
    private void init() {

        //==========Set the Firebase Auth==========//
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
            mCurUser = mAuth.getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance();
            mUsers = mDatabase.getReference("users");
            mStorage = FirebaseStorage.getInstance().getReference();
            setUserInfo();
        }else {
            mCurUser = mAuth.getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance();
            mUsers = mDatabase.getReference("users");
            mStorage = FirebaseStorage.getInstance().getReference();
            setUserInfo();
        }

        //==========Initialize the View's Controls==========//
        tv_displayName = (TextView) findViewById(R.id.main_user_profile_tv_displayName);
        tv_Rank        = (TextView) findViewById(R.id.main_user_profile_tv_rank);
        tv_status      = (TextView) findViewById(R.id.main_user_profile_tv_status);
        tv_Friends     = (TextView) findViewById(R.id.main_user_profile_tv_friends);
        iv_profile_image = (ImageView) findViewById(R.id.main_iv_user_profile);
        btn_setName    =  (Button) findViewById(R.id.main_user_profile_btn_Edit);
        btn_Edit       = (Button) findViewById(R.id.main_user_profile_btn_ChangeImage);
        btn_Accept_Request = (Button) findViewById(R.id.main_user_profile_btn_Accept_Requests);
    }
    //==========END INITIALIZE FIREBASE/CONTROLS==========//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (mAuth.getCurrentUser() != null) {

            switch (item.getItemId()) {
                case R.id.logout:
                    Toast.makeText(this, "User signed out!", Toast.LENGTH_SHORT).show();
                    signOut();
                    return true;
                case R.id.navFriends:

                    return true;
                case R.id.navUsers:
                    Intent userIntent = new Intent(Main_Settings_Profile.this, UsersList.class);
                    startActivity(userIntent);
                    return true;
                case R.id.settings:
                    userIntent = new Intent(Main_Settings_Profile.this, Main_Settings_Profile.class);
                    startActivity(userIntent);
                    return true;
                case R.id.navChatbox:
                    userIntent = new Intent(Main_Settings_Profile.this, Chatbox_Activity.class);
                    startActivity(userIntent);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        mUsers.child(mAuth.getCurrentUser().getUid()).child("status").setValue("Offline");
        mAuth.signOut();
        Intent intent = new Intent(Main_Settings_Profile.this, MainActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int newHeight = 256;
        int newWidth = 256;

           if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

               Uri uri = data.getData();

               try {
                   Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                   bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
                   ByteArrayOutputStream baos = new ByteArrayOutputStream();
                   bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                   byte[] imgData = baos.toByteArray();

                   mUploadProgress = new ProgressDialog(this);
                   mUploadProgress.setTitle("Uploading Image");
                   mUploadProgress.setMessage("Please wait while DevChat Uploads your profile image...");
                   mUploadProgress.setCanceledOnTouchOutside(false);
                   mUploadProgress.show();

                   iv_profile_image.setImageURI(uri);

                   StorageReference imgPath = mStorage.child("ProfileImages").child(mAuth.getCurrentUser().getUid()).child("profile_img.jpg");

                   imgPath.putBytes(imgData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                           if (task.isSuccessful()) {

                               String downloadUrl = task.getResult().getDownloadUrl().toString();

                               if (mCurUser == null) {
                                   mAuth = FirebaseAuth.getInstance();
                                   mCurUser = mAuth.getCurrentUser();
                                   mUsers.child(mCurUser.getUid()).child("main_img_url").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if (task.isSuccessful()) {
                                               mUploadProgress.dismiss();
                                               Toast.makeText(Main_Settings_Profile.this, "Image Upload Success\r\nChanges will take place on next restart!!", Toast.LENGTH_SHORT).show();
                                           }

                                       }
                                   });

                               }else {
                                   mUsers.child(mCurUser.getUid()).child("main_img_url").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if (task.isSuccessful()) {
                                               mUploadProgress.dismiss();
                                               Toast.makeText(Main_Settings_Profile.this, "Image Upload Success!!", Toast.LENGTH_SHORT).show();
                                           }

                                       }
                                   });

                               }

                           }else {
                               mUploadProgress.dismiss();
                               Toast.makeText(Main_Settings_Profile.this, "Image Upload Failed!!", Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
               }catch (Exception e) {
                   Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
               }


           }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void setUserInfo() {
        mUsers.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("username").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String rank = dataSnapshot.child("rank").getValue().toString();
                String mainImg = dataSnapshot.child("main_img_url").getValue().toString();

                if (mainImg != "default_url") {
                    Picasso.with(Main_Settings_Profile.this).load(mainImg).placeholder(R.drawable.ic_person).into(iv_profile_image);
                }

                tv_displayName.setText(name);
                tv_Rank.setText(rank);
                tv_status.setText(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Uri img_uri = mStorage.child(mAuth.getCurrentUser().getUid()).child("main_profile").getDownloadUrl().getResult();
       // iv_profile_image.setImageURI(img_uri);
    }
}
