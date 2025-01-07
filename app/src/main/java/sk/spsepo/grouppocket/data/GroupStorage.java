package sk.spsepo.grouppocket.data;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GroupStorage {
    private static final String FILE_NAME = "groups.json";

    public static void saveGroup(Context context, Group group) {
        try {
            // Načítaj existujúce skupiny
            List<Group> groups = loadGroups(context);
            groups.add(group);

            // Vytvor JSON array
            JSONArray jsonArray = new JSONArray();
            for (Group g : groups) {
                JSONObject groupJson = new JSONObject();
                groupJson.put("name", g.getName());

                // Pridaj členov
                JSONArray membersJson = new JSONArray();
                for (String member : g.getMembers()) {
                    membersJson.put(member);
                }
                groupJson.put("members", membersJson);

                // Pridaj výdavky
                JSONArray expensesJson = new JSONArray();
                for (Group.Expense expense : g.getExpenses()) {
                    JSONObject expenseJson = new JSONObject();
                    expenseJson.put("name", expense.getName());
                    expenseJson.put("amount", expense.getAmount());
                    expensesJson.put(expenseJson);
                }
                groupJson.put("expenses", expensesJson);

                jsonArray.put(groupJson);
            }

            // Ulož do súboru
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(jsonArray.toString().getBytes());
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Group> loadGroups(Context context) {
        List<Group> groups = new ArrayList<>();
        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            if (!file.exists()) {
                return groups;
            }

            FileInputStream fis = context.openFileInput(FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            JSONArray jsonArray = new JSONArray(content.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject groupJson = jsonArray.getJSONObject(i);
                
                // Načítaj členov
                List<String> members = new ArrayList<>();
                JSONArray membersJson = groupJson.getJSONArray("members");
                for (int j = 0; j < membersJson.length(); j++) {
                    members.add(membersJson.getString(j));
                }

                // Načítaj výdavky
                List<Group.Expense> expenses = new ArrayList<>();
                JSONArray expensesJson = groupJson.getJSONArray("expenses");
                for (int j = 0; j < expensesJson.length(); j++) {
                    JSONObject expenseJson = expensesJson.getJSONObject(j);
                    expenses.add(new Group.Expense(
                        expenseJson.getString("name"),
                        expenseJson.getDouble("amount")
                    ));
                }

                groups.add(new Group(
                    groupJson.getString("name"),
                    members,
                    expenses
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return groups;
    }

    public static void deleteGroup(Context context, String groupName) {
        try {
            List<Group> groups = loadGroups(context);
            groups.removeIf(group -> group.getName().equals(groupName));
            
            // Vytvor JSON array
            JSONArray jsonArray = new JSONArray();
            for (Group g : groups) {
                JSONObject groupJson = new JSONObject();
                groupJson.put("name", g.getName());

                JSONArray membersJson = new JSONArray();
                for (String member : g.getMembers()) {
                    membersJson.put(member);
                }
                groupJson.put("members", membersJson);

                JSONArray expensesJson = new JSONArray();
                for (Group.Expense expense : g.getExpenses()) {
                    JSONObject expenseJson = new JSONObject();
                    expenseJson.put("name", expense.getName());
                    expenseJson.put("amount", expense.getAmount());
                    expensesJson.put(expenseJson);
                }
                groupJson.put("expenses", expensesJson);

                jsonArray.put(groupJson);
            }

            // Ulož do súboru
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(jsonArray.toString().getBytes());
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 