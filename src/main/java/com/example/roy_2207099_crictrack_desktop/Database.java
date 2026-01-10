package com.example.roy_2207099_crictrack_desktop;

import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:cricktrack.db";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void init() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL UNIQUE, password TEXT NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS matches (id INTEGER PRIMARY KEY AUTOINCREMENT, team_a TEXT, team_b TEXT, overs INTEGER, stadium TEXT, date TEXT, toss_winner TEXT, decision TEXT, first_innings_total INTEGER, result TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS match_approvals (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "match_id INTEGER NOT NULL, " +
                    "approved INTEGER DEFAULT 0, " +
                    "UNIQUE (user_id, match_id))");
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static int createMatch(String teamA, String teamB, int overs, String stadium, String date, String tossWinner, String decision) {
        int matchId = -1;
        String sql = "INSERT INTO matches (team_a, team_b, overs, stadium, date, toss_winner, decision) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, teamA);
            ps.setString(2, teamB);
            ps.setInt(3, overs);
            ps.setString(4, stadium);
            ps.setString(5, date);
            ps.setString(6, tossWinner);
            ps.setString(7, decision);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) matchId = rs.getInt(1);
            }

            if (matchId != -1) {
                String initApprovals = "INSERT INTO match_approvals (user_id, match_id, approved) SELECT id, ?, 0 FROM users";
                try (PreparedStatement ps2 = conn.prepareStatement(initApprovals)) {
                    ps2.setInt(1, matchId);
                    ps2.executeUpdate();
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return matchId;
    }
}