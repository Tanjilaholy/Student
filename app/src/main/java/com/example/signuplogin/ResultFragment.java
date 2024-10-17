package com.example.signuplogin;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
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


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.semester_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semesterSpinner.setAdapter(adapter);

        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedSemester = parentView.getItemAtPosition(position).toString();
                loadResultsForSemester(selectedSemester);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        return view;
    }

    private void loadResultsForSemester(String semester) {
        int childCount = resultTable.getChildCount();
        if (childCount > 1) {
            resultTable.removeViews(1, childCount - 1);
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Results").child(semester);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String courseCode = snapshot.child("courseCode").getValue(String.class);
                    String courseTitle = snapshot.child("courseTitle").getValue(String.class);
                    String grade = snapshot.child("grade").getValue(String.class);
                    String gradePoint = snapshot.child("gradePoint").getValue(String.class);

                    TableRow tableRow = new TableRow(getContext());
                    tableRow.setPadding(8, 8, 8, 8);

                    TextView courseCodeView = new TextView(getContext());
                    courseCodeView.setText(courseCode);
                    courseCodeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    courseCodeView.setPadding(8, 8, 8, 8);
                    tableRow.addView(courseCodeView);

                    TextView courseTitleView = new TextView(getContext());
                    courseTitleView.setText(courseTitle);
                    courseTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    courseTitleView.setPadding(8, 8, 8, 8);
                    tableRow.addView(courseTitleView);

                    TextView gradeView = new TextView(getContext());
                    gradeView.setText(grade);
                    gradeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    gradeView.setPadding(8, 8, 8, 8);
                    tableRow.addView(gradeView);

                    TextView gradePointView = new TextView(getContext());
                    gradePointView.setText(gradePoint);
                    gradePointView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    gradePointView.setPadding(8, 8, 8, 8);
                    tableRow.addView(gradePointView);

                    tableRow.setBackgroundColor(Color.parseColor("#FFFFFF"));

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