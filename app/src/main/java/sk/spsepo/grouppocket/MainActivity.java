package sk.spsepo.grouppocket;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import sk.spsepo.grouppocket.data.Group;
import sk.spsepo.grouppocket.data.GroupStorage;

public class MainActivity extends AppCompatActivity {
    private LinearLayout groupsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Make status bar #141414 with white icons
        getWindow().setStatusBarColor(Color.parseColor("#141414"));

        // Make navigation bar black with white icons
        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.black));

        groupsContainer = findViewById(R.id.groupsContainer);

        // Add click listeners for navigation buttons
        findViewById(R.id.accountButton).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        findViewById(R.id.plusButton).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGroups();
    }

    private void loadGroups() {
        groupsContainer.removeAllViews();
        List<Group> groups = GroupStorage.loadGroups(this);
        
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Group group : groups) {
            View groupView = inflater.inflate(R.layout.item_group, groupsContainer, false);
            
            TextView groupName = groupView.findViewById(R.id.groupName);
            TextView memberCount = groupView.findViewById(R.id.memberCount);
            View deleteButton = groupView.findViewById(R.id.deleteButton);

            groupName.setText(group.getName());
            memberCount.setText(group.getMembers().size() + " members");

            deleteButton.setOnClickListener(v -> {
                GroupStorage.deleteGroup(this, group.getName());
                loadGroups(); // Refresh the list
            });

            groupsContainer.addView(groupView);
        }
    }
}