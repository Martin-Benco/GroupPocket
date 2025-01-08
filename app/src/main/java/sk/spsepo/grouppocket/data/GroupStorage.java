package sk.spsepo.grouppocket.data;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GroupStorage {
    private static final String FILENAME = "groups.json";
    private static final Gson gson = new Gson();

    public static void saveGroup(Context context, Group group) {
        List<Group> groups = loadGroups(context);
        groups.add(group);
        saveGroups(context, groups);
    }

    public static void saveGroups(Context context, List<Group> groups) {
        File file = new File(context.getFilesDir(), FILENAME);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(groups, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Group> loadGroups(Context context) {
        File file = new File(context.getFilesDir(), FILENAME);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<List<Group>>(){}.getType();
            List<Group> groups = gson.fromJson(reader, type);
            return groups != null ? groups : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
} 