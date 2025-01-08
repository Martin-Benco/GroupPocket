package sk.spsepo.grouppocket;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class GroupActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        // Nastav ViewPager2
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new GroupPagerAdapter(this));

        // Nastav TabLayout
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager,
            (tab, position) -> tab.setText(position == 0 ? "Overview" : "Expenses")
        ).attach();
    }
} 