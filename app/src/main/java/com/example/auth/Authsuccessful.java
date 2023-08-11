package com.example.auth;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

public class Authsuccessful extends AppCompatActivity {
    String displayName,uid;
    TextView displayNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_successful);
        // Get the display name from the intent extras
         uid = getIntent().getStringExtra("uid");
         displayNameTextView = findViewById(R.id.display_name_text);

        // Find the TextView in the layout and set the display name
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot,@Nullable String previousChildName) {
                String key = dataSnapshot.getKey();
                assert key != null;
                if (key.equals(uid)) {
                    User newPost = dataSnapshot.getValue(User.class);


                    if (newPost != null) {

                        displayName = newPost.getDisplayname();
                        displayNameTextView.setText("Hello, " + displayName + "!");
                    } else {
                        displayNameTextView.setText("Welcome, No user existed!");

                    }

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
}
