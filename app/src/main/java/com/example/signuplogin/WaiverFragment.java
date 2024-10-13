package com.example.signuplogin;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WaiverFragment extends Fragment {

    private TextView waiverCodeTextView, waiverNameTextView, totalWaiverTextView, isActiveTextView;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waiver, container, false);

        waiverCodeTextView = view.findViewById(R.id.waiverCode);
        waiverNameTextView = view.findViewById(R.id.waiverName);
        totalWaiverTextView = view.findViewById(R.id.totalWaiver);
        isActiveTextView = view.findViewById(R.id.isActive);

        databaseReference = FirebaseDatabase.getInstance().getReference("waivers");
        fetchWaiverData();

        return view;
    }

    private void fetchWaiverData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot waiverSnapshot : dataSnapshot.getChildren()) {
                    String waiverCode = waiverSnapshot.child("waiverCode").getValue(String.class);
                    String waiverName = waiverSnapshot.child("waiverName").getValue(String.class);
                    String totalWaiver = waiverSnapshot.child("totalWaiver").getValue(String.class);
                    boolean isActive = waiverSnapshot.child("isActive").getValue(Boolean.class);

                    // Set text size to 20sp
                    waiverCodeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    waiverNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    totalWaiverTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    isActiveTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

                    // Set text values
                    waiverCodeTextView.setText(waiverCode);
                    waiverNameTextView.setText(waiverName);
                    totalWaiverTextView.setText(totalWaiver);
                    isActiveTextView.setText(isActive ? "Yes" : "No");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here, e.g., log the error or display a message to the user
            }
        });
    }
}