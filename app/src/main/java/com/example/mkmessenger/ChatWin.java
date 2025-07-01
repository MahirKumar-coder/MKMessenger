// ChatWin.java (Modified)
package com.example.mkmessenger;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatWin extends AppCompatActivity {

    public static String senderImg = "";
    public static String receiverIImg = "";

    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_win);

        Log.d("ChatWinLog", "Receiver Name: " + getIntent().getStringExtra("nameeee"));
        Log.d("ChatWinLog", "Receiver Img: " + getIntent().getStringExtra("reciverImg"));
        Log.d("ChatWinLog", "Receiver UID: " + getIntent().getStringExtra("uid"));
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        String receiverName = getIntent().getStringExtra("nameeee");
        if (receiverName == null) receiverName = "Unknown";

        String receiverImg = getIntent().getStringExtra("reciverImg");
        if (receiverImg == null) receiverImg = "";

        String receiverUid = getIntent().getStringExtra("uid");
        if (receiverUid == null || receiverUid.isEmpty()) {
            Toast.makeText(this, "Invalid UID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String senderUid = firebaseAuth.getUid();

        if (senderUid == null || receiverUid == null) {
            Toast.makeText(this, "Invalid sender or receiver UID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String senderRoom = senderUid + receiverUid;
        String reciverRoom = receiverUid + senderUid;

        CircleImageView profile = findViewById(R.id.profilerg0);
        TextView receiverNName = findViewById(R.id.receivername);
        CardView sendbtn = findViewById(R.id.sendbtnn);
        EditText textmsg = findViewById(R.id.textmsg);
        RecyclerView mmessangesAdpter = findViewById(R.id.msgadapter);

        if (receiverImg != null && !receiverImg.isEmpty()) {
            Picasso.get().load(receiverImg).placeholder(R.drawable.photocamera).error(R.drawable.man).into(profile);
        }
        receiverNName.setText(receiverName != null ? receiverName : "");

        ArrayList<msgModdelClass> messageArrayList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        mmessangesAdpter.setLayoutManager(layoutManager);
        messagesAdapter messagesAdapter = new messagesAdapter(this, messageArrayList);
        mmessangesAdpter.setAdapter(messagesAdapter);

        receiverIImg = receiverImg != null ? receiverImg : "";

        database.getReference("users").child(senderUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("profilepic").getValue() != null ? snapshot.child("profilepic").getValue().toString() : "";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        database.getReference("chats").child(senderRoom).child("message")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageArrayList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            msgModdelClass msg = snap.getValue(msgModdelClass.class);
                            if (msg != null) messageArrayList.add(msg);
                        }
                        messagesAdapter.notifyDataSetChanged();
                        mmessangesAdpter.scrollToPosition(messageArrayList.size() - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        sendbtn.setOnClickListener(v -> {
            String message = textmsg.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(ChatWin.this, "Enter a message first", Toast.LENGTH_SHORT).show();
                return;
            }

            textmsg.setText("");
            msgModdelClass msg = new msgModdelClass(message, senderUid, new Date().getTime());

            database.getReference("chats").child(senderRoom).child("message").push().setValue(msg)
                    .addOnCompleteListener(task -> database.getReference("chats").child(reciverRoom).child("message").push().setValue(msg));
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
