package com.ui;

import com.ECSConsole;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;

import static com.ECSConsole.db;

public class Authentication {
    static JFrame authWindow = new  JFrame("Login");
    static JPanel authPanel;
    static JLabel login = new JLabel("User ID");
    static JLabel password = new JLabel("Password");
    static JTextField id = new JTextField();
    static JPasswordField password1 = new JPasswordField();
    static JButton loginButton = new JButton("Login");

    public Authentication() {
        initAuth();
    }

    public static void initAuth() {
        authWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        authWindow.setSize(300, 200);
        authWindow.setLocationRelativeTo(null);

        authPanel = new JPanel();
        authPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);
        c.anchor = GridBagConstraints.WEST;

        id.setPreferredSize(new Dimension(100, 20));
        password1.setPreferredSize(new Dimension(100, 20));
        password1.addActionListener(e -> loginButton.doClick());

        loginButton.addActionListener(
                e -> {
                    int user = Integer.parseInt(id.getText());
                    String pass = new String(password1.getPassword());

                    boolean supervisor;
                    boolean passed;

                    try {
                        supervisor = ECSConsole.db.isSuper(user);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    try {
                        passed = db.auth(user, pass);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    if (passed) {
                        authWindow.setVisible(false);

                        AppWindow mainFrame = new AppWindow(user,supervisor);
                        mainFrame.show();
                    } else {
                        JOptionPane.showMessageDialog(authWindow, "Wrong id or password");
                    }

                    // Clear password array for security
                    Arrays.fill(password1.getPassword(), '\0');
                });
        //Add components
        c.gridx = 1;
        c.gridy = 1;
        authPanel.add(login, c);
        c.gridy = 3;
        authPanel.add(password, c);
        c.gridx = 2;
        c.gridy = 1;
        c.gridwidth = 2;
        authPanel.add(id, c);
        c.gridy = 3;
        authPanel.add(password1, c);
        c.gridy = 5;
        authPanel.add(loginButton, c);
        authWindow.getRootPane().setDefaultButton(loginButton);
        authWindow.add(authPanel);

        show();
    }

    public static void show(){authWindow.setVisible(true);}

}