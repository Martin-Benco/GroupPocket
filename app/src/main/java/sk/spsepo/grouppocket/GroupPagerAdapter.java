package sk.spsepo.grouppocket;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class GroupPagerAdapter extends FragmentStateAdapter {

    public GroupPagerAdapter(FragmentActivity activity) {
        super(activity);
    }

    @Override
    public Fragment createFragment(int position) {
        // Vráť príslušný fragment podľa pozície
        return position == 0 ? new GroupOverviewFragment() : new GroupExpensesFragment();
    }

    @Override
    public int getItemCount() {
        return 2; // Máme 2 stránky
    }
} 