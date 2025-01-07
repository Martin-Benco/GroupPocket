package sk.spsepo.grouppocket;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import java.util.List;
import java.util.ArrayList;

import sk.spsepo.grouppocket.data.Group;
import sk.spsepo.grouppocket.data.GroupStorage;

public class AddTransactionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        
        // Make status bar #141414 with white icons
        getWindow().setStatusBarColor(Color.parseColor("#141414"));

        // Make navigation bar black with white icons
        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.black));

        // Add click listener for close button
        findViewById(R.id.closeButton).setOnClickListener(v -> {
            finish();
        });

        // Setup input fields
        setupInputField(R.id.groupNameInput);
        setupEmailInput();

        // Setup root view touch listener to hide keyboard
        View rootView = findViewById(android.R.id.content);
        rootView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View currentFocus = getCurrentFocus();
                if (currentFocus instanceof EditText) {
                    currentFocus.clearFocus();
                    hideKeyboard(currentFocus);
                }
            }
            return false;
        });

        // Pridaj listener pre create button
        findViewById(R.id.bottomButton).setOnClickListener(v -> createGroup());
    }

    private void setupInputField(int id) {
        EditText input = findViewById(id);
        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                input.clearFocus();
                hideKeyboard(input);
                return true;
            }
            return false;
        });
    }

    private void setupEmailInput() {
        EditText emailInput = findViewById(R.id.membersInput);
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0 && s.charAt(start + count - 1) == ',') {
                    String text = s.toString();
                    String email = text.substring(0, text.length() - 1).trim();
                    
                    if (!email.isEmpty()) {
                        SpannableStringBuilder builder = new SpannableStringBuilder();
                        builder.append(email);
                        
                        // Add text color only
                        builder.setSpan(
                            new ForegroundColorSpan(Color.parseColor("#ff914d")),
                            0,
                            email.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        );
                        
                        builder.append(" ");
                        
                        emailInput.setText(builder);
                        emailInput.setSelection(builder.length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup expense input fields
        setupExpenseInputs();
    }

    private void setupExpenseInputs() {
        for (int i = 1; i <= 4; i++) {
            int nameId = getResources().getIdentifier("expenseName" + i, "id", getPackageName());
            int amountId = getResources().getIdentifier("expenseAmount" + i, "id", getPackageName());
            
            EditText nameInput = findViewById(nameId);
            EditText amountInput = findViewById(amountId);
            
            setupInputField(nameId);
            setupInputField(amountId);
        }
    }

    private void hideKeyboard(View view) {
        android.view.inputmethod.InputMethodManager imm = 
            (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void createGroup() {
        // Získaj názov skupiny
        EditText groupNameInput = findViewById(R.id.groupNameInput);
        String groupName = groupNameInput.getText().toString().trim();
        if (groupName.isEmpty()) {
            groupNameInput.setError("Please enter group name");
            return;
        }

        // Získaj členov
        EditText membersInput = findViewById(R.id.membersInput);
        String[] memberEmails = membersInput.getText().toString().split(" ");
        List<String> members = new ArrayList<>();
        for (String email : memberEmails) {
            if (!email.isEmpty()) {
                members.add(email);
            }
        }
        if (members.isEmpty()) {
            membersInput.setError("Please add at least one member");
            return;
        }

        // Získaj výdavky
        List<Group.Expense> expenses = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            EditText nameInput = findViewById(getResources().getIdentifier("expenseName" + i, "id", getPackageName()));
            EditText amountInput = findViewById(getResources().getIdentifier("expenseAmount" + i, "id", getPackageName()));
            
            String name = nameInput.getText().toString().trim();
            String amountStr = amountInput.getText().toString().trim();
            
            if (!name.isEmpty() && !amountStr.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    expenses.add(new Group.Expense(name, amount));
                } catch (NumberFormatException e) {
                    amountInput.setError("Invalid amount");
                    return;
                }
            }
        }

        // Vytvor a ulož skupinu
        Group group = new Group(groupName, members, expenses);
        GroupStorage.saveGroup(this, group);

        // Zatvor aktivitu
        finish();
    }
} 