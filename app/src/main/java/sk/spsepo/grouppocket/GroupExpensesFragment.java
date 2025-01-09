package sk.spsepo.grouppocket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import java.util.List;
import sk.spsepo.grouppocket.data.Group;
import sk.spsepo.grouppocket.data.GroupStorage;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.view.Gravity;
import android.view.animation.AnimationUtils;
import java.util.Arrays;
import java.util.ArrayList;

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
            adapter = new ExpensesAdapter(currentGroup);
            recyclerView.setAdapter(adapter);
        }
        
        return view;
    }
    
    private class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ExpenseViewHolder> {
        private Group group;
        
        public ExpensesAdapter(Group group) {
            this.group = group;
        }
        
        @Override
        public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
            return new ExpenseViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(ExpenseViewHolder holder, int position) {
            Group.Expense expense = group.getExpenses().get(position);
            holder.expenseName.setText(expense.getName());
            holder.expenseAmount.setText(String.format("%.2f€", expense.getAmount()));
            holder.contributorsCount.setText(group.getMembers().size() + " contributors");
            
            holder.itemView.setOnClickListener(v -> showContributorsDialog(expense));
        }
        
        @Override
        public int getItemCount() {
            return group.getExpenses().size();
        }
        
        private void showContributorsDialog(Group.Expense expense) {
            View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_expense_contributors, null);
            
            // Vytvor popup s plnou šírkou
            PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            );

            // Nastav pozadie a animáciu
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);

            // Pridaj vylepšenú animáciu pre zatvorenie
            popupWindow.setOnDismissListener(() -> {
                android.view.animation.Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
                anim.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(android.view.animation.Animation animation) {}

                    @Override
                    public void onAnimationEnd(android.view.animation.Animation animation) {
                        // Popup sa už zatvoril
                        popupWindow.dismiss();
                    }

                    @Override
                    public void onAnimationRepeat(android.view.animation.Animation animation) {}
                });
                popupView.startAnimation(anim);
            });

            // Nastav RecyclerView a adapter
            RecyclerView contributorsRecyclerView = popupView.findViewById(R.id.contributorsRecyclerView);
            contributorsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            
            // Získaj aktuálnu skupinu
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
                ContributorsAdapter contributorsAdapter = new ContributorsAdapter(currentGroup.getMembers(), expense);
                contributorsRecyclerView.setAdapter(contributorsAdapter);
            }

            // Zobraz popup zo spodnej časti obrazovky
            View rootView = getActivity().findViewById(R.id.mainLayout);
            popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

            // Pridaj vylepšenú animáciu pre otvorenie
            android.view.animation.Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
            popupView.startAnimation(anim);
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

    private class ContributorsAdapter extends RecyclerView.Adapter<ContributorsAdapter.ContributorViewHolder> {
        private List<String> members;
        private Group.Expense expense;
        private boolean[] selectedContributors;
        private Group currentGroup;

        ContributorsAdapter(List<String> members, Group.Expense expense) {
            this.members = members;
            this.expense = expense;
            this.selectedContributors = new boolean[members.size()];
            
            // Inicializuj selectedContributors podľa uložených dát
            if (expense.getContributors() == null || expense.getContributors().isEmpty()) {
                // Ak ešte nie sú contributors nastavení, označ všetkých
                Arrays.fill(selectedContributors, true);
                expense.setContributors(new ArrayList<>(members));
            } else {
                // Nastav checkboxy podľa uložených contributors
                for (int i = 0; i < members.size(); i++) {
                    selectedContributors[i] = expense.getContributors().contains(members.get(i));
                }
            }
        }

        @Override
        public ContributorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_contributor, parent, false);
            return new ContributorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ContributorViewHolder holder, int position) {
            String member = members.get(position);
            holder.memberName.setText(member);
            holder.checkBox.setChecked(selectedContributors[position]);

            View.OnClickListener clickListener = v -> {
                selectedContributors[position] = !selectedContributors[position];
                holder.checkBox.setChecked(selectedContributors[position]);
                
                // Aktualizuj zoznam contributors v expense
                List<String> updatedContributors = new ArrayList<>();
                for (int i = 0; i < members.size(); i++) {
                    if (selectedContributors[i]) {
                        updatedContributors.add(members.get(i));
                    }
                }
                expense.setContributors(updatedContributors);
                
                // Ulož zmeny do storage
                String groupName = getActivity().getIntent().getStringExtra("groupName");
                List<Group> groups = GroupStorage.loadGroups(getContext());
                
                for (Group group : groups) {
                    if (group.getName().equals(groupName)) {
                        // Nájdi a aktualizuj expense v skupine
                        for (Group.Expense e : group.getExpenses()) {
                            if (e.getName().equals(expense.getName()) && 
                                e.getAmount() == expense.getAmount()) {
                                e.setContributors(updatedContributors);
                                break;
                            }
                        }
                        break;
                    }
                }
                
                GroupStorage.saveGroups(getContext(), groups);
            };

            // Nastav click listener pre celý item aj checkbox
            holder.itemView.setOnClickListener(clickListener);
            holder.checkBox.setOnClickListener(clickListener);
        }

        @Override
        public int getItemCount() {
            return members.size();
        }

        class ContributorViewHolder extends RecyclerView.ViewHolder {
            TextView memberName;
            CheckBox checkBox;

            ContributorViewHolder(View itemView) {
                super(itemView);
                memberName = itemView.findViewById(R.id.contributorName);
                checkBox = itemView.findViewById(R.id.contributorCheckBox);
            }
        }
    }
} 