package com.example.signuplogin;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
public class NewregiFragment extends Fragment {
    private TableLayout courseTableLayout;
    private Button submitButton;
    private List<Map<String, String>> selectedCourses = new ArrayList<>();
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


        TextView snHeader = createHeaderTextView("#", 0.3f);
        TextView courseCodeHeader = createHeaderTextView("Course Code", 1.5f);
        TextView courseTitleHeader = createHeaderTextView("Course Title", 2f);
        TextView creditHeader = createHeaderTextView("Credit", 1f);
        TextView selectHeader = createHeaderTextView("Select", 1f);

        headerRow.addView(snHeader);
        headerRow.addView(courseCodeHeader);
        headerRow.addView(courseTitleHeader);
        headerRow.addView(creditHeader);
        headerRow.addView(selectHeader);


        courseTableLayout.addView(headerRow);
    }

    private TextView createHeaderTextView(String text, float weight) {
        TextView textView = new TextView(getActivity());
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(8, 8, 8, 8);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight));
        textView.setTextColor(getResources().getColor(R.color.primary));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        return textView;
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
                Toast.makeText(getActivity(), "Failed to load courses.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCourseRow(int serialNumber, String courseCode, String courseTitle, String credit) {
        TableRow courseRow = new TableRow(getActivity());
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        courseRow.setLayoutParams(params);

        TextView snView = createTextView(String.valueOf(serialNumber), 0.3f);
        TextView courseCodeView = createTextView(courseCode, 1.5f);
        TextView courseTitleView = createTextView(courseTitle, 2f);
        courseTitleView.setTypeface(null, Typeface.BOLD); // Semi-bold for fetched data
        TextView creditView = createTextView(credit, 1f);

        // Checkbox to select the course with OnCheckedChangeListener
        CheckBox checkBox = new CheckBox(getActivity());
        checkBox.setTag(courseCode);
        checkBox.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Map<String, String> courseData = new HashMap<>();
                    courseData.put("courseCode", courseCode);
                    courseData.put("courseTitle", courseTitle);
                    courseData.put("credit", credit);
                    selectedCourses.add(courseData);
                } else {
                    selectedCourses.removeIf(course -> course.get("courseCode").equals(courseCode));
                }
            }
        });

        courseRow.addView(snView);
        courseRow.addView(courseCodeView);
        courseRow.addView(courseTitleView);
        courseRow.addView(creditView);
        courseRow.addView(checkBox);

        courseTableLayout.addView(courseRow);
    }

    private TextView createTextView(String text, float weight) {
        TextView textView = new TextView(getActivity());
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(6, 6, 6, 6);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight));
        textView.setTypeface(null, Typeface.BOLD); // Semi-bold for fetched data
        return textView;
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

        registrationRef.child(userId).setValue(registrationData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Courses successfully registered!", Toast.LENGTH_SHORT).show();
                            selectedCourses.clear(); // Clear selected courses after successful submission
                        } else {
                            Toast.makeText(getActivity(), "Course registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}