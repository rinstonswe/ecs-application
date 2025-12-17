package com.ui;

import com.data.Equipment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static com.ECSConsole.db;

public class EquipSearchPanel {
    private static JPanel searchPanel;
    private static JTextPane searchTextArea;
    private static JScrollPane searchScroll;
    private static JTextField equipmentIdField, requiredSkillField, nameField;

    public static JPanel initSearchPanel() {
        searchPanel = new JPanel(new GridBagLayout());

        JLabel idSearchLabel = new JLabel("Equipment ID: ");
        idSearchLabel.setForeground(Color.DARK_GRAY);

        JLabel skillSearchLabel = new JLabel("Required Skill: ");
        skillSearchLabel.setForeground(Color.DARK_GRAY);

        JLabel nameSearchLabel = new JLabel("Name: ");
        nameSearchLabel.setForeground(Color.DARK_GRAY);

        searchTextArea = new JTextPane();
        searchTextArea.setEditable(false);
        searchScroll = new JScrollPane(searchTextArea);
        Dimension fixed = new Dimension(400, 260);
        searchScroll.setPreferredSize(fixed);
        searchScroll.setMinimumSize(fixed);



        // ---------------- Search by ID ----------------
        equipmentIdField = createJTextField();
        equipmentIdField.addActionListener((ActionEvent e) -> {
            try {
                int id = Integer.parseInt(equipmentIdField.getText());
                Equipment eq = db.getEquipmentById(id);

                if (eq != null) {
                    searchTextArea.setText(formatEquipment(eq));
                } else {
                    searchTextArea.setText("No equipment found with ID " + id);
                }
            } catch (Exception ex) {
                searchTextArea.setText("Error: " + ex.getMessage());
            }
            equipmentIdField.setText("");
        });

        // ---------------- Search by Required Skill ----------------
        requiredSkillField = createJTextField();
        requiredSkillField.addActionListener((ActionEvent e) -> {
            try {
                String skill = requiredSkillField.getText();
                List<Equipment> results = db.getEquipmentBySkill(skill);
                searchTextArea.setText(formatEquipmentList(results));
            } catch (SQLException ex) {
                searchTextArea.setText("Error: " + ex.getMessage());
            }
            requiredSkillField.setText("");
        });

        // ---------------- Search by Name ----------------
        nameField = createJTextField();
        nameField.addActionListener((ActionEvent e) -> {
            try {
                String name = nameField.getText();
                List<Equipment> results = db.getEquipmentByName(name);
                searchTextArea.setText(formatEquipmentList(results));
            } catch (SQLException ex) {
                searchTextArea.setText("Error: " + ex.getMessage());
            }
            nameField.setText("");
        });

        // Layout
        searchPanel.add(idSearchLabel, gbc(0, 0, 1, 1, 0));
        searchPanel.add(equipmentIdField, gbc(1, 0, 2, 1, 0));

        searchPanel.add(skillSearchLabel, gbc(0, 1, 1, 1, 0));
        searchPanel.add(requiredSkillField, gbc(1, 1, 2, 1, 0));

        searchPanel.add(nameSearchLabel, gbc(0, 2, 1, 1, 0));
        searchPanel.add(nameField, gbc(1, 2, 2, 1, 0));

        GridBagConstraints c = gbc(3, 0, 2, 4, 1);
        c.weightx = 1.0;
        searchPanel.add(searchScroll, c);

        return searchPanel;
    }

    private static String formatEquipment(Equipment eq) {
        return String.format(
                "ID: %d | Name: %s | Requires: %s | Checked Out: %s",
                eq.getId(),
                eq.getName(),
                eq.getRequiredSkill() == null ? "None" : eq.getRequiredSkill(),
                eq.isCheckedOut() ? "Yes" : "No"
        );
    }

    private static String formatEquipmentList(List<Equipment> list) {
        if (list.isEmpty()) return "No equipment found.";
        return list.stream()
                .map(EquipSearchPanel::formatEquipment)
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