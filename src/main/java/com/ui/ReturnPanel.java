package com.ui;

import com.data.Equipment;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.ECSConsole.db;

public class ReturnPanel {

    private final int employeeId;
    private JPanel panel;
    private JButton returnButton;
    private List<JCheckBox> equipmentCheckBoxes;

    public ReturnPanel(int employeeId) {
        this.employeeId = employeeId;
    }

    public JPanel initReturnPanel() {
        panel = new JPanel(new BorderLayout(10, 10));

        panel.add(buildCenterPanel(), BorderLayout.CENTER);

        returnButton = new JButton("Return Selected Equipment");
        returnButton.addActionListener(e -> handleReturn());
        panel.add(returnButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        equipmentCheckBoxes = new ArrayList<>();

        try {
            List<Equipment> checkedOut = db.getEquipmentByEmployee(employeeId);

            if (checkedOut.isEmpty()) {
                centerPanel.add(new JLabel("No equipment currently checked out."));
            } else {
                for (Equipment eq : checkedOut) {
                    JCheckBox cb = new JCheckBox(
                            "ID: " + eq.getId() + " | " + eq.getName() +
                                    " (Skill: " + eq.getRequiredSkill() + ")"
                    );
                    cb.putClientProperty("equipmentId", eq.getId());
                    equipmentCheckBoxes.add(cb);
                    centerPanel.add(cb);
                }
            }
        } catch (SQLException ex) {
            centerPanel.add(new JLabel("Error loading equipment: " + ex.getMessage()));
        }

        return centerPanel;
    }

    private void handleReturn() {
        List<Integer> selectedIds = new ArrayList<>();
        for (JCheckBox cb : equipmentCheckBoxes) {
            if (cb.isSelected()) {
                selectedIds.add((Integer) cb.getClientProperty("equipmentId"));
            }
        }

        if (selectedIds.isEmpty()) {
            JOptionPane.showMessageDialog(panel,
                    "Please select equipment to return.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(panel,
                "Are you sure you want to return equipment IDs: " + selectedIds + "?",
                "Confirm Return",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            for (int equipmentId : selectedIds) {
                try {
                    db.returnEquipment(equipmentId, LocalDate.now());
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(panel,
                            "Error returning equipment ID " + equipmentId + ": " + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            JOptionPane.showMessageDialog(panel,
                    "Selected equipment returned successfully.",
                    "Return Complete",
                    JOptionPane.INFORMATION_MESSAGE);

            refreshPanel();
        }
    }

    // Refresh the center panel
    public void refreshPanel(){
        panel.remove(0); // remove old center panel
        panel.add(buildCenterPanel(), BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }
}