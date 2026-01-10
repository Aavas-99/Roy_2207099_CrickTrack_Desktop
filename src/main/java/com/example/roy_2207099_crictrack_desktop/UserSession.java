package com.example.roy_2207099_crictrack_desktop;

public class UserSession {
    private static int userId;
    private static boolean isadmin;

    public static void setUserId(int id) { userId = id; }
    public static int getUserId() { return userId; }
    public static void setIsAdmin(boolean admin) { isadmin = admin; }
    public static boolean getIsAdmin() { return isadmin; }
}