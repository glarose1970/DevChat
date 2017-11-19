package com.commandcenter.devchat.Controller;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.commandcenter.devchat.Model.User;
import com.commandcenter.devchat.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class UsersList extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsers;
    private FirebaseUser currentUser;
    private RecyclerView userRecView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mUsers = mDatabase.getReference("users");
        currentUser = mAuth.getCurrentUser();

        userRecView = (RecyclerView) findViewById(R.id.usersList_RecView);
        userRecView.setHasFixedSize(true);
        userRecView.setLayoutManager(new LinearLayoutManager(this));


        FirebaseRecyclerAdapter<User, UsersViewHolder> allUsersAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(
                User.class,
                R.layout.single_user_row_layout,
                UsersViewHolder.class,
                mUsers) {
            @Override
            protected void populateViewHolder(final UsersViewHolder viewHolder, User users, int position) {

                viewHolder.setName(users.getUsername());
                viewHolder.setRank(users.getRank());
                viewHolder.setStatus(users.getStatus());
                viewHolder.setProfileImg(users.getMain_img_url());

               final String user_id = getRef(position).getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(UsersList.this, User_Profile.class);
                        intent.putExtra("user_id", user_id);
                        startActivity(intent);
                    }
                });

            }
        };

        userRecView.setAdapter(allUsersAdapter);


    }

    @Override
    protected void onStart() {
        super.onStart();


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
                    Intent intent = new Intent(UsersList.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navFriends:

                    return true;
                case R.id.navUsers:

                    return true;
                case R.id.settings:
                    intent = new Intent(UsersList.this, Main_Settings_Profile.class);
                    startActivity(intent);
                    return true;
                case R.id.navChatbox:
                    intent = new Intent(UsersList.this, Chatbox_Activity.class);
                    startActivity(intent);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private void signOut() {

        if (currentUser != null) {
            mUsers.child(currentUser.getUid()).child("status").setValue("Offline");
            mAuth.signOut();
            Intent intent = new Intent(UsersList.this, MainActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(UsersList.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        final Context context;
        public UsersViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            mView = itemView;
        }

        public void setName(String name) {
            TextView tv_DisplayName = (TextView) mView.findViewById(R.id.single_user_tv_displayName);
            tv_DisplayName.setText(name);
        }

        public void setStatus(String status) {
            TextView tv_userStatus = (TextView) mView.findViewById(R.id.single_user_tv_Status);
            if (status.equalsIgnoreCase("Online")) {
                tv_userStatus.setTextColor(Color.GREEN);
                tv_userStatus.setText(status);
            }else {
                tv_userStatus.setTextColor(Color.RED);
                tv_userStatus.setText(status);
            }

        }

        public void setRank(String rank) {
            TextView tv_rank = (TextView) mView.findViewById(R.id.single_user_tv_Rank);
            tv_rank.setText(rank);
        }

        public void setProfileImg(String imgUrl) {
            de.hdodenhof.circleimageview.CircleImageView iv_profile = (de.hdodenhof.circleimageview.CircleImageView) mView.findViewById(R.id.single_user_iv_profileImg);
            Picasso.with(context).load(imgUrl).placeholder(R.drawable.ic_person).into(iv_profile);
        }

    }
}
