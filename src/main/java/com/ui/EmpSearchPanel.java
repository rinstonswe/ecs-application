package com.ui;

import com.Employee;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static com.ECSConsole.db;

public class EmpSearchPanel {
    private static JPanel searchPanel;
    private static JTextPane searchTextArea;
    private static JScrollPane searchScroll;
    private static JTextField empIdField, skillField, nameField;

    public static JPanel initEmpSearchPanel() {
        searchPanel = new JPanel(new GridBagLayout());

        JLabel idSearchLabel = new JLabel("Employee ID: ");
        idSearchLabel.setForeground(Color.DARK_GRAY);

        JLabel skillSearchLabel = new JLabel("Employee Skill: ");
        skillSearchLabel.setForeground(Color.DARK_GRAY);

        JLabel nameSearchLabel = new JLabel("Name: ");
        nameSearchLabel.setForeground(Color.DARK_GRAY);

        searchTextArea = new JTextPane();
        searchTextArea.setEditable(false);

        searchScroll = new JScrollPane(searchTextArea);
        Dimension fixed = new Dimension(400, 260);
        searchScroll.setPreferredSize(fixed);
        searchScroll.setMinimumSize(fixed);
        searchScroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
        );

        // ---------------- Search by ID ----------------
        empIdField = createJTextField();
        empIdField.addActionListener((ActionEvent e) -> {
            try {
                int id = Integer.parseInt(empIdField.getText());
                Employee emp = db.getEmployeeById(id);

                if (emp != null) {
                    searchTextArea.setText(formatEmployee(emp));
                } else {
                    searchTextArea.setText("No employee found with ID " + id);
                }
            } catch (Exception ex) {
                searchTextArea.setText("Error: " + ex.getMessage());
            }
            empIdField.setText("");
        });

        // ---------------- Search by Skill ----------------
        skillField = createJTextField();
        skillField.addActionListener((ActionEvent e) -> {
            try {
                String skill = skillField.getText();
                List<Employee> results = db.getEmployeesBySkill(skill);
                searchTextArea.setText(formatEmployeeList(results));
            } catch (SQLException ex) {
                searchTextArea.setText("Error: " + ex.getMessage());
            }
            skillField.setText("");
        });

        // ---------------- Search by Name ----------------
        nameField = createJTextField();
        nameField.addActionListener((ActionEvent e) -> {
            try {
                String name = nameField.getText();
                List<Employee> results = db.getEmployeesByName(name);
                searchTextArea.setText(formatEmployeeList(results));
            } catch (SQLException ex) {
                searchTextArea.setText("Error: " + ex.getMessage());
            }
            nameField.setText("");
        });

        // Layout
        searchPanel.add(idSearchLabel, gbc(0, 0, 1, 1, 0));
        searchPanel.add(empIdField, gbc(1, 0, 2, 1, 0));

        searchPanel.add(skillSearchLabel, gbc(0, 1, 1, 1, 0));
        searchPanel.add(skillField, gbc(1, 1, 2, 1, 0));

        searchPanel.add(nameSearchLabel, gbc(0, 2, 1, 1, 0));
        searchPanel.add(nameField, gbc(1, 2, 2, 1, 0));

        GridBagConstraints c = gbc(3, 0, 2, 4, 1);
        c.weightx = 1.0;
        searchPanel.add(searchScroll, c);

        return searchPanel;
    }

    private static String formatEmployee(Employee emp) {
        return String.format(
                "ID: %d | Name: %s | Skills: %s | Role: %s",
                emp.getId(),
                emp.getName(),
                emp.getSkills(),
                emp.isSupervisor() ? "Supervisor" : "Standard"
        );
    }

    private static String formatEmployeeList(List<Employee> list) {
        if (list.isEmpty()) return "No employees found.";
        return list.stream()
                .map(EmpSearchPanel::formatEmployee)
                .collect(Collectors.joining("\n"));
    }

    public static JTextField createJTextField() {
        JTextField textField = new JTextField(10);
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        return textField;
    }

    private static GridBagConstraints gbc(int x, int y, int w, int h, int z) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        gbc.weighty = z;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        return gbc;
    }
}