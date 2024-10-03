package com.example.messaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.messaging.Adapters.ChatAdapter;
import com.example.messaging.Models.MessageModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity {


    EditText etMessage;
    ImageView back, phone, video, menu, send;
    RecyclerView Chat_RecylarView;
    CircleImageView profile;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        getSupportActionBar().hide();


        etMessage = findViewById(R.id.editTextTextPersonName_group);
        back = findViewById(R.id.back_group);
        phone = findViewById(R.id.phone_group);
        video = findViewById(R.id.video_group);
        menu = findViewById(R.id.menu_group);
        send = findViewById(R.id.send_group);
        Chat_RecylarView = findViewById(R.id.Chat_RecylarView_group);
        profile = findViewById(R.id.profile_image_group);
        name = findViewById(R.id.name_group);


        Picasso.get().load(R.drawable.profile).into(profile);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final ArrayList<MessageModel> messageModels = new ArrayList<>();

        final String senderId = FirebaseAuth.getInstance().getUid();

        name.setText("Friends Group");


        final ChatAdapter adapter = new ChatAdapter(messageModels, this);

        Chat_RecylarView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        Chat_RecylarView.setLayoutManager(layoutManager);


        database.getReference().child("Group Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                messageModels.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    MessageModel model = snapshot.getValue(MessageModel.class);
                    messageModels.add(model);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String message = etMessage.getText().toString();


                final MessageModel model = new MessageModel(senderId, message);
                model.setTimestamp(new Date().getTime());
                etMessage.setText("");

                database.getReference().child("Group Chat").push()
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });





            }
        });


    }


}