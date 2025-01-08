package sk.spsepo.grouppocket;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.List;
import sk.spsepo.grouppocket.data.Group;
import sk.spsepo.grouppocket.data.GroupStorage;
import sk.spsepo.grouppocket.data.AccountManager;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class GroupActivity extends AppCompatActivity {
    
    private void showSettingsPopup() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_group_settings, null);
        
        // Vytvor popup s plnou šírkou a výškou
        PopupWindow popupWindow = new PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        );

        // Nastav pozadie a animáciu
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        
        // Nastav systémové UI flags aby popup prekryl navigačné tlačidlá
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        
        // Nastav čiernu farbu pre navigačné tlačidlá
        getWindow().setNavigationBarColor(Color.BLACK);

        // Nastav click listener pre leave group
        View leaveGroup = popupView.findViewById(R.id.leaveGroupButton);
        leaveGroup.setOnClickListener(v -> {
            // Odstráň skupinu zo súboru
            String groupName = getIntent().getStringExtra("groupName");
            List<Group> groups = GroupStorage.loadGroups(this);
            groups.removeIf(group -> group.getName().equals(groupName));
            GroupStorage.saveGroups(this, groups);
            
            // Zatvor popup a aktivitu
            popupWindow.dismiss();
            finish();
            overridePendingTransition(0, R.anim.slide_out_right);
        });

        // Pridaj listener pre zatvorenie popupu
        popupWindow.setOnDismissListener(() -> {
            // Obnov pôvodné systémové UI flags a farby
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            );
            getWindow().setNavigationBarColor(Color.BLACK);
            getWindow().setStatusBarColor(Color.parseColor("#141414"));
        });

        // Uprav onClick pre settings button
        View settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            // Zobraz popup od spodku obrazovky
            popupWindow.showAtLocation(settingsButton, android.view.Gravity.BOTTOM, 0, 0);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        // Nastav čiernu farbu pre systémové tlačidlá
        getWindow().setNavigationBarColor(Color.BLACK);
        getWindow().setStatusBarColor(Color.parseColor("#141414"));

        // Inicializuj ViewPager a TabLayout
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        GroupPagerAdapter pagerAdapter = new GroupPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        
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

        // Nastav pay button a jeho stav
        View payButton = findViewById(R.id.payButton);
        TextView payText = payButton.findViewById(R.id.payText);
        
        // Získaj skupiny a skontroluj stav platby
        String groupName = getIntent().getStringExtra("groupName");
        String currentUser = AccountManager.getCurrentUserEmail();
        List<Group> groups = GroupStorage.loadGroups(this);
        
        // Skontroluj, či používateľ už zaplatil
        for (Group group : groups) {
            if (group.getName().equals(groupName)) {
                if (group.getPaidMembers().contains(currentUser)) {
                    // Ak už zaplatil, nastav PAID vzhľad
                    payButton.setBackgroundTintList(null);
                    payButton.setBackgroundResource(R.drawable.paid_button_background);
                    payText.setTextColor(Color.parseColor("#ff914d"));
                    payText.setText("PAID");
                }
                break;
            }
        }

        payButton.setOnClickListener(v -> {
            for (Group group : groups) {
                if (group.getName().equals(groupName)) {
                    if (!group.getPaidMembers().contains(currentUser)) {
                        group.getPaidMembers().add(currentUser);
                        GroupStorage.saveGroups(this, groups);
                        
                        // Aktualizuj UI
                        payButton.setBackgroundTintList(null);
                        payButton.setBackgroundResource(R.drawable.paid_button_background);
                        payText.setTextColor(Color.parseColor("#ff914d"));
                        payText.setText("PAID");
                        
                        // Obnov fragment s členmi
                        GroupPagerAdapter adapter = (GroupPagerAdapter) viewPager.getAdapter();
                        if (adapter != null) {
                            GroupPeopleFragment fragment = adapter.getPeopleFrag();
                            if (fragment != null) {
                                fragment.refreshMembers();
                            }
                        }
                    }
                    break;
                }
            }
        });

        // Nastav settings button
        findViewById(R.id.settingsButton).setOnClickListener(v -> showSettingsPopup());
        
        // Nastav close button
        findViewById(R.id.closeButton).setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, R.anim.slide_out_right);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_right);
    }
} 