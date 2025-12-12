package com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import static com.ECSConsole.db;

public class SearchPanel {
    private static JPanel searchPanel;
    private static JTextPane searchTextArea;
    private static JScrollPane searchScroll;
    private static JTextField eqSearchTextField, skillSearchTextField, nameSearchTextField;

    public static JPanel initSearchPanel() {
        searchPanel = new JPanel();
        searchPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Create Search Labels
        JLabel idSearchLabel = new JLabel("Equipment ID: ");
        idSearchLabel.setForeground(Color.DARK_GRAY);
        JLabel skillSearchLabel = new JLabel("Required Skill: ");
        skillSearchLabel.setForeground(Color.DARK_GRAY);
        JLabel nameSearchLabel = new JLabel("Required Skill: ");
        nameSearchLabel.setForeground(Color.DARK_GRAY);

        // Create Text pane to display search results
        searchTextArea = new JTextPane();
        searchTextArea.setEditable(false);
        searchTextArea.setPreferredSize(new Dimension(200, 350));

        //Create Scroll pane for search results
        searchScroll = new JScrollPane(searchTextArea);

        // Create text field box that will be used to search for equipment
        eqSearchTextField = createJTextField();
        eqSearchTextField.addActionListener(new ActionListener() {
            // This action listener will take the text in the box and pass it to the database getEquipment method in ECSConsole
            @Override
            public void actionPerformed(ActionEvent search) {
                // Set the search result text area
                try {
                    searchTextArea.setText(
                            // Convert the results to a readable string
                            String.valueOf(
                                    // Call getEquipment method of the DB object in ECSConsole
                                    db.idSearch(
                                            // Attempt to convert the text integer to search for equipment via ID
                                            Integer.parseInt(eqSearchTextField.getText()))));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                // Clear the search text field for the next search
                eqSearchTextField.setText("");
            }
        });

        // Create text field box that will be used to search for equipment
        skillSearchTextField = createJTextField();
        skillSearchTextField.addActionListener(new ActionListener() {
            // This action listener will take the text in the box and pass it to the database getEquipment method in ECSConsole
            @Override
            public void actionPerformed(ActionEvent search) {
                // Set the search result text area
                try {
                    searchTextArea.setText(
                            // Convert the results to a readable string
                            String.valueOf(
                                    //
                                    db.skillSearch(skillSearchTextField.getText())));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                // Clear the search text field for the next search
                skillSearchTextField.setText("");
            }
        });

        nameSearchTextField = createJTextField();
        nameSearchTextField.addActionListener(new ActionListener() {
            // This action listener will take the text in the box and pass it to the database getEquipment method in ECSConsole
            @Override
            public void actionPerformed(ActionEvent search) {
                // Set the search result text area
                try {
                    searchTextArea.setText(
                            // Convert the results to a readable string
                            String.valueOf(
                                    //
                                    db.nameSearch(skillSearchTextField.getText())));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                // Clear the search text field for the next search
                nameSearchTextField.setText("");
            }
        });


        //add previously created items to the panel
        gbc.gridx = 1;
        gbc.gridy = 1;
        searchPanel.add(idSearchLabel, gbc);
        gbc.gridx = 2;
        gbc.width = 2;
        searchPanel.add(eqSearchTextField, gbc);
        gbc.gridy = 2;
        gbc.gridx = 1;
        gbc.width = 1;
        searchPanel.add(skillSearchLabel, gbc);
        gbc.gridx = 2;
        gbc.width = 2;
        searchPanel.add(skillSearchTextField, gbc);
        gbc.gridy = 3;
        gbc.gridx = 1;
        gbc.width = 1;
        searchPanel.add(nameSearchLabel, gbc);
        gbc.gridx = 2;
        gbc.width = 1;
        searchPanel.add(nameSearchTextField, gbc);
        gbc.gridy = 2;
        gbc.gridx = 1;
        searchPanel.add(searchScroll, gbc);
        return searchPanel;
    }

    //----------------------- Create and configure generic JTextField -------------------//
    // This can be used to create every JTextField that will be used
    public static JTextField createJTextField() {
        JTextField textField = new JTextField(10);
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        return textField;
    }


}
