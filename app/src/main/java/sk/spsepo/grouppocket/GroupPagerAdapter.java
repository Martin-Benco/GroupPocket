package sk.spsepo.grouppocket;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class GroupPagerAdapter extends FragmentStateAdapter {
    private GroupPeopleFragment peopleFrag;
    private GroupExpensesFragment expensesFrag;
    private GroupChatFragment chatFrag;

    public GroupPagerAdapter(FragmentActivity activity) {
        super(activity);
        peopleFrag = new GroupPeopleFragment();
        expensesFrag = new GroupExpensesFragment();
        chatFrag = new GroupChatFragment();
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return peopleFrag;
            case 1:
                return expensesFrag;
            case 2:
                return chatFrag;
            default:
                return peopleFrag;
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