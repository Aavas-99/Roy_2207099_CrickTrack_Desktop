package com.example.roy_2207099_crictrack_desktop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:cricktrack.db";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
            return conn;
        } catch (SQLException e) {
            System.out.println("Database Connection Failed!");
            e.printStackTrace();
            return null;
        }
    }
    public static void init() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Database connected successfully.");
                try (var stmt = conn.createStatement()) {
                    String sql = "CREATE TABLE IF NOT EXISTS users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "username TEXT NOT NULL UNIQUE," +
                            "password TEXT NOT NULL" +
                            ");";
                    stmt.execute(sql);
                    System.out.println("Users table ensured.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to initialize database.");
            e.printStackTrace();
        }
    }

    public static int createMatch(String teamA, String teamB, int overs, String stadium, String date, String tossWinner, String decision) {
        int matchId=0 ;
        try (Connection conn = getConnection()) {
            if (conn == null) return -1;
            try (var stmt = conn.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS matches (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "team_a TEXT, " +
                        "team_b TEXT, " +
                        "overs INTEGER, " +
                        "stadium TEXT, " +
                        "date TEXT, " +
                        "toss_winner TEXT, " +
                        "decision TEXT, " +
                        "first_innings_total INTEGER, " +
                        "result TEXT" +
                        ");";
                stmt.execute(sql);
            }

            String insert = "INSERT INTO matches (team_a, team_b, overs, stadium, date, toss_winner, decision) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, teamA);
                ps.setString(2, teamB);
                ps.setInt(3, overs);
                ps.setString(4, stadium);
                ps.setString(5, date);
                ps.setString(6, tossWinner);
                ps.setString(7, decision);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs != null && rs.next()) {
                        matchId = rs.getInt(1);
                    }
                }

                    try (Statement s2 = conn.createStatement(); ResultSet rs2 = s2.executeQuery("SELECT last_insert_rowid()")) {
                        if (rs2 != null && rs2.next()) {
                            matchId = rs2.getInt(1);
                        }
                    }
                    catch (SQLException ex) {
                        matchId=-1;
                        System.out.println("createMatch last_insert_rowid() failed: " + ex.getMessage());
                    }

                System.out.println(matchId);
                if (matchId == -1) {
                    String q = "SELECT id FROM matches WHERE team_a = ? AND team_b = ? AND date = ? ORDER BY id DESC LIMIT 1";
                    try (PreparedStatement psq = conn.prepareStatement(q)) {
                        psq.setString(1, teamA);
                        psq.setString(2, teamB);
                        psq.setString(3, date);
                        try (ResultSet rs3 = psq.executeQuery()) {
                            if (rs3 != null && rs3.next()) {
                                matchId = rs3.getInt(1);
                            }
                        }
                    } catch (SQLException ex) {
                        System.out.println("createMatch fallback query failed: " + ex.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to create match: " + e.getMessage());
        }

        return matchId;
    }

}
