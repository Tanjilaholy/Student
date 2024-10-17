package com.example.signuplogin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scheduleadmin);

        databaseReference = FirebaseDatabase.getInstance().getReference("Semesters");

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

        submitButton = findViewById(R.id.submitSemester);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSemesterToDatabase();
            }
        });
    }

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

        if (TextUtils.isEmpty(semesterName) || TextUtils.isEmpty(registrationStart) || TextUtils.isEmpty(registrationEnd)
                || TextUtils.isEmpty(classStart) || TextUtils.isEmpty(classEnd) || TextUtils.isEmpty(semesterDrop)
                || TextUtils.isEmpty(addDrop) || TextUtils.isEmpty(midTermStart) || TextUtils.isEmpty(finalExamStart)
                || TextUtils.isEmpty(terFillUp)) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String semesterId = databaseReference.push().getKey();


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
        semesterData.put("terFillUp", terFillUp);

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