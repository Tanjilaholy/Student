package com.example.signuplogin;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class resultadminActivity extends AppCompatActivity {

    private Spinner semesterSpinner;
    private EditText courseCodeInput, courseTitleInput, gradeInput, gradePointInput;
    private TableLayout resultTable;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resultadmin);

        databaseReference = FirebaseDatabase.getInstance().getReference("Results");


        semesterSpinner = findViewById(R.id.semesterSpinner);
        courseCodeInput = findViewById(R.id.courseCodeInput);
        courseTitleInput = findViewById(R.id.courseTitleInput);
        gradeInput = findViewById(R.id.gradeInput);
        gradePointInput = findViewById(R.id.gradePointInput);
        resultTable = findViewById(R.id.resultTable);
        Button addButton = findViewById(R.id.addButton);
        Button submitButton = findViewById(R.id.submitButton);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.semester_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semesterSpinner.setAdapter(adapter);

        addButton.setOnClickListener(v -> addResultRow());

        submitButton.setOnClickListener(v -> submitResultsToDatabase());
    }

    private void addResultRow() {
        String courseCode = courseCodeInput.getText().toString().trim();
        String courseTitle = courseTitleInput.getText().toString().trim();
        String grade = gradeInput.getText().toString().trim();
        String gradePoint = gradePointInput.getText().toString().trim();

        if (courseCode.isEmpty() || courseTitle.isEmpty() || grade.isEmpty() || gradePoint.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        TableRow newRow = new TableRow(this);

        TextView codeView = createTextView(courseCode);
        TextView titleView = createTextView(courseTitle);
        TextView gradeView = createTextView(grade);
        TextView gradePointView = createTextView(gradePoint);
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");

        deleteButton.setOnClickListener(v -> {
            resultTable.removeView(newRow);
            deleteResultFromDatabase(courseCode);
        });


        newRow.addView(codeView);
        newRow.addView(titleView);
        newRow.addView(gradeView);
        newRow.addView(gradePointView);
        newRow.addView(deleteButton);


        resultTable.addView(newRow);

        clearInputs();
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        return textView;
    }

    private void clearInputs() {
        courseCodeInput.setText("");
        courseTitleInput.setText("");
        gradeInput.setText("");
        gradePointInput.setText("");
    }

    private void deleteResultFromDatabase(String courseCode) {
        String semester = semesterSpinner.getSelectedItem().toString();
        databaseReference.child(semester).child(courseCode).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(resultadminActivity.this, "Deleted from database", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(resultadminActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show());
    }

    private void submitResultsToDatabase() {
        for (int i = 1; i < resultTable.getChildCount(); i++) {
            TableRow row = (TableRow) resultTable.getChildAt(i);

            String courseCode = ((TextView) row.getChildAt(0)).getText().toString();
            String courseTitle = ((TextView) row.getChildAt(1)).getText().toString();
            String grade = ((TextView) row.getChildAt(2)).getText().toString();
            String gradePoint = ((TextView) row.getChildAt(3)).getText().toString();

            saveResultToDatabase(courseCode, courseTitle, grade, gradePoint);
        }
        Toast.makeText(this, "Results submitted", Toast.LENGTH_SHORT).show();
    }

    private void saveResultToDatabase(String courseCode, String courseTitle, String grade, String gradePoint) {
        String semester = semesterSpinner.getSelectedItem().toString();

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("courseCode", courseCode);
        resultData.put("courseTitle", courseTitle);
        resultData.put("grade", grade);
        resultData.put("gradePoint", gradePoint);

        databaseReference.child(semester).child(courseCode).setValue(resultData)



                .addOnSuccessListener(aVoid -> Toast.makeText(resultadminActivity.this, "Result saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(resultadminActivity.this, "Failed to save result", Toast.LENGTH_SHORT).show());
    }
}