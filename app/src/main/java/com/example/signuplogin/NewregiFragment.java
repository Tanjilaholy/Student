package com.example.signuplogin;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class NewregiFragment extends Fragment {
    private TableLayout courseTableLayout;
    private Button submitButton;
    private List<String> selectedCourses = new ArrayList<>();
    private DatabaseReference coursesRef;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newregi, container, false);
        courseTableLayout = view.findViewById(R.id.course_table_layout);
        submitButton = view.findViewById(R.id.btn_submit_registration);
        coursesRef = FirebaseDatabase.getInstance().getReference("OfferedCourses");
        addTableHeaderRow();
        fetchCoursesFromFirebase();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSelectedCourses();
            }
        });
        return view;
    }
    private void addTableHeaderRow() {
        TableRow headerRow = new TableRow(getActivity());
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        headerRow.setLayoutParams(params);
        // Create the headers
        TextView snHeader = new TextView(getActivity());
        snHeader.setText("#");
        snHeader.setGravity(Gravity.CENTER);
        snHeader.setPadding(8, 8, 8, 8);
        snHeader.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f));
        TextView courseCodeHeader = new TextView(getActivity());
        courseCodeHeader.setText("Course Code");
        courseCodeHeader.setGravity(Gravity.CENTER);
        courseCodeHeader.setPadding(8, 8, 8, 8);
        courseCodeHeader.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f));
        TextView courseTitleHeader = new TextView(getActivity());
        courseTitleHeader.setText("Course Title");
        courseTitleHeader.setGravity(Gravity.CENTER);
        courseTitleHeader.setPadding(8, 8, 8, 8);
        courseTitleHeader.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));
        TextView creditHeader = new TextView(getActivity());
        creditHeader.setText("Credit");
        creditHeader.setGravity(Gravity.CENTER);  // Center the text
        creditHeader.setPadding(8, 8, 8, 8);
        creditHeader.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        TextView selectHeader = new TextView(getActivity());
        selectHeader.setText("Select");
        selectHeader.setGravity(Gravity.CENTER);  // Center the text
        selectHeader.setPadding(8, 8, 8, 8);
        selectHeader.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        headerRow.addView(snHeader);
        headerRow.addView(courseCodeHeader);
        headerRow.addView(courseTitleHeader);
        headerRow.addView(creditHeader);
        headerRow.addView(selectHeader);
        courseTableLayout.addView(headerRow);
    }
    private void fetchCoursesFromFirebase() {
        coursesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                courseTableLayout.removeAllViews();
                addTableHeaderRow();
                int serialNumber = 1;
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    String courseCode = courseSnapshot.child("courseCode").getValue(String.class);
                    String courseTitle = courseSnapshot.child("courseTitle").getValue(String.class);
                    String credit = courseSnapshot.child("credit").getValue(String.class);
                    addCourseRow(serialNumber++, courseCode, courseTitle, credit);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void addCourseRow(int serialNumber, String courseCode, String courseTitle, String credit) {
        TableRow courseRow = new TableRow(getActivity());
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        courseRow.setLayoutParams(params);

        TextView snView = new TextView(getActivity());
        snView.setText(String.valueOf(serialNumber));
        snView.setGravity(Gravity.CENTER);
        snView.setPadding(6, 6, 6, 6);
        snView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
        TextView courseCodeView = new TextView(getActivity());
        courseCodeView.setText(courseCode);
        courseCodeView.setGravity(Gravity.CENTER);
        courseCodeView.setPadding(6, 6, 6, 6);
        courseCodeView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        TextView courseTitleView = new TextView(getActivity());
        courseTitleView.setText(courseTitle);
        courseTitleView.setGravity(Gravity.CENTER);
        courseTitleView.setPadding(6, 6, 6, 6);
        courseTitleView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));
        TextView creditView = new TextView(getActivity());
        creditView.setText(credit);
        creditView.setGravity(Gravity.CENTER);
        creditView.setPadding(6, 6, 6, 6);
        creditView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        CheckBox checkBox = new CheckBox(getActivity());
        checkBox.setTag(courseCode);
        checkBox.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
        courseRow.addView(snView);
        courseRow.addView(courseCodeView);
        courseRow.addView(courseTitleView);
        courseRow.addView(creditView);
        courseRow.addView(checkBox);
        courseTableLayout.addView(courseRow);
    }
    private void submitSelectedCourses() {
        if (selectedCourses.isEmpty()) {
            Toast.makeText(getActivity(), "No courses selected", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference registrationRef = FirebaseDatabase.getInstance().getReference("UserRegistrations");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> registrationData = new HashMap<>();
        registrationData.put("selectedCourses", selectedCourses);
        registrationRef.child(userId).setValue(registrationData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Courses successfully registered!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Course registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

