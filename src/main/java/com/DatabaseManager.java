package com;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.sql.*;

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

    //Initialize the schema
    private void initSchema() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Employees (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    skills TEXT,
                    supervisor BOOLEAN,
                    pass_hash TEXT NOT NULL DEFAULT please_change_me);
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS equipment (
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

    //------------------------- Employee Queries -------------------------
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
        return stmt.executeQuery("""
            SELECT id, name, skills,
            CASE 
                WHEN supervisor = 1 THEN 'Supervisor'
                ELSE 'Standard'
            END AS role
        FROM Employees
    """);
    }



    //------------------------- Equipment Queries -------------------------
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

//------------------------- Search Helpers -------------------------
    public String idSearch(int equipmentId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM Equipment WHERE id = ?")) {
            ps.setInt(1, equipmentId);
            ResultSet rs = ps.executeQuery();
            return String.format(("ID: %d | Name: %s | Requires: %s"),
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("req_skill"));

        }
    }

    public String skillSearch(String skill) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM Equipment WHERE required_skill LIKE ?")) {

            ps.setString(1, "%" + skill + "%");
            ResultSet rs = ps.executeQuery();

            StringBuilder out = new StringBuilder();
            while (rs.next()) {
                out.append(String.format("ID: %d | Name: %s | Requires: %s%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("required_skill")));
            }

            return out.toString();
        }
    }

    public String nameSearch(String name) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM Equipment WHERE name LIKE ?")) {

            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();

            StringBuilder out = new StringBuilder();
            while (rs.next()) {
                out.append(String.format("ID: %d | Name: %s | Requires: %s%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("required_skill")));
            }

            return out.toString();
        }
    }

    //------------------------- Authentication -------------------------
    public boolean auth(int id, String password) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT pass_hash FROM Employees WHERE id = ?")) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return false;

            String storedHash = rs.getString("pass_hash");
            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

            return argon2.verify(storedHash, password);
        }
    }

    public boolean isSuper(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT supervisor FROM Employees WHERE id = ?")) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            return rs.next() && rs.getInt("supervisor") == 1;
        }
    }
}

