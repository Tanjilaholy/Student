package com.example.signuplogin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResultFragment extends Fragment {

    private Spinner semesterSpinner;
    private TableLayout resultTable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        semesterSpinner = view.findViewById(R.id.semesterSpinner);
        resultTable = view.findViewById(R.id.resultTable);

        // Set up the semester spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.semester_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semesterSpinner.setAdapter(adapter);

        // Load results when a semester is selected
        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedSemester = parentView.getItemAtPosition(position).toString();
                loadResultsForSemester(selectedSemester);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        return view;
    }

    private void loadResultsForSemester(String semester) {
        // Clear existing rows (except the header)
        int childCount = resultTable.getChildCount();
        if (childCount > 1) {
            resultTable.removeViews(1, childCount - 1);
        }

        // Query Firebase to get the results for the selected semester
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Results").child(semester);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Fetch course data from Firebase
                    String courseCode = snapshot.child("courseCode").getValue(String.class);
                    String courseTitle = snapshot.child("courseTitle").getValue(String.class);
                    String grade = snapshot.child("grade").getValue(String.class);
                    String gradePoint = snapshot.child("gradePoint").getValue(String.class);

                    // Create a new table row
                    TableRow tableRow = new TableRow(getContext());
                    tableRow.setPadding(8, 8, 8, 8); // Add padding

                    // Create and add TextViews for courseCode, courseTitle, grade, and gradePoint
                    TextView courseCodeView = new TextView(getContext());
                    courseCodeView.setText(courseCode);
                    tableRow.addView(courseCodeView);

                    TextView courseTitleView = new TextView(getContext());
                    courseTitleView.setText(courseTitle);
                    tableRow.addView(courseTitleView);

                    TextView gradeView = new TextView(getContext());
                    gradeView.setText(grade);
                    tableRow.addView(gradeView);

                    TextView gradePointView = new TextView(getContext());
                    gradePointView.setText(gradePoint);
                    tableRow.addView(gradePointView);

                    // Set background color for the rows (optional)
                    tableRow.setBackgroundColor(Color.parseColor("#FFFFFF")); // White background for rows

                    // Add the row to the TableLayout
                    resultTable.addView(tableRow);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error fetching results", Toast.LENGTH_SHORT).show();
            }
        });
    }
}