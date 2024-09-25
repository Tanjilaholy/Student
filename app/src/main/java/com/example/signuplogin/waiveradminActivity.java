package com.example.signuplogin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class waiveradminActivity extends AppCompatActivity {

    private EditText editTextWaiverCode, editTextWaiverName, editTextTotalWaiver, editTextIsActive;
    private Button buttonSubmitWaiver;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiveradmin);

        databaseReference = FirebaseDatabase.getInstance().getReference("waivers");

        editTextWaiverCode = findViewById(R.id.editTextWaiverCode);
        editTextWaiverName = findViewById(R.id.editTextWaiverName);
        editTextTotalWaiver = findViewById(R.id.editTextTotalWaiver);
        editTextIsActive = findViewById(R.id.editTextIsActive);
        buttonSubmitWaiver = findViewById(R.id.buttonSubmitWaiver);

        buttonSubmitWaiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitWaiverInfo();
            }
        });
    }

    private void submitWaiverInfo() {
        String waiverCode = editTextWaiverCode.getText().toString();
        String waiverName = editTextWaiverName.getText().toString();
        String totalWaiver = editTextTotalWaiver.getText().toString();
        String isActive = editTextIsActive.getText().toString();


        if (waiverCode.isEmpty() || waiverName.isEmpty() || totalWaiver.isEmpty() || isActive.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> waiverData = new HashMap<>();
        waiverData.put("waiverCode", waiverCode);
        waiverData.put("waiverName", waiverName);
        waiverData.put("totalWaiver", totalWaiver);
        waiverData.put("isActive", isActive.equals("Yes"));

        databaseReference.push().setValue(waiverData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(waiveradminActivity.this, "Waiver Info Submitted", Toast.LENGTH_SHORT).show();

                editTextWaiverCode.setText("");
                editTextWaiverName.setText("");
                editTextTotalWaiver.setText("");
                editTextIsActive.setText("");
            } else {
                Toast.makeText(waiveradminActivity.this, "Submission Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
