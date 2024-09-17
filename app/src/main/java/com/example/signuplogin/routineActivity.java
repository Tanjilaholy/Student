package com.example.signuplogin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class routineActivity extends AppCompatActivity {
    private Spinner sectionSpinner;
    private Button uploadRoutineButton, deleteRoutineButton;
    private ImageView selectedImageView;
    private String selectedSection;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

        sectionSpinner = findViewById(R.id.sectionSpinner);
        uploadRoutineButton = findViewById(R.id.uploadRoutineButton);
        deleteRoutineButton = findViewById(R.id.deleteRoutineButton);
        selectedImageView = findViewById(R.id.selectedImageView);

        databaseReference = FirebaseDatabase.getInstance().getReference("classRoutines");
        storageReference = FirebaseStorage.getInstance().getReference("routines");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.selection_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectionSpinner.setAdapter(adapter);

        sectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSection = parent.getItemAtPosition(position).toString();
                loadRoutineImage(selectedSection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedImageView.setImageResource(R.drawable.figma1);
            }
        });

        selectedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        uploadRoutineButton.setOnClickListener(v -> {
            if (selectedSection != null) {
                uploadRoutine(selectedSection);
            }
        });

        deleteRoutineButton.setOnClickListener(v -> {
            if (selectedSection != null) {
                deleteRoutineImage(selectedSection);
            }
        });
    }

    private void loadRoutineImage(String section) {
        databaseReference.child(section).child("routine").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String routineUrl = dataSnapshot.getValue(String.class);
                if (routineUrl != null) {
                    Picasso.get()
                            .load(routineUrl)
                            .placeholder(R.drawable.figma1)
                            .into(selectedImageView);
                } else {
                    selectedImageView.setImageResource(R.drawable.figma1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void uploadRoutine(String section) {
        // This method might not be needed anymore as image upload is handled in onActivityResult
    }

    private void deleteRoutineImage(String section) {
        StorageReference fileRef = storageReference.child(section + ".jpg");
        fileRef.delete().addOnSuccessListener(aVoid -> {
            databaseReference.child(section).child("routine").removeValue();
            selectedImageView.setImageResource(R.drawable.figma1);
        }).addOnFailureListener(e -> {
            // Handle deletion failure
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            if (selectedImageUri != null && selectedSection != null) {
                StorageReference fileRef = storageReference.child(selectedSection + ".jpg");
                fileRef.putFile(selectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        databaseReference.child(selectedSection).child("routine").setValue(imageUrl);
                                        Toast.makeText(routineActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(routineActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            Picasso.get().load(selectedImageUri).into(selectedImageView);
        }
    }
}