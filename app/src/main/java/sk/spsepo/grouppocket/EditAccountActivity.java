package sk.spsepo.grouppocket;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class EditAccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        
        // Make status bar #141414 with white icons
        getWindow().setStatusBarColor(Color.parseColor("#141414"));

        // Make navigation bar black with white icons
        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.black));

        // Add click listeners
        findViewById(R.id.profilePictureSetting).setOnClickListener(v -> {
            // Handle profile picture change
        });

        findViewById(R.id.changeNameSetting).setOnClickListener(v -> {
            // Handle name change
        });
    }
} 