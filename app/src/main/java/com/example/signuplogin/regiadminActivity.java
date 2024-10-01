package com.example.signuplogin;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class regiadminActivity extends AppCompatActivity {

    private EditText semesterNameEditText, semesterNumberEditText, totalCreditEditText;
    private EditText courseCodeEditText, courseTitleEditText, creditEditText;
    private Button updateSemesterButton, addCourseButton, submitCoursesButton;
    private TableLayout courseTableLayout;

    private DatabaseReference databaseReference;
    private List<Map<String, Object>> courses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiadmin);

        // Initialize views
        semesterNameEditText = findViewById(R.id.semester_name);
        semesterNumberEditText = findViewById(R.id.edit_semester_number);
        totalCreditEditText = findViewById(R.id.edit_total_credit);
        updateSemesterButton = findViewById(R.id.btn_update_semester);

        courseCodeEditText = findViewById(R.id.edit_course_code);
        courseTitleEditText = findViewById(R.id.edit_course_title);
        creditEditText = findViewById(R.id.edit_credit);
        addCourseButton = findViewById(R.id.btn_add_course);

        courseTableLayout = findViewById(R.id.course_table_layout);
        submitCoursesButton = findViewById(R.id.btn_submit_courses);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("courses");

        // Update semester info
        updateSemesterButton.setOnClickListener(v -> {
            String semesterName = semesterNameEditText.getText().toString();
            String semesterNumber = semesterNumberEditText.getText().toString();
            String totalCredit = totalCreditEditText.getText().toString();

            // Create a HashMap to store semester data
            Map<String, Object> semesterData = new HashMap<>();
            semesterData.put("semesterName", semesterName);
            semesterData.put("semesterNumber", semesterNumber);
            semesterData.put("totalCredit", totalCredit);

            // Update semester info in the database
            databaseReference.child("semesterInfo").setValue(semesterData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(regiadminActivity.this, "Semester info updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(regiadminActivity.this, "Error updating semester info: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        // Add course
        addCourseButton.setOnClickListener(v -> {
            String courseCode = courseCodeEditText.getText().toString();
            String courseTitle = courseTitleEditText.getText().toString();
            double credit = Double.parseDouble(creditEditText.getText().toString());

            Map<String, Object> course = new HashMap<>();
            course.put("courseCode", courseCode);
            course.put("courseTitle", courseTitle);
            course.put("credit", credit);

            courses.add(course);
            addCourseToTable(course);

            // Clear input fields
            courseCodeEditText.setText("");
            courseTitleEditText.setText("");
            creditEditText.setText("");
        });

        // Submit courses
        submitCoursesButton.setOnClickListener(v -> {
            // Get the semester information
            String semesterName = semesterNameEditText.getText().toString();
            String semesterNumber = semesterNumberEditText.getText().toString();
            String totalCredit = totalCreditEditText.getText().toString();

            // Create a HashMap to store semester data
            Map<String, Object> semesterData = new HashMap<>();
            semesterData.put("semesterName", semesterName);
            semesterData.put("semesterNumber", semesterNumber);
            semesterData.put("totalCredit", totalCredit);

            // Create a HashMap to store all courses for the semester
            Map<String, Object> allCoursesData = new HashMap<>();
            allCoursesData.put("semesterInfo", semesterData); // Store semester info
            allCoursesData.put("courses", courses); // Store the list of courses

            // Upload all data to the database under a unique semester key
            String semesterKey = semesterName + "_" + semesterNumber; // Create a unique key for the semester
            databaseReference.child(semesterKey).setValue(allCoursesData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(regiadminActivity.this, "Courses and semester info submitted", Toast.LENGTH_SHORT).show();
                        // Clear input fields or perform other actions after successful submission
                        courses.clear(); // Clear the courses list
                        courseTableLayout.removeAllViews(); // Clear the table layout
                        // ... (Clear other input fields as needed) ...
                    })
                    .addOnFailureListener(e -> Toast.makeText(regiadminActivity.this, "Error submitting data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    private void addCourseToTable(Map<String, Object> course) {
        TableRow row = new TableRow(this);

        TextView serialTextView = new TextView(this);
        serialTextView.setText(String.valueOf(courseTableLayout.getChildCount())); // Serial number
        serialTextView.setPadding(8, 8, 8, 8);
        row.addView(serialTextView);

        TextView courseCodeTextView = new TextView(this);
        courseCodeTextView.setText((String) course.get("courseCode"));
        courseCodeTextView.setPadding(8, 8, 8, 8);
        row.addView(courseCodeTextView);

        TextView courseTitleTextView = new TextView(this);
        courseTitleTextView.setText((String) course.get("courseTitle"));
        courseTitleTextView.setPadding(8, 8, 8, 8);
        row.addView(courseTitleTextView);

        TextView creditTextView = new TextView(this);
        creditTextView.setText(String.valueOf(course.get("credit")));
        creditTextView.setPadding(8, 8, 8, 8);
        row.addView(creditTextView);

        CheckBox checkBox = new CheckBox(this);
        checkBox.setPadding(8, 8, 8, 8);
        row.addView(checkBox);

        courseTableLayout.addView(row);
    }
}