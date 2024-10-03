package com.example.messaging.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.messaging.Adapters.UsersAdapter;
import com.example.messaging.Models.Users;
import com.example.messaging.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {



    public ChatsFragment() {
        // Required empty public constructor
    }

    ArrayList<Users> list = new ArrayList<>();
    FirebaseDatabase database;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        UsersAdapter adapter = new UsersAdapter(list, getContext());


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.chatRecylarView);
        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    if (users != null) {
                        users.setUserId(dataSnapshot.getKey()); // Set the userId manually if necessary
                        if (!users.getUserId().equals(FirebaseAuth.getInstance().getUid())){
                            list.add(users);
                            Log.d("ChatsFragment", "User added: " + users.getUserName() + ", " + users.getProfilepic());
                        }
                    } else {
                        Log.d("ChatsFragment", "User object is null");
                    }
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }
}