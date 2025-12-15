package com;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.security.PasswordHasher;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:ecs.db";
    private final Connection conn;

    public DatabaseManager() throws SQLException {
        conn = DriverManager.getConnection(DB_URL);
        initSchema();
    }

    private void initSchema() throws SQLException {
        try (Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Employees (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    skills TEXT,
                    supervisor BOOLEAN,
                    pass_hash TEXT NOT NULL DEFAULT 'please_change_me'
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS equipment (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    req_skill TEXT,
                    is_checked_out INTEGER DEFAULT 0,
                    current_holder_id INTEGER,
                    FOREIGN KEY (current_holder_id) REFERENCES Employees(id)
                );
            """);
        }
    }

    // ---------------------------------------------------------
    // EMPLOYEE INSERT
    // ---------------------------------------------------------
    public void addEmployee(String name, String skills) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Employees (name, skills) VALUES (?, ?)")) {
            ps.setString(1, name);
            ps.setString(2, skills);
            ps.executeUpdate();
        }
    }

    // ---------------------------------------------------------
    // EMPLOYEE GETTERS
    // ---------------------------------------------------------
    public Employee getEmployeeById(int id) throws SQLException {
        String sql = """
            SELECT id, name, skills, supervisor, pass_hash
            FROM Employees
            WHERE id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            return mapEmployee(rs);
        }
    }

    public List<Employee> getEmployeesByName(String name) throws SQLException {
        String sql = """
            SELECT id, name, skills, supervisor, pass_hash
            FROM Employees
            WHERE name LIKE ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();

            List<Employee> list = new ArrayList<>();
            while (rs.next()) list.add(mapEmployee(rs));
            return list;
        }
    }

    public List<Employee> getEmployeesBySkill(String skill) throws SQLException {
        String sql = """
            SELECT id, name, skills, supervisor, pass_hash
            FROM Employees
            WHERE skills LIKE ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + skill + "%");
            ResultSet rs = ps.executeQuery();

            List<Employee> list = new ArrayList<>();
            while (rs.next()) list.add(mapEmployee(rs));
            return list;
        }
    }

    private Employee mapEmployee(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");

        String skillString = rs.getString("skills");
        Set<String> skills = Set.of(skillString.split("\\s*,\\s*"));

        boolean supervisor = rs.getInt("supervisor") == 1;
        String passHash = rs.getString("pass_hash");

        return new Employee(id, name, skills, supervisor, passHash);
    }

    // ---------------------------------------------------------
    // EQUIPMENT INSERT
    // ---------------------------------------------------------
    public void addEquipment(String name, String requiredSkill) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Equipment (name, req_skill) VALUES (?, ?)")) {
            ps.setString(1, name);
            ps.setString(2, requiredSkill);
            ps.executeUpdate();
        }
    }

    // ---------------------------------------------------------
    // EQUIPMENT GETTERS
    // ---------------------------------------------------------
    public Equipment getEquipmentById(int id) throws SQLException {
        String sql = """
            SELECT id, name, req_skill, is_checked_out
            FROM equipment
            WHERE id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            return mapEquipment(rs);
        }
    }

    public List<Equipment> getEquipmentByName(String name) throws SQLException {
        String sql = """
            SELECT id, name, req_skill, is_checked_out
            FROM equipment
            WHERE name LIKE ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();

            List<Equipment> list = new ArrayList<>();
            while (rs.next()) list.add(mapEquipment(rs));
            return list;
        }
    }

    public List<Equipment> getEquipmentBySkill(String skill) throws SQLException {
        String sql = """
            SELECT id, name, req_skill, is_checked_out
            FROM equipment
            WHERE req_skill LIKE ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + skill + "%");
            ResultSet rs = ps.executeQuery();

            List<Equipment> list = new ArrayList<>();
            while (rs.next()) list.add(mapEquipment(rs));
            return list;
        }
    }

    private Equipment mapEquipment(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String reqSkill = rs.getString("req_skill");
        boolean isCheckedOut = rs.getInt("is_checked_out") == 1;

        return new Equipment(id, name, reqSkill, isCheckedOut);
    }

    // ---------------------------------------------------------
    // CHECKOUT / RETURN
    // ---------------------------------------------------------
    public void checkoutEquipment(int equipmentId, int employeeId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE Equipment SET is_checked_out=1, current_holder_id=? WHERE id=?")) {
            ps.setInt(1, employeeId);
            ps.setInt(2, equipmentId);
            ps.executeUpdate();
        }
    }

    public void returnEquipment(int equipmentId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE Equipment SET is_checked_out=0, current_holder_id=NULL WHERE id=?")) {
            ps.setInt(1, equipmentId);
            ps.executeUpdate();
        }
    }

    // ---------------------------------------------------------
    // AUTH
    // ---------------------------------------------------------
    public boolean auth(int id, String password) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT pass_hash FROM Employees WHERE id = ?")) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return false;

            return PasswordHasher.verify(password, rs.getString("pass_hash"));
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