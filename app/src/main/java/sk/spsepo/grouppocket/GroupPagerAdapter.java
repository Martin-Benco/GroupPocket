package sk.spsepo.grouppocket;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class GroupPagerAdapter extends FragmentStateAdapter {
    public GroupPeopleFragment peopleFrag;
    
    public GroupPagerAdapter(FragmentActivity fa) {
        super(fa);
        peopleFrag = new GroupPeopleFragment();
    }
    
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return peopleFrag;
            case 1:
                return new GroupOverviewFragment();
            case 2:
                return new GroupExpensesFragment();
            default:
                return null;
        }
    }
    
    @Override
    public int getItemCount() {
        return 3;
    }
    
    public GroupPeopleFragment getPeopleFrag() {
        return peopleFrag;
    }
} 