package com.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List

public class ReportPanel {
    private JPanel panel,buttonPanel, dataPanel;


    ReportPanel(){
        initReportPanel();
    }

    private void initReportPanel(){
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        buttonPanel = new JPanel(new FlowLayout());
        dataPanel = new JPanel();


    }

    private List<String> getEmp(){
        List<String> empList = List.of();

        return empList;
    }

    private List<String> getEquipment(String checkedOut) {
        List<String> equipmentList = List.of();
        return equipmentList;
    }

    private List<String> getOverdue(String emp) {
        List<String> overdueList = List.of();
        return overdueList;
    }

    private List<String> getEmployeeEquipment(String emp) {
        List<String> employeeEquipmentList = List.of();
        return employeeEquipmentList;
    }

    private List<String> getEquipmentHistory(String equipment) {
        List<String> equipmentHistoryList = List.of();
        return equipmentHistoryList;
    }
}
