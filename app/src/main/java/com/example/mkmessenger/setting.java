package com.example.mkmessenger;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;

import com.squareup.picasso.Picasso;

public class setting extends AppCompatActivity {

    private ImageView setprofile;
    private EditText setname, setstatus;
    private Button doneout;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private Uri setImageUri;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        // Init Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // Init Views
        setprofile = findViewById(R.id.settingprofile);
        setname = findViewById(R.id.settingname);
        setstatus = findViewById(R.id.settingstatus);
        doneout = findViewById(R.id.donebut);

        // Firebase Refs
        String uid = auth.getUid();
        reference = database.getReference().child("user").child(uid);
        storageReference = storage.getReference().child("upload").child(uid);

        // Load existing data
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.child("mail").getValue(String.class);
                password = snapshot.child("password").getValue(String.class);
                String name = snapshot.child("userName").getValue(String.class);
                String profile = snapshot.child("profilepic").getValue(String.class);
                String status = snapshot.child("status").getValue(String.class);

                setname.setText(name);
                setstatus.setText(status);
                if (profile != null && !profile.isEmpty()) {
                    Picasso.get().load(profile).into(setprofile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Select profile image
        setprofile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
        });

        // Save data
        doneout.setOnClickListener(v -> {
            String name = setname.getText().toString().trim();
            String status = setstatus.getText().toString().trim();

            if (name.isEmpty() || status.isEmpty()) {
                Toast.makeText(setting.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (setImageUri != null) {
                storageReference.putFile(setImageUri).addOnSuccessListener(taskSnapshot ->
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            saveUserData(name, status, imageUrl);
                        }));
            } else {
                // Use old image or blank if none
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    saveUserData(name, status, imageUrl);
                }).addOnFailureListener(e -> {
                    saveUserData(name, status, ""); // fallback
                });
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK && data != null) {
            setImageUri = data.getData();
            setprofile.setImageURI(setImageUri);
        }
    }

    private void saveUserData(String name, String status, String imageUrl) {
        Users users = new Users(auth.getUid(), name, email, password, imageUrl, status);
        reference.setValue(users).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(setting.this, "Data saved", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(setting.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(setting.this, "Error saving data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
