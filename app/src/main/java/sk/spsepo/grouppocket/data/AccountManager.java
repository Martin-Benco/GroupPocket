package sk.spsepo.grouppocket.data;

public class AccountManager {
    private static String currentUserEmail;

    public static void setCurrentUserEmail(String email) {
        currentUserEmail = email;
    }

    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }
} 