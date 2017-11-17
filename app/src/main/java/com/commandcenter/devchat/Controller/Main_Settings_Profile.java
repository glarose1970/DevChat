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

import com.commandcenter.devchat.R;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

public class Main_Settings_Profile extends AppCompatActivity {

    private DatabaseReference mUsers;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser curUser;

    private StorageReference mStorage;
    private static final int GALLERY_PICK = 1;

    Button btn_Edit;
    TextView tv_displayName, tv_Rank, tv_Friends, tv_status;
    ImageView iv_profile_image;

    ProgressDialog mUploadProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__settings__profile);

        mDatabase = FirebaseDatabase.getInstance();
        mUsers = mDatabase.getReference("users");
        mStorage = FirebaseStorage.getInstance().getReference();

        tv_displayName = (TextView) findViewById(R.id.main_user_profile_tv_displayName);
        tv_Rank        = (TextView) findViewById(R.id.main_user_profile_tv_rank);
        tv_status      = (TextView) findViewById(R.id.main_user_profile_tv_status);
        tv_Friends     = (TextView) findViewById(R.id.main_user_profile_tv_friends);
        iv_profile_image = (ImageView) findViewById(R.id.main_iv_user_profile);
        btn_Edit       = (Button) findViewById(R.id.main_user_profile_btn_ChangeImage);

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

    }

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

                               if (curUser == null) {
                                   mAuth = FirebaseAuth.getInstance();
                                   curUser = mAuth.getCurrentUser();
                                   mUsers.child(curUser.getUid()).child("main_img_url").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if (task.isSuccessful()) {
                                               mUploadProgress.dismiss();
                                               Toast.makeText(Main_Settings_Profile.this, "Image Upload Success!!", Toast.LENGTH_SHORT).show();
                                           }

                                       }
                                   });

                               }else {
                                   mUsers.child(curUser.getUid()).child("main_img_url").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
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

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
            curUser = mAuth.getCurrentUser();
            setUserInfo();
        }else {
            curUser = mAuth.getCurrentUser();
            setUserInfo();
        }

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
