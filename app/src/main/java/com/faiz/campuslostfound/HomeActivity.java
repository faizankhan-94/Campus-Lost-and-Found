package com.faiz.campuslostfound;

import com.google.firebase.auth.FirebaseAuth;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;

import android.widget.EditText;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout reportsContainer;
    private DatabaseReference reportsReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button lostButton = findViewById(R.id.reportLostButton);
        Button foundButton = findViewById(R.id.reportFoundButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        reportsContainer = findViewById(R.id.reportsContainer);

        reportsReference = FirebaseDatabase.getInstance()
                .getReference("reports");

        lostButton.setOnClickListener(v -> openReport("Lost"));
        foundButton.setOnClickListener(v -> openReport("Found"));

        logoutButton.setOnClickListener(v -> {

            // Firebase se logout
            FirebaseAuth.getInstance().signOut();

            // Login screen open karo
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        loadReports();
        setupSearch();
    }

    private void openReport(String type) {
        Intent intent = new Intent(HomeActivity.this, ReportItemActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    private void setupSearch() {
        EditText searchInput = findViewById(R.id.searchInput);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                filterReports(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterReports(String query) {
        for (int i = 0; i < reportsContainer.getChildCount(); i++) {
            View card = reportsContainer.getChildAt(i);

            if (!(card instanceof LinearLayout)) {
                continue;
            }

            LinearLayout reportCard = (LinearLayout) card;

            TextView titleView = (TextView) reportCard.getChildAt(0);
            TextView typeView = (TextView) reportCard.getChildAt(1);
            TextView locationView = (TextView) reportCard.getChildAt(2);

            String title = titleView.getText().toString();
            String type = typeView.getText().toString();
            String location = locationView.getText().toString();

            boolean matches =
                    title.toLowerCase().contains(query.toLowerCase())
                            || type.toLowerCase().contains(query.toLowerCase())
                            || location.toLowerCase().contains(query.toLowerCase());

            card.setVisibility(matches ? View.VISIBLE : View.GONE);
        }
    }
    private void loadReports() {
        reportsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                reportsContainer.removeAllViews();

                if (!snapshot.exists()) {
                    addMessage("No reports available yet.");
                    return;
                }

                for (DataSnapshot item : snapshot.getChildren()) {
                    String title = item.child("title").getValue(String.class);
                    String type = item.child("type").getValue(String.class);
                    String location = item.child("location").getValue(String.class);
                    String description = item.child("description").getValue(String.class);
                    String contact = item.child("contact").getValue(String.class);

                    addReportCard(title, type, location, description, contact);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                reportsContainer.removeAllViews();
                addMessage("Unable to load reports: " + error.getMessage());
            }
        });
    }

    private void addMessage(String message) {
        TextView text = new TextView(this);
        text.setText(message);
        text.setTextColor(Color.GRAY);
        text.setTextSize(14);
        text.setPadding(18, 18, 18, 18);
        text.setGravity(View.TEXT_ALIGNMENT_CENTER);
        reportsContainer.addView(text);
    }

    private void addReportCard(String title, String type,
                               String location, String description,
                               String contact) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 14, 16, 14);

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(16);
        background.setStroke(1, Color.LTGRAY);
        card.setBackground(background);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 8, 0, 8);
        card.setLayoutParams(cardParams);

        TextView name = new TextView(this);
        name.setText(title == null ? "Unnamed Item" : title);
        name.setTextSize(18);
        name.setTypeface(null, Typeface.BOLD);
        name.setTextColor(Color.rgb(29, 38, 56));
        card.addView(name);

        TextView status = new TextView(this);
        status.setText(type == null ? "Item" : type.toUpperCase());
        status.setTextSize(12);
        status.setTypeface(null, Typeface.BOLD);
        status.setTextColor("Found".equalsIgnoreCase(type)
                ? Color.rgb(22, 130, 80)
                : Color.rgb(200, 85, 61));
        status.setPadding(0, 6, 0, 6);
        card.addView(status);

        TextView place = new TextView(this);
        place.setText("Location: " +
                (location == null ? "Not provided" : location));
        place.setTextSize(14);
        place.setTextColor(Color.DKGRAY);
        card.addView(place);

        TextView details = new TextView(this);
        details.setText(description == null ? "" : description);
        details.setTextSize(14);
        details.setTextColor(Color.GRAY);
        details.setPadding(0, 6, 0, 0);
        card.addView(details);
        TextView contactText = new TextView(this);

        contactText.setText("Contact: " +
                (contact == null || contact.trim().isEmpty()
                        ? "Not provided"
                        : contact));

        contactText.setTextSize(14);
        contactText.setTextColor(Color.rgb(29, 38, 56));
        contactText.setPadding(0, 6, 0, 0);

        card.addView(contactText);

        reportsContainer.addView(card);
    }
}