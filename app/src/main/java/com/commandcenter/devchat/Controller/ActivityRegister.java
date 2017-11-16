package com.commandcenter.devchat.Controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.commandcenter.devchat.Model.User;
import com.commandcenter.devchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityRegister extends AppCompatActivity {

    EditText et_email, et_password, et_username, et_gender;
    Button btn_register, btn_cancel;

    //Progress Dialog
    private ProgressDialog msignInDialog;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    String[] values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        values = new String[3];
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("users");

        msignInDialog = new ProgressDialog(this);

        et_email     = (EditText) findViewById(R.id.register_et_email);
        et_password  = (EditText) findViewById(R.id.register_et_password);
        et_username  = (EditText) findViewById(R.id.register_et_username);
        et_gender    = (EditText) findViewById(R.id.register_et_gender);
        btn_cancel   = (Button) findViewById(R.id.register_btnCancel);
        btn_register = (Button) findViewById(R.id.register_btnRegister);


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp(et_email.getText().toString(), et_password.getText().toString());
            }
        });
    }

    private void SignUp(String email, String password) {
        //create a new user account
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ) {

        }else {
            msignInDialog.setTitle("Creating New User");
            msignInDialog.setMessage("Please wait while DevChat creates your new Account...");
            msignInDialog.setCanceledOnTouchOutside(false);
            msignInDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ActivityRegister.this, "User Account Created!", Toast.LENGTH_SHORT).show();
                                createUser(et_username.getText().toString());
                                msignInDialog.dismiss();
                                Intent chatBoxIntent = new Intent(ActivityRegister.this, Chatbox_Activity.class);
                                Bundle bundle = new Bundle();
                                bundle.putStringArray("values", values);
                                chatBoxIntent.putExtras(bundle);
                                startActivity(chatBoxIntent);
                            } else {
                                msignInDialog.dismiss();
                                Toast.makeText(ActivityRegister.this, "Error Creating New User Account!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void createUser(String username) {
        //save the username to use in the chatbox activity
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Username Field Required!", Toast.LENGTH_SHORT).show();
            return;
        }else {
            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
            String userID = current_user.getUid();
            User user = new User(et_username.getText().toString(), et_gender.getText().toString(), "Newbie", "Online", "Active", userID, "", "");
            mRef.child(mAuth.getCurrentUser().getUid()).setValue(user);
        }
    }
}
