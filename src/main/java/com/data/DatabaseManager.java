package com.data;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.security.PasswordHasher;

/**
 * Central database access layer for the ECS system.
 *
 * Responsibilities:
 *  - Establish a SQLite connection
 *  - Initialize schema on first run
 *  - Provide CRUD operations for Employees and Equipment
 */

public class DatabaseManager {

    /** SQLite connection string for the local ECS database. */
    private static final String DB_URL = "jdbc:sqlite:ecs.db";

    /**Single shared connection for db instance*/
    private final Connection conn;

    /**
     * Creates new DatabaseManager object and initializes schema */
    public DatabaseManager() throws SQLException {
        conn = DriverManager.getConnection(DB_URL);
        initSchema();
    }
    /** Creates required tables */
    private void initSchema() throws SQLException {
        try (Statement stmt = conn.createStatement()) {

            // Employee Table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS employees (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    skills TEXT,
                    supervisor BOOLEAN,
                    pass_hash TEXT NOT NULL DEFAULT 'please_change_me'
                );
            """);

            // Equipment Table
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

            //Checkout history table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS checkouts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    equipment_id INTEGER REFERENCES equipment(id),
                    employee_id INTEGER REFERENCES employees(id),
                    checkout_time DATETIME,
                    due_time DATETIME,
                    return_time DATETIME NULL,
                    notes TEXT
                );
            """);
        }
    }

    // Insert Employee into employees table
    // This is included but not used, in the given use case for this application the employee db would be managed by a separate system
    public void addEmployee(String name, String skills) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Employees (name, skills) VALUES (?, ?)")) {
            ps.setString(1, name);
            ps.setString(2, skills);
            ps.executeUpdate();
        }
    }
    // ********************************************************************
    // *------------------------ Employee Getters ------------------------*
    // ********************************************************************

    /**
     * Retrieves a single employee record by employee ID
     *
     * @param id
     * @return Employee Object
     * @throws SQLException
     */
    public Employee getEmployeeById(int id) throws SQLException {
        String sql = """
            SELECT *
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

    /**
     * Retreives all employees whose names contain a given sub-string
     *
     * @param name
     * @return List of employee objects
     * @throws SQLException
     */
    public List<Employee> getEmployeesByName(String name) throws SQLException {
        String sql = """
            SELECT id, name, skills, supervisor
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

    /**
     * Retrieves all employees who have a skill matching a given string
     *
     * @param skill
     * @return List of employee objects
     * @throws SQLException
     */
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

    /**
     * Maps an SQLite ResultSet row to an Employee object
     *
     * @param rs
     * @return Employee Object
     * @throws SQLException
     */
    private Employee mapEmployee(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");

        String skillString = rs.getString("skills");
        Set<String> skills = Set.of(skillString.split("\\s*,\\s*"));

        boolean supervisor = rs.getInt("supervisor") == 1;
        String passHash;
        try {
            passHash = rs.getString("pass_hash");
        } catch (SQLException ex) {
            passHash = null; // or ""
        }

        return new Employee(id, name, skills, supervisor, passHash);
    }

    // ********************************************************************
    // *------------------------ EQUIPMENT INSERT ------------------------*
    // ********************************************************************

    public void addEquipment(String name, String requiredSkill) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Equipment (name, req_skill) VALUES (?, ?, ?)")) {
            ps.setString(1, name);
            ps.setString(2, requiredSkill);
            ps.executeUpdate();
        }
    }

    // *********************************************************************
    // *------------------------ EQUIPMENT GETTERS ------------------------*
    // *********************************************************************

    /**
     * Retrieves a single equipment item by ID.
     *
     * @param id
     * @return Equipment object or null if not found.
     * @throws SQLException
     */
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

    /**
     * Retrieves all equipment whose names contain the given substring.
     *
     * @param name
     * @return List of equipment objects
     * @throws SQLException
     */
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

    /**
     * Retrieves equipment that requires a skill matching the given substring.
     *
     * @param skill
     * @return List of equipment objects
     * @throws SQLException
     */
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

    /**
     * Maps an SQLite ResultSet row to an Equipment object.
     *
     * @param rs
     * @return Equipment object
     * @throws SQLException
     */
    private Equipment mapEquipment(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String reqSkill = rs.getString("req_skill");
        boolean isCheckedOut = rs.getInt("is_checked_out") == 1;

        return new Equipment(id, name, reqSkill, isCheckedOut);
    }

    // ************************************************************************
    // *-------------------------- CHECKOUT / RETURN--------------------------*
    // ************************************************************************

    /**
     * Marks equipment as checked out and records the checkout event.
     * Two-step operation
     *  1. Update equipment status
     *  2. Insert checkout history record
     *
     * @param equipmentId
     * @param employeeId
     * @param checkoutDate
     * @param dueDate
     * @param notes
     * @throws SQLException
     */
    public void checkoutEquipment(int equipmentId, int employeeId, LocalDate checkoutDate, LocalDate dueDate, String notes) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE Equipment SET is_checked_out=1, current_holder_id=? WHERE id=?")) {
            ps.setInt(1, employeeId);
            ps.setInt(2, equipmentId);
            ps.executeUpdate();
        }

        recordCheckout(equipmentId,employeeId,checkoutDate,dueDate,notes);

    }

    /**
     * Inserts a checkout history entry.
     *
     * @param equipmentID
     * @param employeeId
     * @param checkoutDate
     * @param dueDate
     * @param notes
     * @throws SQLException
     */
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

    /**
     * Marks equipment as returned and updates the corresponding checkout record.
     *
     * @param equipmentId
     * @param returnDate
     * @throws SQLException
     */
    public void returnEquipment(int equipmentId, LocalDate returnDate) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE Equipment SET is_checked_out=0, current_holder_id=NULL WHERE id=?")) {
            ps.setInt(1, equipmentId);
            ps.executeUpdate();
        }

        recordReturn(equipmentId,returnDate);
    }

    /**
     * Updates the checkout record to include the return timestamp.
     *
     * Only updates the active (unreturned) checkout entry.
     *
     * @param equipmentID
     * @param returnDate
     * @throws SQLException
     */
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

    // *****************************************************************************
    // *------------------------- Record History Requests -------------------------*
    // *****************************************************************************

    /**
     * Returns a ResultSet of all checkout records for a given employee.
     *
     * Caller is responsible for closing the ResultSet.
     *
     * @param employeeId
     * @return
     * @throws SQLException
     */
    public List<Checkout> getEmployeeHistory(int employeeId) throws SQLException {
        String sql = """
        SELECT equipment_id, employee_id, checkout_time, due_time, return_time, notes
        FROM checkouts
        WHERE employee_id = ?
        ORDER BY checkout_time DESC
    """;

        List<Checkout> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Checkout c = new Checkout(
                            rs.getInt("equipment_id"),
                            rs.getInt("employee_id"),
                            LocalDate.parse(rs.getString("checkout_time")),
                            LocalDate.parse(rs.getString("due_time")),
                            LocalDate.parse(rs.getString("return_time")),
                            rs.getString("notes")
                    );
                    list.add(c);
                }
            }
        }

        return list;
    }

    /**
     * Retrieves up to 3 checkout history records for a specific equipment item,
     * returning each row as a formatted String.
     */
    public List<Checkout> getEquipmentHistory(int equipmentId) throws SQLException {
        String sql = """
        SELECT equipment_id, employee_id, checkout_time, due_time, return_time, notes
        FROM checkouts
        WHERE equipment_id = ?
        ORDER BY checkout_time DESC
        LIMIT 3
    """;

        List<Checkout> rows = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, equipmentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate checkout = LocalDate.parse(rs.getString("checkout_time"));
                    LocalDate due = LocalDate.parse(rs.getString("due_time"));

                    String returnStr = rs.getString("return_time");
                    LocalDate returned = returnStr == null ? null : LocalDate.parse(returnStr);


                    Checkout c = new Checkout(
                            rs.getInt("equipment_id"),
                            rs.getInt("employee_id"),
                            checkout,
                            due,
                            returned,
                            rs.getString("notes")
                    );
                    rows.add(c);
                }
            }
        }

        return rows;
    }

    /**
     * Returns all equipment currently checked out by a specific employee.
     *
     * @param employeeId
     * @return
     * @throws SQLException
     */
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

    /**
     * Retrieves all equipment items that are currently checked out
     * AND past their due date.
     *
     * @return List of overdue Equipment objects
     * @throws SQLException
     */
    public List<Equipment> getAllOverdueEquipment() throws SQLException {
        String sql = """
        SELECT e.id, e.name, e.req_skill, e.is_checked_out
        FROM equipment e
        JOIN checkouts c ON e.id = c.equipment_id
        WHERE c.return_time IS NULL
          AND DATE(c.due_time) < DATE('now')
        ORDER BY c.due_time ASC
    """;

        List<Equipment> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipment eq = new Equipment(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("req_skill"),
                        rs.getBoolean("is_checked_out")
                );
                list.add(eq);
            }
        }

        return list;
    }

    /**
     * Retrieves a list of all equipment items that are currently checked out.
     *
     * @return List of Equipment objects
     * @throws SQLException
     */
    public List<Equipment> getAllCheckedOutEquipment() throws SQLException {
        String sql = """
        SELECT e.id, e.name, e.req_skill, e.is_checked_out
        FROM equipment e
        JOIN checkouts c ON e.id = c.equipment_id
        WHERE c.return_time IS NULL
        ORDER BY e.name
    """;

        List<Equipment> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipment eq = new Equipment(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("req_skill"),
                        rs.getBoolean("is_checked_out")
                );
                list.add(eq);
            }
        }

        return list;
    }



    // ********************************************************************
    // *------------------------- Authentication -------------------------*
    // ********************************************************************

    /**
     * Validates a user's password using the stored Argon2 hash.
     *
     * @param id
     * @param password
     * @return
     * @throws SQLException
     */
    public boolean auth(int id, String password) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT pass_hash FROM Employees WHERE id = ?")) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return false;

            return PasswordHasher.verify(password, rs.getString("pass_hash"));
        }
    }

    /**
     * Returns true if the employee is marked as a supervisor.
     *
     * @param id
     * @return
     * @throws SQLException
     */
    public boolean isSuper(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT supervisor FROM Employees WHERE id = ?")) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            return rs.next() && rs.getInt("supervisor") == 1;
        }
    }
}