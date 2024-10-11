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


                    TableRow headerRow = new TableRow(getContext());
                    headerRow.addView(createHeaderTextView("#"));
                    headerRow.addView(createHeaderTextView("Course Code"));
                    headerRow.addView(createHeaderTextView("Course Title"));
                    headerRow.addView(createHeaderTextView("Credit"));
                    allRegiTableLayout.addView(headerRow);

                    int index = 1;
                    for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                        String courseCode = courseSnapshot.child("courseCode").getValue(String.class);
                        String courseTitle = courseSnapshot.child("courseTitle").getValue(String.class);
                        String credit = courseSnapshot.child("credit").getValue(String.class);

                        TableRow tableRow = new TableRow(getContext());
                        tableRow.addView(createTextView(String.valueOf(index++)));
                        tableRow.addView(createTextView(courseCode));
                        tableRow.addView(createTextView(courseTitle));
                        tableRow.addView(createTextView(credit));
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

    private TextView createHeaderTextView(String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(16, 16, 16, 16);
        return textView;
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);
        return textView;
    }
}