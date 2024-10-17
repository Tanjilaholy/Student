package com.example.signuplogin;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AllregiFragment extends Fragment {

    private TableLayout allRegiTableLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allregi, container, false);

        allRegiTableLayout = view.findViewById(R.id.all_regi);

        fetchRegisteredCoursesFromDatabase();

        return view;
    }

    private void fetchRegisteredCoursesFromDatabase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference registeredCoursesRef = FirebaseDatabase.getInstance().getReference("UserRegistrations").child(userId).child("selectedCourses");

            registeredCoursesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    allRegiTableLayout.removeAllViews();

                    // Create the header row
                    TableRow headerRow = new TableRow(getContext());
                    headerRow.addView(createHeaderTextView("#", 0.5f));
                    headerRow.addView(createHeaderTextView("Course Code", 1f));
                    headerRow.addView(createHeaderTextView("Course Title", 2f));
                    headerRow.addView(createHeaderTextView("Credit", 1f));
                    allRegiTableLayout.addView(headerRow);

                    int index = 1;
                    for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                        String courseCode = courseSnapshot.child("courseCode").getValue(String.class);
                        String courseTitle = courseSnapshot.child("courseTitle").getValue(String.class);
                        String credit = courseSnapshot.child("credit").getValue(String.class);

                        TableRow tableRow = new TableRow(getContext());
                        tableRow.addView(createTextView(String.valueOf(index++), 0.5f));
                        tableRow.addView(createTextView(courseCode, 1f));
                        tableRow.addView(createTextView(courseTitle, 2f));
                        tableRow.addView(createTextView(credit, 1f));
                        allRegiTableLayout.addView(tableRow);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error loading courses!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
        }
    }

    private TextView createHeaderTextView(String text, float weight) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextSize(18);
        textView.setPadding(16, 16, 16, 16);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight));
        return textView;
    }

    private TextView createTextView(String text, float weight) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextSize(16);
        textView.setTypeface(null, Typeface.NORMAL);
        textView.setPadding(16, 16, 16, 16);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight));
        return textView;
    }
}