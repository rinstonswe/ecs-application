package com.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

import static com.ECSConsole.db;
import static java.lang.Integer.parseInt;

public class CheckoutPanel {

    private final int employeeId;
    private JTextField equipmentIdField;
    private JTextPane infoArea;
    private JButton checkoutButton;
    private JPanel panel, northPanel, centerPanel, southPanel;

    public CheckoutPanel(int employeeId) {
        this.employeeId = employeeId;
    }

    public JPanel initCheckoutPanel() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 5));


        panel.add(buildNorthPanel(), BorderLayout.NORTH);
        panel.add(buildCenterPanel(), BorderLayout.CENTER);
        panel.add(buildSouthPanel(), BorderLayout.SOUTH);
        return  panel;
    }

    private JButton createCheckoutButton() {
        checkoutButton = new JButton("Checkout");

        return checkoutButton;
    }

    private JTextPane initInfoArea() {
        infoArea = new JTextPane();

        return infoArea;
    }

    private JTextField initIdField() {
        equipmentIdField = new JTextField();

        equipmentIdField.addActionListener(e -> {
            try {
                db.checkoutEquipment(employeeId, parseInt(equipmentIdField.getText()));
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        return equipmentIdField;
    }

    private JPanel buildNorthPanel() {
        northPanel = new JPanel();

        JLabel equipmentIdLabel = new JLabel("Equipment ID");

        northPanel.add(equipmentIdLabel);
        northPanel.add(initIdField());

        return northPanel;
    }

    private JPanel buildCenterPanel() {
        centerPanel = new JPanel();

        centerPanel.add(initInfoArea());

        return centerPanel;
    }

    private JPanel buildSouthPanel() {
        southPanel = new JPanel();

        southPanel.add(createCheckoutButton());

        return southPanel;
    }
}