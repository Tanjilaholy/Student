package com.example.signuplogin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

public class ScheduleFragment extends Fragment {

    private TextView semesterName;
    private TextView registrationStart;
    private TextView registrationEnd;
    private TextView classStart;
    private TextView classEnd;
    private TextView midTermStart;
    private TextView finalExamStart;
    private TextView semesterDrop;
    private TextView addDrop;
    private TextView terFillUp;

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        semesterName = view.findViewById(R.id.semesterName);
        registrationStart = view.findViewById(R.id.registrationStart);
        registrationEnd = view.findViewById(R.id.registrationEnd);
        classStart = view.findViewById(R.id.classStart);
        classEnd = view.findViewById(R.id.classEnd);
        midTermStart = view.findViewById(R.id.midTermStart);
        finalExamStart = view.findViewById(R.id.finalExamStart);
        semesterDrop = view.findViewById(R.id.semesterDrop);
        addDrop = view.findViewById(R.id.addDrop);
        terFillUp = view.findViewById(R.id.terFillUp);

        databaseReference = FirebaseDatabase.getInstance().getReference("Semesters");
        fetchSemesterData();

        return view;
    }

    private void fetchSemesterData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot semesterSnapshot : dataSnapshot.getChildren()) {
                        // Declare and initialize variables here
                        String semester = semesterSnapshot.child("semesterName").getValue(String.class);
                        String regStart = semesterSnapshot.child("registrationStart").getValue(String.class);
                        String regEnd = semesterSnapshot.child("registrationEnd").getValue(String.class);
                        String classStartDate = semesterSnapshot.child("classStart").getValue(String.class);
                        String classEndDate = semesterSnapshot.child("classEnd").getValue(String.class);
                        String midTerm = semesterSnapshot.child("midTermStart").getValue(String.class);
                        String finalExam = semesterSnapshot.child("finalExamStart").getValue(String.class);
                        String dropLast = semesterSnapshot.child("semesterDrop").getValue(String.class);
                        String addDropStart = semesterSnapshot.child("addDrop").getValue(String.class);
                        String terFillUpStart = semesterSnapshot.child("terFillUp").getValue(String.class);

                        semesterName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                        registrationStart.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                        registrationEnd.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                        classStart.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                        classEnd.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                        midTermStart.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                        finalExamStart.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                        semesterDrop.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                        addDrop.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                        terFillUp.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);


                        semesterName.setText(semester);
                        registrationStart.setText(regStart);
                        registrationEnd.setText(regEnd);
                        classStart.setText(classStartDate);
                        classEnd.setText(classEndDate);
                        midTermStart.setText(midTerm);

                        finalExamStart.setText(finalExam);
                        semesterDrop.setText(dropLast);
                        addDrop.setText(addDropStart);
                        terFillUp.setText(terFillUpStart);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}