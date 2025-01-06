package sk.spsepo.grouppocket;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class AccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        
        // Make status bar #141414 with white icons
        getWindow().setStatusBarColor(Color.parseColor("#141414"));

        // Make navigation bar black with white icons
        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.black));

        // Add click listeners for navigation buttons
        findViewById(R.id.navigationButton).setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.accountButton).setOnClickListener(v -> {
            // Already on AccountActivity, no action needed
        });

        // Add click listener for edit account
        findViewById(R.id.editAccountSetting).setOnClickListener(v -> {
            EditAccountBottomSheet bottomSheet = new EditAccountBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "editAccountBottomSheet");
        });

        findViewById(R.id.themeSetting).setOnClickListener(v -> {
            ThemeBottomSheet bottomSheet = new ThemeBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "themeBottomSheet");
        });

        findViewById(R.id.currencySetting).setOnClickListener(v -> {
            CurrencyBottomSheet bottomSheet = new CurrencyBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "currencyBottomSheet");
        });

        findViewById(R.id.notificationsSetting).setOnClickListener(v -> {
            NotificationBottomSheet bottomSheet = new NotificationBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "notificationBottomSheet");
        });

        findViewById(R.id.devicesSetting).setOnClickListener(v -> {
            DevicesBottomSheet bottomSheet = new DevicesBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "devicesBottomSheet");
        });
    }

    public void updateUserName(String newName) {
        TextView userNameView = findViewById(R.id.userName);
        userNameView.setText(newName);
    }
} 