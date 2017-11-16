package com.commandcenter.devchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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

import com.commandcenter.devchat.Controller.Chatbox_Activity;
import com.commandcenter.devchat.Controller.MainActivity;
import com.commandcenter.devchat.Controller.UsersList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Main_Settings_Profile extends AppCompatActivity {

    private DatabaseReference mUsers;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    private StorageReference mStorage;
    private static final int GALLERY_INTENT = 2;

    Button btn_Edit;
    TextView tv_displayName, tv_Rank, tv_Friends, tv_status;
    ImageView iv_profile_image;

    ProgressDialog mUploadProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__settings__profile);

        mAuth = FirebaseAuth.getInstance();
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
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        setUserInfo();

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

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            mUploadProgress = new ProgressDialog(this);
            mUploadProgress.setTitle("Uploading Image");
            mUploadProgress.setMessage("Please wait while DevChat Uploads your profile image...");
            mUploadProgress.setCanceledOnTouchOutside(false);
            mUploadProgress.show();
            Uri uri = data.getData();
            iv_profile_image.setImageURI(uri);
            StorageReference profileImagePath = mStorage.child("ProfileImages").child(mAuth.getCurrentUser().getUid()).child("main_profile");

            profileImagePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mUploadProgress.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    private void setUserInfo() {
        mUsers.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("username").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String rank = dataSnapshot.child("rank").getValue().toString();

                tv_displayName.setText(name);
                tv_Rank.setText(rank);
                tv_status.setText(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
