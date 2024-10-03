package com.example.messaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.OnNewIntentProvider;
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

public class ChatDetailsActivity extends AppCompatActivity {

FirebaseDatabase database;
FirebaseAuth auth;

EditText etMessage;
ImageView back,phone,video,menu,send;
RecyclerView Chat_RecylarView;
CircleImageView profile;
TextView name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
        getSupportActionBar().hide();


        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        final String senderId = auth.getUid();
        String  recieveId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");




        etMessage = findViewById(R.id.editTextTextPersonName);
        back = findViewById(R.id.back);
        phone = findViewById(R.id.phone);
        video = findViewById(R.id.video);
        menu = findViewById(R.id.menu);
        send = findViewById(R.id.send);
        Chat_RecylarView = findViewById(R.id.Chat_RecylarView);
        profile = findViewById(R.id.profile_image);
        name = findViewById(R.id.name);




        name.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.profile).into(profile);




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });




        final ArrayList<MessageModel> messageModels = new ArrayList<>();

        final ChatAdapter chatAdapter = new ChatAdapter(messageModels, this, recieveId);

        Chat_RecylarView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        Chat_RecylarView.setLayoutManager(layoutManager);


        final String senderRoom = senderId+recieveId;
        final  String reciverRoom = recieveId+senderId;


        database.getReference().child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();  // Clear list before adding new data
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                          MessageModel model = snapshot1.getValue(MessageModel.class);
                            model.setMessageId(snapshot1.getKey());
                            if (model != null) {
                                messageModels.add(model);  // Only add messages from the sender
                            }
                        }
                        chatAdapter.notifyDataSetChanged();  // Notify the adapter about data changes
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });





        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String message = etMessage.getText().toString();

                final MessageModel model = new MessageModel(senderId, message);

                model.setTimestamp(new Date().getTime());

                etMessage.setText("");


                database.getReference().child("chats").child(senderRoom)
                        .push()
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        database.getReference().child("chats").child(reciverRoom)
                                .push()
                                .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {



                            }
                        });



                    }
                });



            }
        });








    }
}