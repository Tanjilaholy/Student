package com.example.signuplogin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class scheduleadminActivity extends AppCompatActivity {

    private EditText semesterNameEditText, registrationStartEditText, registrationEndEditText,
            classStartEditText, classEndEditText, semesterDropEditText, addDropEditText,
            midTermStartEditText, finalExamStartEditText, terFillUpEditText;
    private Button submitButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduleadmin); // Assuming your layout file is activity_scheduleadmin.xml

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Semesters");

        // Initialize EditTexts
        semesterNameEditText = findViewById(R.id.semesterName);
        registrationStartEditText = findViewById(R.id.registrationStart);
        registrationEndEditText = findViewById(R.id.registrationEnd);
        classStartEditText = findViewById(R.id.classStart);
        classEndEditText = findViewById(R.id.classEnd);
        semesterDropEditText = findViewById(R.id.semesterDrop);
        addDropEditText = findViewById(R.id.addDrop);
        midTermStartEditText = findViewById(R.id.midTermStart);
        finalExamStartEditText = findViewById(R.id.finalExamStart);
        terFillUpEditText = findViewById(R.id.terFillUp);

        // Initialize Submit Button
        submitButton = findViewById(R.id.submitSemester);

        // Set Click Listener for Submit Button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSemesterToDatabase();
            }
        });
    }

    // Method to Add Semester Data to Firebase Realtime Database
    private void addSemesterToDatabase() {
        String semesterName = semesterNameEditText.getText().toString();
        String registrationStart = registrationStartEditText.getText().toString();
        String registrationEnd = registrationEndEditText.getText().toString();
        String classStart = classStartEditText.getText().toString();
        String classEnd = classEndEditText.getText().toString();
        String semesterDrop = semesterDropEditText.getText().toString();
        String addDrop = addDropEditText.getText().toString();
        String midTermStart = midTermStartEditText.getText().toString();
        String finalExamStart = finalExamStartEditText.getText().toString();
        String terFillUp = terFillUpEditText.getText().toString();

        // Validation - Ensure Fields Are Not Empty
        if (TextUtils.isEmpty(semesterName) || TextUtils.isEmpty(registrationStart) || TextUtils.isEmpty(registrationEnd)
                || TextUtils.isEmpty(classStart) || TextUtils.isEmpty(classEnd) || TextUtils.isEmpty(semesterDrop)
                || TextUtils.isEmpty(addDrop) || TextUtils.isEmpty(midTermStart) || TextUtils.isEmpty(finalExamStart)
                || TextUtils.isEmpty(terFillUp)) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique ID for each semester entry
        String semesterId = databaseReference.push().getKey();

        // Create a HashMap to store the semester details
        HashMap<String, String> semesterData = new HashMap<>();
        semesterData.put("semesterName", semesterName);
        semesterData.put("registrationStart", registrationStart);
        semesterData.put("registrationEnd", registrationEnd);
        semesterData.put("classStart", classStart);
        semesterData.put("classEnd", classEnd);
        semesterData.put("semesterDrop", semesterDrop);
        semesterData.put("addDrop", addDrop);
        semesterData.put("midTermStart", midTermStart);
        semesterData.put("finalExamStart", finalExamStart);
        semesterData.put("terFillUp", terFillUp); // Assuming "terFillUp" is the key for Final Exam End Date

        // Store the data in the Firebase Realtime Database under the unique semesterId
        databaseReference.child(semesterId).setValue(semesterData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(scheduleadminActivity.this, "Semester Added Successfully", Toast.LENGTH_SHORT).show();
                        clearFields(); // Clear fields after successful submission
                    } else {
                        Toast.makeText(scheduleadminActivity.this, "Failed to add Semester", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to Clear Input Fields After Submission
    private void clearFields() {
        semesterNameEditText.setText("");
        registrationStartEditText.setText("");
        registrationEndEditText.setText("");
        classStartEditText.setText("");
        classEndEditText.setText("");
        semesterDropEditText.setText("");
        addDropEditText.setText("");
        midTermStartEditText.setText("");
        finalExamStartEditText.setText("");
        terFillUpEditText.setText("");
    }
}