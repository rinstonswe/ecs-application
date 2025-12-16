package com.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

import static com.ECSConsole.db;
import static java.lang.Integer.parseInt;

public class EquipPanel {
    JPanel panel;
    JLabel idLabel, nameLabel, skillLabel;
    JTextField idField, nameField, skillField;
    JButton equipButton;

    public EquipPanel() {

    }

    JPanel initEquipPanel() {
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        idLabel = new JLabel("Equipment ID:");
        nameLabel = new JLabel("Equipment Name:");
        skillLabel = new JLabel("Skill Required (Leave blank if no skill):");

        idField = new JTextField();
        nameField = new JTextField();
        skillField = new JTextField();

        equipButton = new JButton("Create Equipment");
        equipButton.addActionListener(e -> {
            try {
                db.addEquipment(parseInt(idField.getText()), nameField.getText(), skillField.getText());

                JOptionPane.showMessageDialog(panel,
                        "Equipment Created!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(panel,
                        "Equipment ID must be a number.",
                        "Input Error",
                        JOptionPane.WARNING_MESSAGE);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panel,
                        "Database error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                        "Unexpected error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }


        });
        GridBagConstraints gbc = c(0,1,0);
        panel.add(idLabel, gbc);
        gbc = c(0,1,0);
        panel.add(idField, gbc);
        gbc = c(0,1,0);
        panel.add(nameLabel, gbc);
        gbc = c(0,1,0);
        panel.add(nameField, gbc);
        gbc = c(0,1,0);
        panel.add(skillLabel, gbc);
        gbc = c(0,1,0);
        panel.add(skillField, gbc);
        gbc = c(0,1,0);
        panel.add(equipButton, gbc);

        return panel;

    }

    public GridBagConstraints c(int gx, int gy, int gw) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = gx;
        c.gridy = gy;
        c.gridwidth = gw;
        c.weighty = 0;
        c.weightx = 1.0;
        return c;
    }
}

