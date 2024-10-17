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

import androidx.activity.EdgeToEdge;
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

    private EditText semesterName, semesterNumber, totalCredits, courseCode, courseTitle, credit;
    private Button btnUpdateSemester, btnAddCourse, btnSubmitCourses;
    private TableLayout courseTableLayout;
    private DatabaseReference databaseReference;
    private ArrayList<Course> offeredCourses = new ArrayList<>();
    private int courseCounter = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_regiadmin);
        databaseReference = FirebaseDatabase.getInstance().getReference();
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
        btnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCourseToTable();
            }
        });
        btnSubmitCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCoursesToDatabase();
            }
        });
        btnUpdateSemester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSemesterInfo();
            }
        });
    }
    private void addCourseToTable() {
        String code = courseCode.getText().toString();
        String title = courseTitle.getText().toString();
        String creditValue = credit.getText().toString();
        if (code.isEmpty() || title.isEmpty() || creditValue.isEmpty()) {
            Toast.makeText(this, "Please fill in all course fields", Toast.LENGTH_SHORT).show();
            return;
        }
        offeredCourses.add(new Course(code, title, creditValue));
        TableRow tableRow = new TableRow(this);
        TextView numberView = new TextView(this);
        numberView.setText(String.valueOf(courseCounter++));
        TextView codeView = new TextView(this);
        codeView.setText(code);
        TextView titleView = new TextView(this);
        titleView.setText(title);
        TextView creditView = new TextView(this);
        creditView.setText(creditValue);
        CheckBox checkBox = new CheckBox(this);
        tableRow.addView(numberView);
        tableRow.addView(codeView);
        tableRow.addView(titleView);
        tableRow.addView(creditView);
        tableRow.addView(checkBox);
        courseTableLayout.addView(tableRow);
        courseCode.setText("");
        courseTitle.setText("");
        credit.setText("");
    }
    private void submitCoursesToDatabase() {
        if (offeredCourses.isEmpty()) {
            Toast.makeText(this, "No courses to submit", Toast.LENGTH_SHORT).show();
            return;
        }
        for (Course course : offeredCourses) {
            String courseId = databaseReference.push().getKey();
            databaseReference.child("OfferedCourses").child(courseId).setValue(course)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(regiadminActivity.this, "Courses Submitted Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(regiadminActivity.this, "Error Submitting Courses", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        offeredCourses.clear();
        courseTableLayout.removeViews(1, courseCounter - 1);
        courseCounter = 1;
    }
    private void updateSemesterInfo() {
        String name = semesterName.getText().toString();
        String number = semesterNumber.getText().toString();
        String totalCredit = totalCredits.getText().toString();
        if (name.isEmpty() || number.isEmpty() || totalCredit.isEmpty()) {
            Toast.makeText(this, "Please fill in all semester fields", Toast.LENGTH_SHORT).show();
            return;
        }
        Semester semester = new Semester(name, number, totalCredit);
        databaseReference.child("SemesterInfo").setValue(semester)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(regiadminActivity.this, "Semester Info Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(regiadminActivity.this, "Error Updating Semester Info", Toast.LENGTH_SHORT).show();
                    }
                });
    }
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
