package com.example.signuplogin;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class regiadminActivity<Map> extends AppCompatActivity {

    // UI elements
    private EditText semesterName, semesterNumber, totalCredits, courseCode, courseTitle, credit;
    private Button btnUpdateSemester, btnAddCourse, btnSubmitCourses;
    private TableLayout courseTableLayout;
    // Firebase Database reference
    private DatabaseReference databaseReference;
    // ArrayList to hold offered courses
    private ArrayList<Course> offeredCourses = new ArrayList<>();
    // Counter for course table rows
    private int courseCounter = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiadmin);
        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();
        // Initialize UI elements
        semesterName = findViewById(R.id.semester_name);
        semesterNumber = findViewById(R.id.edit_semester_number);
        totalCredits = findViewById(R.id.edit_total_credit);
        courseCode = findViewById(R.id.edit_course_code);
        courseTitle = findViewById(R.id.edit_course_title);
        credit = findViewById(R.id.edit_credit);
        btnUpdateSemester = findViewById(R.id.btn_update_semester);
        btnAddCourse = findViewById(R.id.btn_add_course);
        btnSubmitCourses = findViewById(R.id.btn_submit_courses);
        courseTableLayout = findViewById(R.id.course_table_layout);
        // Add a new course on button click
        btnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCourseToTable();
            }
        });
        // Submit courses to the database on button click
        btnSubmitCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCoursesToDatabase();
            }
        });
        // Update semester information on button click
        btnUpdateSemester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSemesterInfo();
            }
        });
    }
    // Method to add a course to the table
    private void addCourseToTable() {
        String code = courseCode.getText().toString();
        String title = courseTitle.getText().toString();
        String creditValue = credit.getText().toString();
        if (code.isEmpty() || title.isEmpty() || creditValue.isEmpty()) {
            Toast.makeText(this, "Please fill in all course fields", Toast.LENGTH_SHORT).show();
            return;
        }
        // Add the course to the offeredCourses list
        offeredCourses.add(new Course(code, title, creditValue));
        // Create a new table row
        TableRow tableRow = new TableRow(this);
        // Create TextViews for course details
        TextView numberView = new TextView(this);
        numberView.setText(String.valueOf(courseCounter++));
        TextView codeView = new TextView(this);
        codeView.setText(code);
        TextView titleView = new TextView(this);
        titleView.setText(title);
        TextView creditView = new TextView(this);
        creditView.setText(creditValue);
        CheckBox checkBox = new CheckBox(this);
        // Add views to the table row
        tableRow.addView(numberView);
        tableRow.addView(codeView);
        tableRow.addView(titleView);
        tableRow.addView(creditView);
        tableRow.addView(checkBox);
        // Add the row to the table layout
        courseTableLayout.addView(tableRow);
        // Clear the input fields
        courseCode.setText("");
        courseTitle.setText("");
        credit.setText("");
    }
    // Method to submit the courses to Firebase
    private void submitCoursesToDatabase() {
        if (offeredCourses.isEmpty()) {
            Toast.makeText(this, "No courses to submit", Toast.LENGTH_SHORT).show();
            return;
        }
        for (Course course : offeredCourses) {
            // Generate a unique ID for each course
            String courseId = databaseReference.push().getKey();
            // Submit the course data to Firebase under the unique courseId
            databaseReference.child("OfferedCourses").child(courseId).setValue(course)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(regiadminActivity.this, "Courses Submitted Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(regiadminActivity.this, "Error Submitting Courses", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        offeredCourses.clear(); // Clear the course list after submission
        courseTableLayout.removeViews(1, courseCounter - 1); // Clear the table rows
        courseCounter = 1; // Reset the counter
    }
    // Method to update semester information in Firebase
    private void updateSemesterInfo() {
        String name = semesterName.getText().toString();
        String number = semesterNumber.getText().toString();
        String totalCredit = totalCredits.getText().toString();
        if (name.isEmpty() || number.isEmpty() || totalCredit.isEmpty()) {
            Toast.makeText(this, "Please fill in all semester fields", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create a Semester object to store semester details
        Semester semester = new Semester(name, number, totalCredit);
        // Store the semester data in Firebase
        databaseReference.child("SemesterInfo").setValue(semester)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(regiadminActivity.this, "Semester Info Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(regiadminActivity.this, "Error Updating Semester Info", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // Inner class to represent a course
    public class Course {
        private String courseCode;
        private String courseTitle;
        private String credit;
        public Course(String courseCode, String courseTitle, String credit) {
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
        public String getCredit() {
            return credit;
        }
    }
    // Inner class to represent semester
    public class Semester {
        private String semesterName;
        private String semesterNumber;
        private String totalCredits;
        public Semester(String semesterName, String semesterNumber, String totalCredits) {
            this.semesterName = semesterName;
            this.semesterNumber = semesterNumber;
            this.totalCredits = totalCredits;
        }
        public String getSemesterName() {
            return semesterName;
        }
        public String getSemesterNumber() {
            return semesterNumber;
        }
        public String getTotalCredits() {
            return totalCredits;
        }
    }
}
