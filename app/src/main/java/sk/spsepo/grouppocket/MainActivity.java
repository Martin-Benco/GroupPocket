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
import android.content.SharedPreferences;
import android.content.Context;
import sk.spsepo.grouppocket.data.AccountManager;

public class MainActivity extends AppCompatActivity {
    private LinearLayout groupsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Nastav email aktuálneho používateľa
        AccountManager.setCurrentUserEmail(getSharedPreferences("user_prefs", MODE_PRIVATE)
            .getString("user_email", ""));

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
        
        // Získaj výšku ScrollView kontajnera
        View scrollView = findViewById(R.id.scrollView);
        scrollView.post(() -> {
            int availableHeight = scrollView.getHeight();
            int gapHeight = (int)(16 * getResources().getDisplayMetrics().density);
            
            // Vypočítaj výšku jednej skupiny
            // Celková výška mínus 2 medzery, vydelené 3 skupinami
            int groupHeight = (availableHeight - (2 * gapHeight)) / 3;
            
            // Vytvor skupiny s vypočítanou výškou
            LayoutInflater inflater = LayoutInflater.from(this);
            for (int i = 0; i < groups.size(); i++) {
                Group group = groups.get(i);
                View groupView = inflater.inflate(R.layout.item_group, groupsContainer, false);
                
                // Nastav parametre layoutu pre skupinu
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    groupHeight
                );
                
                // Pridaj medzeru medzi skupinami
                if (i > 0) {
                    params.topMargin = gapHeight;
                }
                
                groupView.setLayoutParams(params);
                
                // Nastav obsah skupiny
                TextView groupName = groupView.findViewById(R.id.groupName);
                TextView memberCount = groupView.findViewById(R.id.memberCount);
                TextView totalAmount = groupView.findViewById(R.id.totalAmount);
                TextView paymentStatus = groupView.findViewById(R.id.paymentStatus);

                groupName.setText(group.getName());
                memberCount.setText(group.getMembers().size() + " members");

                double total = 0;
                for (Group.Expense expense : group.getExpenses()) {
                    total += expense.getAmount();
                }
                totalAmount.setText(String.format("%.0f€", total));

                // Skontroluj, či aktuálny používateľ zaplatil
                String currentUser = AccountManager.getCurrentUserEmail();
                boolean isPaid = group.getPaidMembers().contains(currentUser);
                
                // Nastav status platby podľa stavu platby
                paymentStatus.setText(isPaid ? "PAID" : "UNPAID");
                int statusColor = getResources().getColor(
                    isPaid ? android.R.color.holo_green_light : android.R.color.holo_red_light
                );
                paymentStatus.setTextColor(statusColor);

                // Vypočítaj percentuálny podiel platby
                View progressCircle = groupView.findViewById(R.id.progressCircle);
                if (isPaid && total > 0) {
                    // Vypočítaj sumu na člena
                    double amountPerMember = total / group.getMembers().size();
                    // Vypočítaj percento (amountPerMember z total)
                    int percentage = (int)((amountPerMember / total) * 100);
                    // Nastav úroveň kruhu (level je od 0 do 10000)
                    int level = (percentage * 100);
                    progressCircle.getBackground().setLevel(level);
                    progressCircle.setVisibility(View.VISIBLE);
                } else {
                    // Ak používateľ nezaplatil, skry kruh
                    progressCircle.setVisibility(View.GONE);
                }

                groupView.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, GroupActivity.class);
                    intent.putExtra("groupName", group.getName());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });

                groupsContainer.addView(groupView);
            }
        });
    }
}