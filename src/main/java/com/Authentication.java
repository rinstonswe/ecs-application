package com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Arrays;

import static com.ECSConsole.db;

public class Authentication {
    static JFrame authWindow = new  JFrame("Login");
    static JPanel authPanel;
    static JLabel login = new JLabel("Login");
    static JLabel password = new JLabel("Password");
    static JTextField username = new JTextField();
    static JPasswordField password1 = new JPasswordField();
    static JButton loginButton = new JButton("Login");

    public static void initAuth() {
        authWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        authWindow.setSize(300, 200);
        authWindow.setLocationRelativeTo(null);

        authPanel = new JPanel();
        authPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);
        c.anchor = GridBagConstraints.WEST;

        username.setPreferredSize(new Dimension(100, 20));
        password1.setPreferredSize(new Dimension(100, 20));

        loginButton.addActionListener(
                e -> {
                    String user = username.getText();
                    String pass = Arrays.toString(password1.getPassword());
                    boolean supervisor = false;
                    boolean passed = false;
                    while (!passed) {

                        try {
                            supervisor = ECSConsole.db.isSuper(user);
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        try {
                            if (db.auth(user, pass)) {
                                authWindow.setVisible(false);
                                passed = true;
                            } else {
                                JOptionPane.showMessageDialog(authWindow, "Wrong username or password");
                            }
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    ECSConsole.Interface mainFrame = new ECSConsole.Interface();
                    mainFrame.setSupervisor(supervisor);
                    mainFrame.initWindow();
                    mainFrame.show();
                }
        );


        c.gridx = 1;
        c.gridy = 1;
        authPanel.add(login, c);
        c.gridy = 3;
        authPanel.add(password, c);
        c.gridx = 2;
        c.gridy = 1;
        c.gridwidth = 2;
        authPanel.add(username, c);
        c.gridy = 3;
        authPanel.add(password1, c);
        c.gridy = 5;
        authPanel.add(loginButton, c);
        authWindow.add(authPanel);

        show();
    }

    public static void show(){authWindow.setVisible(true);}

}
