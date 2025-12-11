package com;

import javax.swing.*;
import java.awt.*;

public class AppWindow {
    public static JFrame appWindow;
    public static JPanel menuPanel, searchPanel;

    public static JFrame initWindow() {
        appWindow = new JFrame();

        //Set window default behavior
        appWindow.setTitle("Equipment Checkout System"); //Title shown at top of window
        appWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Terminates window
        appWindow.setSize(800, 600); //Sets default application size to 800x600 pixels
        appWindow.setLayout(new BorderLayout()); //Sets window layout to Boarder
        appWindow.setResizable(false);

        menuPanel = com.MenuPanel.initMenuPanel();
        searchPanel = com.SearchPanel.initSearchPanel();

        JPanel cardPanel = cardPanel();

        appWindow.add(menuPanel, BorderLayout.WEST);
        appWindow.add(cardPanel, BorderLayout.CENTER);
        appWindow.setLocationRelativeTo(null);

        return appWindow;
    }

    static JPanel cardPanel() {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new CardLayout());
        cardPanel.add(searchPanel, "searchPanel");
        return cardPanel;
    }
}
