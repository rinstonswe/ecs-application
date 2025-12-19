package com.ui;

import static com.ECSConsole.db;
import static java.lang.Integer.parseInt;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

public class ReportPanel {

    private JTable table;
    private JTextField equipIdField, empIdField;
    private String currentReportName = "Report";

    public JPanel initReportPanel() {

        JPanel root = new JPanel(new BorderLayout());

        // *************************************************************
        // *-------------- ROW 1 — GLOBAL REPORT BUTTONS --------------*
        // *************************************************************
        JPanel globalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnCheckedOut = new JButton("All Checked Out");
        btnCheckedOut.addActionListener(e -> {
            try {
                currentReportName = "CheckedOut";
                displayList(db.getAllCheckedOutEquipment());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton btnOverdue = new JButton("All Overdue");
        btnOverdue.addActionListener(e -> {
            try {
                currentReportName = "Overdue";
                displayList(db.getAllOverdueEquipment());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton btnEmployees = new JButton("All Employees");
        btnEmployees.addActionListener(e -> {
            try {
                currentReportName = "Employees";
                displayList(db.getEmployeesByName(""));
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        globalPanel.add(btnCheckedOut);
        globalPanel.add(btnOverdue);
        globalPanel.add(btnEmployees);

        // ************************************************************
        // *----------------- ROW 2 ID‑BASED REPORTS -----------------*
        // ************************************************************
        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Employee-specific
        idPanel.add(new JLabel("Employee ID:"));
        empIdField = new JTextField();
        empIdField.setPreferredSize(new Dimension(80, 20));
        idPanel.add(empIdField);

        JButton btnEquipForEmployee = new JButton("Equipment for Employee");
        btnEquipForEmployee.addActionListener(e -> {
            try {
                int id = parseInt(empIdField.getText());
                currentReportName = String.format("%dEquipment", id);
                displayList(db.getEquipmentByEmployee(id));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(root, "Invalid Employee ID");
            }
        });
        idPanel.add(btnEquipForEmployee);

        // Equipment-specific
        idPanel.add(new JLabel("Equipment ID:"));
        equipIdField = new JTextField();
        equipIdField.setPreferredSize(new Dimension(80, 20));
        idPanel.add(equipIdField);

        JButton btnEquipHistory = new JButton("Equipment History");
        btnEquipHistory.addActionListener(e -> {
            try {
                int id = parseInt(equipIdField.getText().trim());
                currentReportName = String.format("%dEquipmentHistory", id);
                displayList(db.getEquipmentHistory(id));
            } catch (Exception ex) {
                ex.printStackTrace();   // TEMP: see the real error
                JOptionPane.showMessageDialog(root, "Invalid Equipment ID");
            }
        });
        idPanel.add(btnEquipHistory);

        // *************************************************************
        // *------- Combine both rows into a single NORTH panel -------*
        // *************************************************************
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(globalPanel);
        north.add(idPanel);

        root.add(north, BorderLayout.NORTH);

        // ************************************************************
        // *---------------------- CENTER TABLE ----------------------*
        // ************************************************************
        table = new JTable();
        table.setShowGrid(true);
        table.setGridColor(Color.GRAY);
        table.setFillsViewportHeight(true);

        root.add(new JScrollPane(table), BorderLayout.CENTER);

        // ***********************************************************
        // *------------------ SOUTH EXPORT BUTTON ------------------*
        // ***********************************************************
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportButton = new JButton("Export");
        exportButton.addActionListener(e -> {
            // Use the last report type the user ran
            exportTableToCSV(currentReportName);
        });
        exportPanel.add(exportButton);

        root.add(exportPanel, BorderLayout.SOUTH);

        return root;
    }

    // ***************************************************************
    // *---- Converts ANY List<?> into a JTable using reflection ----*
    // ***************************************************************
    private void displayList(List<?> list) {
        if (list == null || list.isEmpty()) {
            table.setModel(new DefaultTableModel());
            return;
        }

        Object first = list.get(0);
        Field[] fields = first.getClass().getDeclaredFields();

        Vector<String> columnNames = new Vector<>();
        for (Field f : fields) {
            columnNames.add(f.getName());
        }

        Vector<Vector<Object>> rows = new Vector<>();

        try {
            for (Object obj : list) {
                Vector<Object> row = new Vector<>();
                for (Field f : fields) {
                    f.setAccessible(true);
                    row.add(f.get(obj));
                }
                rows.add(row);
            }
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }

        table.setModel(new DefaultTableModel(rows, columnNames));
    }

    private void exportTableToCSV(String reportName) {
        try {
            // Build filename: ReportType_YYYY-MM-DD_HH-mm-ss.csv
            String timestamp = java.time.LocalDateTime.now()
                    .toString()
                    .replace(":", "-")
                    .replace("T", "_");

            String fileName = reportName + "_" + timestamp + ".csv";

            // Resolve user's Downloads folder
            String userHome = System.getProperty("user.home");
            java.nio.file.Path downloads = java.nio.file.Paths.get(userHome, "Downloads", fileName);

            // Build CSV content
            StringBuilder sb = new StringBuilder();

            // Column headers
            for (int col = 0; col < table.getColumnCount(); col++) {
                sb.append(table.getColumnName(col));
                if (col < table.getColumnCount() - 1) sb.append(",");
            }
            sb.append("\n");

            // Rows
            for (int row = 0; row < table.getRowCount(); row++) {
                for (int col = 0; col < table.getColumnCount(); col++) {
                    Object value = table.getValueAt(row, col);
                    sb.append(value == null ? "" : value.toString());
                    if (col < table.getColumnCount() - 1) sb.append(",");
                }
                sb.append("\n");
            }

            // Write file
            java.nio.file.Files.writeString(downloads, sb.toString());

            JOptionPane.showMessageDialog(null,
                    "Report exported to:\n" + downloads.toString(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Failed to export CSV:\n" + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}