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
        switch (position) {
            case 0:
                return new GroupPeopleFragment(); // Nová stránka pre ľudí
            case 1:
                return new GroupOverviewFragment();
            default:
                return new GroupExpensesFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Teraz máme 3 stránky
    }
} 