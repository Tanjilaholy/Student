package com.example.signuplogin;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewregiFragment extends Fragment {

    private TableLayout courseTableLayout;
    private Button btnSubmitRegistration;
    private List<Course> selectedCourses;  // Store selected courses

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_newregi, container, false);
        courseTableLayout = view.findViewById(R.id.course_table_layout);
        btnSubmitRegistration = view.findViewById(R.id.btn_submit_registration);
        selectedCourses = new ArrayList<>();

        // Fetch courses from the database and populate the table
        fetchCoursesFromDatabase();

        // Handle the submit button click event
        btnSubmitRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSelectedCourses();
            }
        });

        return view;
    }

    private void fetchCoursesFromDatabase() {
        // Fetch course data from Firebase (or any other source)
        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference("courses");
        courseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courseTableLayout.removeAllViews(); // Clear previous rows
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    Course course = courseSnapshot.getValue(Course.class);

                    // Dynamically add rows to the table for each course
                    TableRow tableRow = new TableRow(getContext());

                    // Create Checkbox for course selection
                    CheckBox courseCheckBox = new CheckBox(getContext());
                    courseCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                selectedCourses.add(course);  // Add course to selected list
                            } else {
                                selectedCourses.remove(course); // Remove course from selected list
                            }
                        }
                    });
                    tableRow.addView(courseCheckBox);

                    // Add Course Code
                    TextView courseCodeTextView = new TextView(getContext());
                    courseCodeTextView.setText(course.getCourseCode());
                    tableRow.addView(courseCodeTextView);

                    // Add Course Title
                    TextView courseTitleTextView = new TextView(getContext());
                    courseTitleTextView.setText(course.getCourseTitle());
                    tableRow.addView(courseTitleTextView);

                    // Add Credit
                    TextView courseCreditTextView = new TextView(getContext());
                    courseCreditTextView.setText(String.valueOf(course.getCredit()));
                    tableRow.addView(courseCreditTextView);

                    courseTableLayout.addView(tableRow);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    private void submitSelectedCourses() {
        // Get reference to the database where you want to store the registered courses
        DatabaseReference registeredCoursesRef = FirebaseDatabase.getInstance().getReference("registered_courses").child("user_id"); // Use user's unique id

        for (Course course : selectedCourses) {
            String key = registeredCoursesRef.push().getKey();
            registeredCoursesRef.child(key).setValue(course) // Add selected courses to database
                    .addOnSuccessListener(aVoid -> {
                        // Data successfully written to the database
                        Toast.makeText(getContext(), "Courses Registered Successfully", Toast.LENGTH_SHORT).show();
                        // You can optionally clear the selectedCourses list here:
                        // selectedCourses.clear();
                    })
                    .addOnFailureListener(e -> {
                        // Handle database error
                        Toast.makeText(getContext(), "Error registering courses: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Course class
    public static class Course {
        private String courseCode;
        private String courseTitle;
        private int credit;

        public Course() {
            // Default constructor for Firebase
        }

        public Course(String courseCode, String courseTitle, int credit) {
            this.courseCode = courseCode;
            this.courseTitle = courseTitle;
            this.credit = credit;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public String getCourseTitle() {
            return courseTitle;
        }

        public int getCredit() {
            return credit;
        }
    }
}