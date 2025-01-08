package sk.spsepo.grouppocket;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class GroupActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        // Nastav čiernu farbu pre systémové tlačidlá
        getWindow().setNavigationBarColor(Color.BLACK);
        getWindow().setStatusBarColor(Color.parseColor("#141414"));

        // Nastav close button
        View closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, R.anim.slide_out_right);
        });

        // Nastav pay button
        View payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener(v -> {
            Toast.makeText(this, "Payment functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new GroupPagerAdapter(this));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setIcon(R.drawable.people);
                    break;
                case 1:
                    tab.setIcon(R.drawable.overview);
                    break;
                case 2:
                    tab.setIcon(R.drawable.chat);
                    break;
            }
        }).attach();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }
} 