package com.example.signuplogin;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegistrationFragment extends Fragment {
    private Button btnNewRegistration, btnAllRegistration;
    private TableLayout courseTableLayout;
    private TextView semesterNameTextView, semesterNoTextView, totalCreditTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        btnNewRegistration = view.findViewById(R.id.btn_new_registration);
        btnAllRegistration = view.findViewById(R.id.btn_all_registration);
        courseTableLayout = view.findViewById(R.id.course_table_layout);

        semesterNameTextView = view.findViewById(R.id.S_N);
        semesterNoTextView = view.findViewById(R.id.S_no);
        totalCreditTextView = view.findViewById(R.id.Total_credit);

        fetchRegisteredCoursesFromDatabase();
        fetchSemesterInfo();

        btnNewRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newRegistrationFragment = new NewregiFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, newRegistrationFragment);
                fragmentTransaction.addToBackStack(null); // Allow navigation back
                fragmentTransaction.commit();
            }
        });

        btnAllRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment allRegistrationFragment = new AllregiFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, allRegistrationFragment);
                fragmentTransaction.addToBackStack(null); // Allow navigation back
                fragmentTransaction.commit();
            }
        });

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
                    courseTableLayout.removeAllViews();

                    TableRow headerRow = new TableRow(getContext());

                    // Define layout params for header
                    TableRow.LayoutParams paramsHeaderIndex = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f);
                    TableRow.LayoutParams paramsHeaderCourseCode = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
                    TableRow.LayoutParams paramsHeaderCourseTitle = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2.0f);
                    TableRow.LayoutParams paramsHeaderCredit = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);

                    headerRow.addView(createHeaderTextView("#", 16, Color.parseColor("#004E9E"), paramsHeaderIndex));
                    headerRow.addView(createHeaderTextView("Course Code", 16, Color.parseColor("#004E9E"), paramsHeaderCourseCode));
                    headerRow.addView(createHeaderTextView("Course Title", 18, Color.parseColor("#004E9E"), paramsHeaderCourseTitle));
                    headerRow.addView(createHeaderTextView("Credit", 16, Color.parseColor("#004E9E"), paramsHeaderCredit));
                    courseTableLayout.addView(headerRow);

                    int index = 1;
                    for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                        String courseCode = courseSnapshot.child("courseCode").getValue(String.class);
                        String courseTitle = courseSnapshot.child("courseTitle").getValue(String.class);
                        String credit = courseSnapshot.child("credit").getValue(String.class);

                        TableRow tableRow = new TableRow(getContext());


                        TableRow.LayoutParams paramsIndex = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f);
                        TableRow.LayoutParams paramsCourseCode = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
                        TableRow.LayoutParams paramsCourseTitle = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2.0f);
                        TableRow.LayoutParams paramsCredit = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);

                        tableRow.addView(createTextView(String.valueOf(index++), 16, Color.BLACK, paramsIndex));
                        tableRow.addView(createTextView(courseCode, 16, Color.BLACK, paramsCourseCode));
                        tableRow.addView(createTextView(courseTitle, 18, Color.BLACK, paramsCourseTitle));
                        tableRow.addView(createTextView(credit, 16, Color.BLACK, paramsCredit));
                        courseTableLayout.addView(tableRow);
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

    private void fetchSemesterInfo() {
        DatabaseReference semesterInfoRef = FirebaseDatabase.getInstance().getReference("SemesterInfo");

        semesterInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String semesterName = snapshot.child("semesterName").getValue(String.class);
                String semesterNumber = snapshot.child("semesterNumber").getValue(String.class);
                String totalCredits = snapshot.child("totalCredits").getValue(String.class);

                if (semesterName != null && semesterNumber != null && totalCredits != null) {
                    semesterNameTextView.setText(semesterName);
                    semesterNoTextView.setText(semesterNumber);
                    totalCreditTextView.setText(totalCredits);


                    int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

                    if (semesterName != null) {
                        semesterNameTextView.setTypeface(semesterNameTextView.getTypeface(), Typeface.BOLD);
                        semesterNameTextView.setPadding(padding, padding, padding, padding);
                    }

                    semesterNoTextView.setTypeface(semesterNoTextView.getTypeface(), Typeface.BOLD);
                    totalCreditTextView.setTypeface(totalCreditTextView.getTypeface(), Typeface.BOLD);
                    semesterNoTextView.setPadding(padding, padding, padding, padding);
                    totalCreditTextView.setPadding(padding, padding, padding, padding);
                } else {
                    semesterNameTextView.setText("Not available");
                    semesterNoTextView.setText("Not available");
                    totalCreditTextView.setText("Not available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error fetching semester information!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private TextView createHeaderTextView(String text, int textSize, int textColor, TableRow.LayoutParams layoutParams) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        textView.setTextColor(textColor);
        textView.setPadding(16, 16, 16, 16);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    private TextView createTextView(String text, int textSize, int textColor, TableRow.LayoutParams layoutParams) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTypeface(null, Typeface.NORMAL);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        textView.setTextColor(textColor);
        textView.setPadding(16, 16, 16, 16);
        textView.setLayoutParams(layoutParams);
        return textView;
    }
}