package com.ui;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;

public class MenuPanel {
    private static JPanel panel;

    private static JButton makeButton(String text, Dimension buttonSize){
        JButton button = new JButton(text);
        button.setPreferredSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMargin(new Insets(0,0,0,0));
        button.setFont(new Font("Arial", Font.PLAIN, 10));
        return button;
    };

    public static JPanel initMenuPanel(CardLayout layout, JPanel cardPanel) {
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.BLUE);
        GridBagConstraints menuGbc = new GridBagConstraints();
        menuGbc.insets = new Insets(5, 5, 5, 5);

        // add label at top of panel
        JLabel label = new JLabel("Menu");
        label.setForeground(Color.white);
        menuGbc.gridx = 0;
        menuGbc.gridy = 0;
        panel.add(label,menuGbc);

        //------------------- Button and Action Listeners for card Layout ------------------//
        //Default button sizing for consistency
        Dimension buttonSize = new Dimension(68,20);

        // button to switch to searchPanel in cardLayout
        JButton searchMenuButton = makeButton("Search", buttonSize);
        searchMenuButton.addActionListener(e -> layout.show(cardPanel,"search"));
        menuGbc.gridx = 0;
        menuGbc.gridy = 1;
        panel.add(searchMenuButton, menuGbc);

        // add second button that does things
        JButton checkoutButton = makeButton("Checkout", buttonSize);
        checkoutButton.addActionListener(e -> layout.show(cardPanel,"checkout"));
        menuGbc.gridx = 0;
        menuGbc.gridy = 2;
        panel.add(checkoutButton,menuGbc);

        // add third button that does things
        JButton reportButton = makeButton("Reports", buttonSize);
        searchMenuButton.addActionListener(e -> layout.show(cardPanel,"reports"));
        menuGbc.gridx = 0;
        menuGbc.gridy = 3;
        panel.add(reportButton,menuGbc);

        //Ghost label to push other items to the top of the panel
        JLabel ghostLabel = new JLabel("");
        menuGbc.gridx = 0;
        menuGbc.gridy = 4;
        menuGbc.weighty = 1;
        panel.add(ghostLabel,menuGbc);

        // Application exit button
        JButton exitButton = makeButton("Exit",  buttonSize);
        exitButton.addActionListener(e -> System.exit(0));
        menuGbc.gridx = 0;
        menuGbc.gridy = 5;
        menuGbc.weighty = 0;
        menuGbc.anchor = GridBagConstraints.SOUTH;
        panel.add(exitButton,menuGbc);

        // set panel size
        panel.setPreferredSize(new Dimension(85,600));

        return panel;
    }






}