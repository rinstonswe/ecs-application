package com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import static com.ECSConsole.db;

public class EmpSearchPanel {
    private static JPanel searchPanel;
    private static JTextPane searchTextArea;
    private static JScrollPane searchScroll;
    private static JTextField empIdField, skillField, nameField;

    public static JPanel initEmpSearchPanel() {
        searchPanel = new JPanel();
        searchPanel.setLayout(new GridBagLayout());

        // Create Search Labels
        JLabel idSearchLabel = new JLabel("Employee ID: ");
        idSearchLabel.setForeground(Color.DARK_GRAY);
        JLabel skillSearchLabel = new JLabel("Employee Skill: ");
        skillSearchLabel.setForeground(Color.DARK_GRAY);
        JLabel nameSearchLabel = new JLabel("Name: ");
        nameSearchLabel.setForeground(Color.DARK_GRAY);

        // Create Text pane to display search results
        searchTextArea = new JTextPane();
        searchTextArea.setEditable(false);

        //Create Scroll pane for search results
        searchScroll = new JScrollPane(searchTextArea);

        // Create text field box that will be used to search for employee by id
        empIdField = createJTextField();
        empIdField.addActionListener(new ActionListener() {
            // This action listener will take the text in the box and pass it to the database searchEmployees method
            @Override
            public void actionPerformed(ActionEvent search) {
                // Set the search result text area
                try {
                    searchTextArea.setText(
                                // Call searchEmployees method of the DB
                                db.searchEmployees("id",
                                        // Attempt to convert the text integer to search for employee via ID
                                        Integer.parseInt(empIdField.getText())));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                // Clear the search text field for the next search
                empIdField.setText("");
            }
        });

        // Create text field box that will be used to search for equipment
        skillField = createJTextField();
        skillField.addActionListener(new ActionListener() {
            // This action listener will take the text in the box and pass it to the database getEquipment method in ECSConsole
            @Override
            public void actionPerformed(ActionEvent search) {
                // Set the search result text area
                try {
                    searchTextArea.setText(
                            // Convert the results to a readable string
                            String.valueOf(
                                    //
                                    db.searchEmployees("skills",skillField.getText())));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                // Clear the search text field for the next search
                skillField.setText("");
            }
        });

        nameField = createJTextField();
        nameField.addActionListener(new ActionListener() {
            // This action listener will take the text in the box and pass it to the database getEquipment method in ECSConsole
            @Override
            public void actionPerformed(ActionEvent search) {
                // Set the search result text area
                try {
                    searchTextArea.setText(
                            // Convert the results to a readable string
                            String.valueOf(
                                    //Search based on value in name text field
                                    db.searchEmployees("name",nameField.getText())));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                // Clear the search text field for the next search
                nameField.setText("");
            }
        });


        //add previously created items to the panel
        searchPanel.add(idSearchLabel, gbc(0, 0, 1, 1, 0));
        searchPanel.add(empIdField, gbc(1, 0, 2, 1, 0));

        searchPanel.add(skillSearchLabel, gbc(0, 1, 1, 1, 0));
        searchPanel.add(skillField, gbc(1, 1, 2, 1, 0));

        searchPanel.add(nameSearchLabel, gbc(0, 2, 1, 1, 0));
        searchPanel.add(nameField, gbc(1, 2, 2, 1, 0));

        GridBagConstraints c = gbc(3,0,2,4, 1);
        c.weightx = 1.0;
        searchPanel.add(searchScroll, c);

        return searchPanel;
    }

    //----------------------- Create and configure generic JTextField -------------------//
    // This can be used to create every JTextField that will be used
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
        gbc.insets = new Insets(10, 10, 10, 10); // top, left, bottom, right
        gbc.fill = GridBagConstraints.BOTH;
        return gbc;
    }


}
