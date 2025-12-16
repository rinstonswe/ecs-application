package com;

import java.sql.*;
import java.time.LocalDate;
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
    public void addEquipment(int id, String name, String requiredSkill) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Equipment (id, name, req_skill) VALUES (?, ?, ?)")) {
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, requiredSkill);
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

    // -------------------------- CHECKOUT / RETURN--------------------------
    public void checkoutEquipment(int equipmentId, int employeeId, LocalDate checkoutDate, LocalDate dueDate, String notes) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE Equipment SET is_checked_out=1, current_holder_id=? WHERE id=?")) {
            ps.setInt(1, employeeId);
            ps.setInt(2, equipmentId);
            ps.executeUpdate();
        }

        recordCheckout(equipmentId,employeeId,checkoutDate,dueDate,notes);

    }

    public void recordCheckout(int equipmentID, int employeeId, LocalDate checkoutDate, LocalDate dueDate, String notes) throws SQLException {
        String sql = """
                INSERT INTO checkouts (
                    equipment_id,
                    employee_id,
                    checkout_time,
                    due_time,
                    notes)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, equipmentID);
            ps.setInt(2, employeeId);
            ps.setString(3, checkoutDate.toString());
            ps.setString(4, dueDate.toString());
            ps.setString(5, notes);
            ps.executeUpdate();
        }
    }

    public void returnEquipment(int equipmentId, LocalDate returnDate) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE Equipment SET is_checked_out=0, current_holder_id=NULL WHERE id=?")) {
            ps.setInt(1, equipmentId);
            ps.executeUpdate();
        }

        recordReturn(equipmentId,returnDate);
    }

    public void recordReturn(int equipmentID, LocalDate returnDate) throws SQLException {
        String sql = """
            UPDATE checkouts
            SET return_time = ?
            WHERE equipment_id = ?
            AND return_time IS NULL;
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, returnDate.toString());
            ps.setInt(2, equipmentID);
            ps.executeUpdate();}
    }

    //------------------------- Record History Requests -------------------------
    public ResultSet getCheckoutHistoryForEmployee(int employeeId) throws SQLException {
        String sql = """
        SELECT *
        FROM checkouts
        WHERE employee_id = ?
        ORDER BY checkout_time DESC
    """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, employeeId);
        return ps.executeQuery();
    }

    public ResultSet getCheckoutHistoryForEquipment(int equipmentId) throws SQLException {
        String sql = """
        SELECT *
        FROM checkouts
        WHERE equipment_id = ?
        ORDER BY checkout_time DESC
    """;

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, equipmentId);
        return ps.executeQuery();
    }

    public List<Equipment> getEquipmentByEmployee(int employeeId) throws SQLException {
        List<Equipment> equipmentList = new ArrayList<>();

        String sql = """
        SELECT e.id, e.name, e.req_skill, e.is_checked_out
        FROM equipment e
        JOIN checkouts c ON e.id = c.equipment_id
        WHERE c.employee_id = ?
          AND c.return_time IS NULL
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Equipment eq = new Equipment(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("req_skill"),
                            rs.getBoolean("is_checked_out")
                    );
                    equipmentList.add(eq);
                }
            }
        }

        return equipmentList;
    }

    // ------------------------- Authentication -------------------------
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