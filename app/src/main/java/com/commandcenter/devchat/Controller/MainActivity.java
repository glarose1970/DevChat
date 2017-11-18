package com.commandcenter.devchat.Controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.commandcenter.devchat.Model.ChatboxMessage;
import com.commandcenter.devchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    EditText et_email, et_password;
    Button btn_login, btn_register;

    //Firebase Auth
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsers;
    private DatabaseReference mMessages;
    private FirebaseUser currentUser;
    private DatabaseReference mNewMessageRef;

    String[] values;

    private String user;
    private String curUser;
    private String rank;
    private String time;
    private String date;

    private String status;
    private String curDate;

    //Progress Dialog
     private ProgressDialog mLoginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        curDate = setDate();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mUsers = mDatabase.getReference("users");
        mMessages = mDatabase.getReference("messages");
        mNewMessageRef = mDatabase.getReference("messages").child(curDate);



        mLoginProgress = new ProgressDialog(this);
       // mAuth.signOut();
        et_email    = (EditText) findViewById(R.id.login_et_email);
        et_password = (EditText) findViewById(R.id.login_et_password);

        btn_login    = (Button) findViewById(R.id.login_btnLogin);
        btn_register = (Button) findViewById(R.id.login_btnRegister);

        if (currentUser == null) {
            currentUser = mAuth.getCurrentUser();
        }else {
            getUser();
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    mLoginProgress.setTitle("Logging in user");
                    mLoginProgress.setMessage("Please wait while DevChat Logs you in!");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    loginUser(et_email.getText().toString(), et_password.getText().toString());
                }else {

                    Intent chatBoxIntent = new Intent(MainActivity.this, Chatbox_Activity.class);
                    startActivity(chatBoxIntent);
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(MainActivity.this, ActivityRegister.class);
                startActivity(registerIntent);
            }
        });

    }

    private void welcomeUser(String username) {

        SimpleDateFormat dFormat = new SimpleDateFormat("hh:mm:ss a");
        String curTime = dFormat.format(new Date()).toString();

        ChatboxMessage message = new ChatboxMessage("Piggy Bot", "Welcome to DevChat : " + curUser + " is now ONLINE!", "Moderator", curDate, curTime, "default_url");
        mNewMessageRef.push().setValue(message);
    }


    private void loginUser(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All Fields Required", Toast.LENGTH_SHORT).show();
            return;
        }else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //sign in successful
                                mLoginProgress.dismiss();
                                currentUser = mAuth.getCurrentUser();
                                mUsers.child(currentUser.getUid()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        curUser = dataSnapshot.getValue().toString();
                                        welcomeUser(curUser);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                setStatus(user,"Online");
                                Intent chatBoxIntent = new Intent(MainActivity.this, Chatbox_Activity.class);
                                chatBoxIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(chatBoxIntent);
                                finish();
                            }else {
                                //sign in failure
                                mLoginProgress.dismiss();
                                Toast.makeText(MainActivity.this, "Email not found, Please Register a new Account!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    private void getUser() {
        mUsers.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.child("username").getValue().toString();
                rank = dataSnapshot.child("rank").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setStatus(String user, String status) {

        switch(status) {
            case "Online":
                mUsers.child(mAuth.getCurrentUser().getUid()).child("status").setValue("Online");
                break;
            case "Offline":
                mUsers.child(mAuth.getCurrentUser().getUid()).child("status").setValue("Offline");
                break;
            case "Banned":
                mUsers.child(mAuth.getCurrentUser().getUid()).child("status").setValue("Banned");
                break;
            case "Silenced":
                mUsers.child(mAuth.getCurrentUser().getUid()).child("status").setValue("Silence");
                break;
            case "Kick":
                mUsers.child(mAuth.getCurrentUser().getUid()).child("status").setValue("Kick");
                break;
        }
    }

    private String getChatStatus(String user) {

        Query query = mUsers
                .orderByChild("username")
                .equalTo(user);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot users : dataSnapshot.getChildren()) {
                    status =  users.child("status").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return status;
    }

    private void Init() {

    }

    private String setDate() {

        String thisDate = "";
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Florida"));
        DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("America/Florida"));
        thisDate = formatter.format(cal.getTime());
        return thisDate;

    }

}
