package com.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class AppWindow {

    private JFrame window;
    private MenuPanel menuPanel;
    private JPanel cardPanel;
    private JPanel searchPanel, equipPanel, empPanel;
    private CardLayout cardLayout = new CardLayout();
    private CheckoutPanel checkoutPanel;
    private ReturnPanel returnPanel;
    private EquipPanel createPanel;
    private ReportPanel reportPanel;

    private boolean supervisor;
    private int user;


    public AppWindow(int user, boolean supervisor) {
        setUser(user);
        setSupervisor(supervisor);

        buildWindow();
        buildCards();
        buildMenu();
    }

    //------------------------------- Build the main application window -------------------------------
    private void buildWindow() {
        window = new JFrame("Equipment Checkout System");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(800, 600);
        window.setLayout(new BorderLayout());
        window.setResizable(false);
        window.setLocationRelativeTo(null);
    }

    //------------------------------- Build the left-side menu panel -------------------------------
    private void buildMenu() {
        menuPanel = new MenuPanel();
        window.add(menuPanel.initMenuPanel(cardLayout, cardPanel, supervisor), BorderLayout.WEST);

    }

    //------------------------------- Build the card panel (center) -------------------------------
    private void buildCards() {
        cardPanel = new JPanel(cardLayout);

        // Equipment search panel
        equipPanel = EquipSearchPanel.initSearchPanel();

        // Combined search panel (equipment + employee if supervisor)
        searchPanel = new JPanel();
        if (supervisor) {
            empPanel = EmpSearchPanel.initEmpSearchPanel();
            searchPanel.setLayout(new GridLayout(2, 1));
            searchPanel.add(equipPanel);
            searchPanel.add(empPanel);
        } else {
            searchPanel.setLayout(new GridLayout(1, 1));
            searchPanel.add(equipPanel);
        }

        // Checkout panel
        checkoutPanel = new CheckoutPanel(user);

        // Return Panel
        returnPanel = new ReturnPanel(user);
        JPanel returnCard = returnPanel.initReturnPanel();
        returnCard.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                returnCard.revalidate();
                returnCard.repaint();
            }
        });

        // Report Panel
        reportPanel = new ReportPanel();

        // Add Equipment Panel
        createPanel = new EquipPanel();

        JPanel reportTest = reportPanel.initReportPanel();

        // Add cards
        cardPanel.add(searchPanel, "search");
        cardPanel.add(checkoutPanel.initCheckoutPanel(), "checkout");
        cardPanel.add(returnCard, "return");
        returnCard.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                returnPanel.refreshPanel();
            }
        });
        cardPanel.add(createPanel.initEquipPanel(), "create");
        cardPanel.add(reportTest,"report");

        window.add(cardPanel, BorderLayout.CENTER);
    }

    //------------------------------- Show the window -------------------------------
    public void show() {
        window.setVisible(true);
    }

    //------------------------------- Supervisor flag -------------------------------
    public boolean isSupervisor() {
        return supervisor;
    }

    public void setSupervisor(boolean supervisor) {
        this.supervisor = supervisor;
    }

    //------------------------------- Logged-in user ID -------------------------------
    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }
}