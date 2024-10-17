package com.example.signuplogin;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RoutineFragment extends Fragment {
    private Spinner sectionSpinner;
    private ImageView selectedImageView;
    private FirebaseStorage firebaseStorage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routine, container, false);

        sectionSpinner = view.findViewById(R.id.sectionSpinner);
        selectedImageView = view.findViewById(R.id.selectedImageView);

        firebaseStorage = FirebaseStorage.getInstance();
        setupSectionSpinner();

        return view;
    }

    private void setupSectionSpinner() {
        String[] sections = {"Section A", "Section B", "Section C", "Section D", "Section E"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, sections);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectionSpinner.setAdapter(adapter);


        sectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSection = parent.getItemAtPosition(position).toString();
                loadRoutineImage(selectedSection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadRoutineImage(String section) {
        StorageReference storageRef = firebaseStorage.getReference().child("routines/" + section + ".jpg");
        Picasso.get().setLoggingEnabled(true);

        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Picasso.get()
                        .load(uri)
                        .placeholder(R.drawable.placeholder) // Optional placeholder
                        .error(R.drawable.figma2)
                        .into(selectedImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed to load routine image for " + section, Toast.LENGTH_SHORT).show();
                selectedImageView.setImageResource(R.drawable.figma2); // Show error image if failed
            }
        });
    }
}
