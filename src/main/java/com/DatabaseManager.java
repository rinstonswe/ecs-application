package com;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
This is a central class to create and manage the database connection
as well as all database queriesq
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:ecs.db";
    private Connection conn;

    public DatabaseManager() throws SQLException {
        conn = DriverManager.getConnection(DB_URL);
        initSchema();
    }

    private void initSchema() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Employees (
                    employee_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    skills TEXT
                )
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Equipment (
                    equipment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    required_skill TEXT,
                    is_checked_out INTEGER DEFAULT 0,
                    current_holder_id INTEGER,
                    FOREIGN KEY (current_holder_id) REFERENCES Employees(employee_id)
                )
            """);
        }
    }

    // --- Employee Queries ---
    public void addEmployee(String name, String skills) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Employees (name, skills) VALUES (?, ?)")) {
            ps.setString(1, name);
            ps.setString(2, skills);
            ps.executeUpdate();
        }
    }

    public ResultSet listEmployees() throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("SELECT * FROM Employees");
    }

    // --- Equipment Queries ---
    public void addEquipment(String name, String requiredSkill) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Equipment (name, required_skill) VALUES (?, ?)")) {
            ps.setString(1, name);
            ps.setString(2, requiredSkill);
            ps.executeUpdate();
        }
    }

    public ResultSet listEquipment() throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("SELECT * FROM Equipment");
    }

    public void checkoutEquipment(int equipmentId, int employeeId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE Equipment SET is_checked_out=1, current_holder_id=? WHERE equipment_id=?")) {
            ps.setInt(1, employeeId);
            ps.setInt(2, equipmentId);
            ps.executeUpdate();
        }
    }

    public void returnEquipment(int equipmentId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE Equipment SET is_checked_out=0, current_holder_id=NULL WHERE equipment_id=?")) {
            ps.setInt(1, equipmentId);
            ps.executeUpdate();
        }
    }

    public String idSearch(int equipmentId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM Equipment WHERE id = ?")) {
            ps.setInt(1, equipmentId);
            ResultSet rs = ps.executeQuery();
            return String.format(("ID: %d | Name: %s | Requires: %s"), rs.getInt("id"), rs.getString("name"), rs.getString("req_skill"));

        }
    }

    public String skillSearch (String skills) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM equipment WHERE req_skill LIKE ?")) {
            ps.setString(1, skills);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
           String equips = "";
            while (rs.next()) {
                equips =  String.format(("ID: %d | Name: %s | Requires: %s\n"), rs.getInt("id"), rs.getString("name"), rs.getString("req_skill"));
            }

            return equips;
        }
    }

    public boolean auth(String username, String password) throws SQLException {
        return true;
    }

    public boolean isSuper(String username) throws SQLException {
        return true;
    }
}