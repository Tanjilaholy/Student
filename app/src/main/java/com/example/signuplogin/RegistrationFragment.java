package com.example.signuplogin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegistrationFragment extends Fragment {
    private Button btnNewRegistration, btnAllRegistration;
    private TableLayout course_table_layout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        btnNewRegistration = view.findViewById(R.id.btn_new_registration);
        btnAllRegistration = view.findViewById(R.id.btn_all_registration);
        course_table_layout = view.findViewById(R.id.course_table_layout); // TableLayout for registered courses
        // Load registered courses from Firebase and display in the table
        fetchRegisteredCoursesFromDatabase();
        // Handle new registration button click
        btnNewRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newRegistrationFragment = new NewregiFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, newRegistrationFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        // Handle all registration button click
        btnAllRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment allRegistrationFragment = new AllregiFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, allRegistrationFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return view;
    }
    private void fetchRegisteredCoursesFromDatabase() {
        // Fetch registered courses from Firebase (or any other source)
        DatabaseReference registeredCoursesRef = FirebaseDatabase.getInstance().getReference("registered_courses").child("user_id"); // Use user's unique id
        registeredCoursesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                course_table_layout.removeAllViews(); // Clear previous rows
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    Map<String, Object> courseData = (Map<String, Object>) courseSnapshot.getValue();
                    // Dynamically add rows to the table for each registered course
                    TableRow tableRow = new TableRow(getContext());
                    // Add Course Code
                    TextView courseCodeTextView = new TextView(getContext());
                    courseCodeTextView.setText(courseData.get("courseCode").toString());
                    tableRow.addView(courseCodeTextView);
                    // Add Course Title
                    TextView courseTitleTextView = new TextView(getContext());
                    courseTitleTextView.setText(courseData.get("courseTitle").toString());
                    tableRow.addView(courseTitleTextView);
                    // Add Credit
                    TextView courseCreditTextView = new TextView(getContext());
                    courseCreditTextView.setText(courseData.get("credit").toString());
                    tableRow.addView(courseCreditTextView);
                     course_table_layout.addView(tableRow);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }
}
