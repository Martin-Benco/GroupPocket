package sk.spsepo.grouppocket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import sk.spsepo.grouppocket.data.Group;
import sk.spsepo.grouppocket.data.GroupStorage;
import sk.spsepo.grouppocket.data.AccountManager;

public class GroupPeopleFragment extends Fragment {
    private MembersAdapter adapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_people, container, false);
        
        RecyclerView recyclerView = view.findViewById(R.id.membersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Získaj skupinu a jej členov
        String groupName = getActivity().getIntent().getStringExtra("groupName");
        List<Group> groups = GroupStorage.loadGroups(getContext());
        Group currentGroup = null;
        for (Group group : groups) {
            if (group.getName().equals(groupName)) {
                currentGroup = group;
                break;
            }
        }
        
        if (currentGroup != null) {
            adapter = new MembersAdapter(currentGroup.getMembers());
            recyclerView.setAdapter(adapter);
        }
        
        return view;
    }
    
    // Presunieme metódu refreshMembers() do hlavnej triedy fragmentu
    public void refreshMembers() {
        if (getView() != null) {
            // Znovu načítaj dáta
            String groupName = getActivity().getIntent().getStringExtra("groupName");
            List<Group> groups = GroupStorage.loadGroups(getContext());
            
            for (Group group : groups) {
                if (group.getName().equals(groupName)) {
                    // Vytvor nový adapter s aktuálnymi dátami a vynúť obnovenie
                    adapter = new MembersAdapter(group.getMembers());
                    RecyclerView recyclerView = getView().findViewById(R.id.membersRecyclerView);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }
    
    private class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberViewHolder> {
        private List<String> members;
        
        public MembersAdapter(List<String> members) {
            this.members = members;
        }
        
        @Override
        public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member, parent, false);
            return new MemberViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(MemberViewHolder holder, int position) {
            String member = members.get(position);
            holder.memberEmail.setText(member);
            
            // Získaj aktuálnu skupinu
            String groupName = getActivity().getIntent().getStringExtra("groupName");
            List<Group> groups = GroupStorage.loadGroups(getContext());
            Group currentGroup = null;
            String currentUser = AccountManager.getCurrentUserEmail();
            
            for (Group group : groups) {
                if (group.getName().equals(groupName)) {
                    currentGroup = group;
                    break;
                }
            }
            
            if (currentGroup != null) {
                // Vypočítaj a zobraz sumu pre člena
                calculateAndDisplayAmounts(currentGroup, holder, member);
                
                // Nastav status platby
                boolean isAdmin = position == 0;
                if (isAdmin) {
                    // Pre admina (prvý člen) zachovaj pôvodnú logiku
                    boolean isPaid = currentGroup.getPaidMembers().contains(currentUser);
                    holder.paymentStatus.setText(isPaid ? "PAID" : "UNPAID");
                    holder.paymentStatus.setTextColor(getResources().getColor(
                        isPaid ? android.R.color.holo_green_light : android.R.color.holo_red_light
                    ));
                } else {
                    // Pre ostatných členov nastav UNPAID
                    holder.paymentStatus.setVisibility(View.VISIBLE);
                    holder.paymentStatus.setText("UNPAID");
                    holder.paymentStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                }
            }
        }
        
        @Override
        public int getItemCount() {
            return members.size();
        }
        
        class MemberViewHolder extends RecyclerView.ViewHolder {
            TextView memberEmail, memberRole, memberAmount, paymentStatus;
            
            MemberViewHolder(View itemView) {
                super(itemView);
                memberEmail = itemView.findViewById(R.id.memberEmail);
                memberRole = itemView.findViewById(R.id.memberRole);
                memberAmount = itemView.findViewById(R.id.memberAmount);
                paymentStatus = itemView.findViewById(R.id.paymentStatus);
            }
        }
        
        private void calculateAndDisplayAmounts(Group currentGroup, MemberViewHolder holder, String memberEmail) {
            double totalAmountForMember = 0;
            
            // Prejdi všetky výdavky
            for (Group.Expense expense : currentGroup.getExpenses()) {
                List<String> contributors = expense.getContributors();
                
                // Ak nie sú nastavení contributors, považuj všetkých za contributors
                if (contributors == null || contributors.isEmpty()) {
                    totalAmountForMember += expense.getAmount() / currentGroup.getMembers().size();
                } else {
                    // Ak je členom contributors, pripočítaj jeho podiel
                    if (contributors.contains(memberEmail)) {
                        totalAmountForMember += expense.getAmount() / contributors.size();
                    }
                }
            }
            
            holder.memberAmount.setText(String.format("%.2f€", totalAmountForMember));
        }
    }
} 