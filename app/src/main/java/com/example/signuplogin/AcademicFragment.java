package com.example.signuplogin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class AcademicFragment extends Fragment {
    ImageView calenderImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_academic, container, false); // Replace with your layout file

        calenderImageView = view.findViewById(R.id.calenderimageview); // Replace with your ImageView ID

        DatabaseReference calenderRef = FirebaseDatabase.getInstance().getReference().child("calender");

        calenderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot calenderSnapshot : snapshot.getChildren()) {
                        // Assuming your calender data has a field named "image" for the image URL
                        String imageUrl = calenderSnapshot.child("image").getValue(String.class);

                        if (imageUrl != null) {
                            Picasso.get().load(imageUrl).into(calenderImageView);
                            // Image loaded successfully, you can break the loop if you only need the first image
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });

        return view;
    }
}