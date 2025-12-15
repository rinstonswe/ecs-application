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

        });
    }
}
