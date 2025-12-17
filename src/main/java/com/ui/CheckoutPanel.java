package com.ui;

import com.data.Employee;
import com.data.Equipment;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;

import static com.ECSConsole.db;

public class CheckoutPanel {

    private final int employeeId;
    private int equipmentId;
    private JTextField equipmentIdField;
    private JTextArea noteField;
    private JTextPane infoTextPane;
    private JButton checkoutButton;
    private JPanel panel, northPanel, centerPanel, southPanel;
    GridBagConstraints c = new GridBagConstraints();

    public CheckoutPanel(int employeeId) {
        this.employeeId = employeeId;
    }

    public JPanel initCheckoutPanel() {
        panel = new JPanel(new BorderLayout(5, 5));

        panel.add(buildNorthPanel(), BorderLayout.NORTH);
        panel.add(buildCenterPanel(), BorderLayout.CENTER);
        panel.add(buildSouthPanel(), BorderLayout.SOUTH);

        return panel;
    }

    private JButton createCheckoutButton() {
        checkoutButton = new JButton("Checkout");
        checkoutButton.setEnabled(false);

        checkoutButton.addActionListener(e -> {
            try {
                db.checkoutEquipment(equipmentId, employeeId, LocalDate.now(), LocalDate.now().plusDays(5), noteField.getText());

                JOptionPane.showMessageDialog(panel, "Checkout successful");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        return checkoutButton;
    }

    private JTextPane initInfoTextPane() {
        infoTextPane = new JTextPane();
        infoTextPane.setEditable(false);
        infoTextPane.setOpaque(false);
        infoTextPane.setContentType("text/html");
        return infoTextPane;
    }

    private JTextField initIdField() {
        equipmentIdField = new JTextField();
        equipmentIdField.setPreferredSize(new Dimension(50, 20));

        equipmentIdField.addActionListener(e -> {
            searchResult();
        });

        return equipmentIdField;
    }

    private JPanel buildNorthPanel() {
        northPanel = new JPanel();
        northPanel.add(new JLabel("Equipment ID"));
        northPanel.add(initIdField());
        return northPanel;
    }

    private JPanel buildCenterPanel() {
        centerPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(initInfoTextPane(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.5;
        centerPanel.add(buildNoteField(), gbc);

        return centerPanel;
    }

    private JPanel buildSouthPanel() {
        southPanel = new JPanel();
        southPanel.add(createCheckoutButton());
        return southPanel;
    }

    private void searchResult() {
        try {
            equipmentId = Integer.parseInt(equipmentIdField.getText());

            Equipment equipment = db.getEquipmentById(equipmentId);
            Employee employee = db.getEmployeeById(employeeId);

            if (equipment == null) {
                infoTextPane.setText("<html><b>Equipment not found</b></html>");
                checkoutButton.setEnabled(false);
                return;
            }

            boolean match = (equipment.getRequiredSkill() == null || employee.getSkills().contains(equipment.getRequiredSkill()));
            checkoutButton.setEnabled(match);

            infoTextPane.setText(buildHtml(equipment, match));

            noteField.setVisible(true);

        } catch (Exception ex) {
            infoTextPane.setText("<html><b>Error retrieving equipment</b></html>");
            checkoutButton.setEnabled(false);
        }
    }

    private String buildHtml(Equipment eq, boolean match) {
        String skillMatchHtml = match
                ? "<span style='color: green;'>True</span>"
                : "<span style='color: red;'>False</span>";

        return """
                <html>
                    <body style='font-family: sans-serif;'>
                        <b>EQUIPMENT ID: </b> %s<br>
                        <b>NAME: </b> %s<br>
                        <b>REQUIRED SKILL: </b> %s<br>
                        <b>SKILL MATCH: </b> %s<br>
                        <b>CHECKOUT DATE: </b> %s<br>
                        <b>RETURN DATE: </b> %s
                    </body>
                </html>
                """.formatted(
                eq.getId(),
                eq.getName(),
                eq.getRequiredSkill(),
                skillMatchHtml,
                LocalDate.now(),
                LocalDate.now().plusDays(5)
        );
    }

    private JTextArea buildNoteField() {
        noteField = new JTextArea();
        noteField.setPreferredSize(new Dimension(300, 150));
        noteField.setVisible(false);
        Border border = BorderFactory.createEtchedBorder();
        noteField.setBorder(border);
        return noteField;
    }
}