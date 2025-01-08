package sk.spsepo.grouppocket.data;

import java.util.List;
import java.util.ArrayList;

public class Group {
    private String name;
    private List<String> members;
    private List<Expense> expenses;
    private List<String> paidMembers;

    public Group(String name, List<String> members, List<Expense> expenses) {
        this.name = name;
        this.members = members;
        this.expenses = expenses;
        this.paidMembers = new ArrayList<>();
    }

    // Gettery a settery
    public String getName() { return name; }
    public List<String> getMembers() { return members; }
    public List<Expense> getExpenses() { return expenses; }
    public List<String> getPaidMembers() {
        if (paidMembers == null) paidMembers = new ArrayList<>();
        return paidMembers;
    }

    public static class Expense {
        private String name;
        private double amount;

        public Expense(String name, double amount) {
            this.name = name;
            this.amount = amount;
        }

        public String getName() { return name; }
        public double getAmount() { return amount; }
    }
} 