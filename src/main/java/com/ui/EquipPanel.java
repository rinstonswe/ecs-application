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

        nameLabel = new JLabel("Equipment Name:");
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        skillLabel = new JLabel("Skill Required (Leave blank if no skill):");
        skillLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));

        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(200, 40));
        nameField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
        skillField = new JTextField();
        skillField.setPreferredSize(new Dimension(200, 40));
        skillField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));

        equipButton = new JButton("Create Equipment");
        equipButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        equipButton.addActionListener(e -> {
            try {
                String skill = skillField.getText();
                if (skill.isEmpty()) {
                    skill = null;
                }
                db.addEquipment(nameField.getText(), skill);

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
        GridBagConstraints gbc = c(0,0);
        panel.add(nameLabel, gbc);
        gbc = c(0,1);
        panel.add(nameField, gbc);
        gbc = c(0,2);
        panel.add(skillLabel, gbc);
        gbc = c(0,3);
        panel.add(skillField, gbc);
        gbc = c(0,4);
        panel.add(equipButton, gbc);

        return panel;

    }

    public GridBagConstraints c(int gx, int gy) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = gx;
        c.gridy = gy;
        c.weightx = 1.0;
        c.insets = new Insets(10,10,10,10);
        return c;
    }
}

