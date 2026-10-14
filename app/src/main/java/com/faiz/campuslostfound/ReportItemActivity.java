package com.faiz.campuslostfound;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ReportItemActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput;
    private EditText locationInput, contactInput;
    private Button submitButton;
    private String reportType;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_item);

        TextView heading = findViewById(R.id.reportHeading);

        titleInput = findViewById(R.id.itemTitleInput);
        descriptionInput = findViewById(R.id.itemDescriptionInput);
        locationInput = findViewById(R.id.itemLocationInput);
        contactInput = findViewById(R.id.itemContactInput);
        submitButton = findViewById(R.id.submitReportButton);

        reportType = getIntent().getStringExtra("type");

        if (reportType == null) {
            reportType = "Lost";
        }

        heading.setText("Report " + reportType + " Item");

        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("reports");

        submitButton.setOnClickListener(v -> submitReport());
    }

    private void submitReport() {

        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        String contact = contactInput.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()
                || location.isEmpty() || contact.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        submitButton.setEnabled(false);

        String reportId = databaseReference.push().getKey();

        if (reportId == null) {
            submitButton.setEnabled(true);
            Toast.makeText(
                    this,
                    "Unable to create report",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Map<String, Object> report = new HashMap<>();

        report.put("id", reportId);
        report.put("type", reportType);
        report.put("title", title);
        report.put("description", description);
        report.put("location", location);
        report.put("contact", contact);

        databaseReference.child(reportId)
                .setValue(report)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            ReportItemActivity.this,
                            "Report saved successfully!",
                            Toast.LENGTH_LONG
                    ).show();

                    finish();
                })
                .addOnFailureListener(error -> {

                    submitButton.setEnabled(true);

                    Toast.makeText(
                            ReportItemActivity.this,
                            "Failed: " + error.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}