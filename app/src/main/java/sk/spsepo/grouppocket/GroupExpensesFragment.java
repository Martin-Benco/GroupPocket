package sk.spsepo.grouppocket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.List;
import sk.spsepo.grouppocket.data.Group;
import sk.spsepo.grouppocket.data.GroupStorage;

public class GroupExpensesFragment extends Fragment {
    private ExpensesAdapter adapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_expenses, container, false);
        
        RecyclerView recyclerView = view.findViewById(R.id.expensesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Získaj skupinu a jej výdavky
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
            adapter = new ExpensesAdapter(currentGroup.getExpenses(), currentGroup.getMembers());
            recyclerView.setAdapter(adapter);
        }
        
        return view;
    }
    
    private class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ExpenseViewHolder> {
        private List<Group.Expense> expenses;
        private List<String> members;
        
        public ExpensesAdapter(List<Group.Expense> expenses, List<String> members) {
            this.expenses = expenses;
            this.members = members;
        }
        
        @Override
        public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
            return new ExpenseViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(ExpenseViewHolder holder, int position) {
            Group.Expense expense = expenses.get(position);
            holder.expenseName.setText(expense.getName());
            holder.expenseAmount.setText(String.format("%.2f€", expense.getAmount()));
            holder.contributorsCount.setText(members.size() + " contributors");
            
            // Nastav click listener pre zobrazenie prispievateľov
            holder.itemView.setOnClickListener(v -> showContributorsDialog(expense));
        }
        
        @Override
        public int getItemCount() {
            return expenses.size();
        }
        
        private void showContributorsDialog(Group.Expense expense) {
            BottomSheetDialog dialog = new BottomSheetDialog(getContext());
            View dialogView = getLayoutInflater().inflate(R.layout.popup_expense_contributors, null);
            
            RecyclerView contributorsRecyclerView = dialogView.findViewById(R.id.contributorsRecyclerView);
            contributorsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            
            // TODO: Implementovať ContributorsAdapter s checkboxami pre každého člena
            
            dialog.setContentView(dialogView);
            dialog.show();
        }
        
        class ExpenseViewHolder extends RecyclerView.ViewHolder {
            TextView expenseName, contributorsCount, expenseAmount;
            
            ExpenseViewHolder(View itemView) {
                super(itemView);
                expenseName = itemView.findViewById(R.id.expenseName);
                contributorsCount = itemView.findViewById(R.id.contributorsCount);
                expenseAmount = itemView.findViewById(R.id.expenseAmount);
            }
        }
    }
} 