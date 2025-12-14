package com;

import java.sql.SQLException;
import java.util.*;
import javax.swing.*;
import java.awt.*;

/**
 * GB Manufacturing - Equipment Checkout System (ECS)
 * Console Version - IntelliJ Ready
 *
 * Features:
 *  - Interactive Application Window
 *  - Employee and Equipment management
 *  - Checkout and return tracking
 *  - Skill-based restrictions
 *  - Randomized test mode (--test)
 *
 * Author: Michael Wright, Geoffrey Baker
 * Prepared for: GB Manufacturing Project
 * Date: October 29, 2025
 */

public class ECSConsole {
    // Default scanner object for simplification
    private static final Scanner scanner = new Scanner(System.in);
    // Create database object
    public static final DatabaseManager db;

    static {
        try {
            db = new DatabaseManager();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {

        // Determines if program should be run in CLI mode or GUI mode.
        //Run the GUI on single thread as the used JavaSwing is not thread safe.
        SwingUtilities.invokeLater(() -> {
            Authentication authFrame = new Authentication();
            authFrame.initAuth();
            authFrame.show();

            /*Interface mainFrame = new Interface();
            mainFrame.initWindow();
            mainFrame.show();*/

        });
    }
    // This is the GUI, eventually this will become the meat of the main class, this will query a class that exist for the
// purpose of interacting with the databases and updating information. The main class will then no longer directly
// manipulate the Database.
    static class Interface {
        //create the variables corresponding to the pieces that will make up the primary GUI window
        private JFrame window;
        private JTextField eqSearchTextField;
        private JPanel cardPanel, equipPanel, empPanel, searchPanel, reportPanel, checkoutPanel;
        private JTextPane searchTextArea;
        private JScrollPane searchScroll;
        private boolean supervisor;

        // Launches the application window
        public Interface() {
            window = AppWindow.initWindow();
        }

        // defines what the window will contain and where those items will be located
        public void initWindow() {
            //------------------- Start of Card Panel -------------------//
            // This panel will be used to hold the different options that can be selected by the menu panel.
            cardPanel = new JPanel();
            cardPanel.setLayout(new CardLayout());

            window.add(cardPanel, BorderLayout.CENTER); // Adds to the center panel in the
            //Call the MenuPanel class and add the resulting panel into the window
            equipPanel = EquipSearchPanel.initSearchPanel();
            searchPanel = new JPanel();
            if(supervisor) {
                empPanel = EmpSearchPanel.initEmpSearchPanel();
                searchPanel.setLayout(new GridLayout(2, 1));
                searchPanel.add(equipPanel);
                searchPanel.add(empPanel);
            }
            else{
                searchPanel.setLayout(new GridLayout(1, 1));
                searchPanel.add(equipPanel);
            }
            cardPanel.add(searchPanel, "search");





            // Centers the created window in the center of the main monitor
            window.setLocationRelativeTo(null); // Centers the window on the main screen.
        }

        public void show() {
            window.setVisible(true);
        }

        public boolean getSupervisor() {
            return this.supervisor;
        }
        public void setSupervisor(boolean supervisor) {
            this.supervisor = supervisor;
        }
    }

}