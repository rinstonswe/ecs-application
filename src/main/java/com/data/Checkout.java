package com.data;

import java.time.LocalDate;

public class Checkout {

    private int equipmentId;
    private int employeeId;
    private LocalDate checkoutTime;
    private LocalDate dueTime;
    private LocalDate returnTime;
    private String notes;

    // ---------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------
    public Checkout(int equipmentId,
                    int employeeId,
                    LocalDate checkoutTime,
                    LocalDate dueTime,
                    LocalDate returnTime,
                    String notes) {

        this.equipmentId = equipmentId;
        this.employeeId = employeeId;
        this.checkoutTime = checkoutTime;
        this.dueTime = dueTime;
        this.returnTime = returnTime;
        this.notes = notes;
    }

    // ---------------------------------------------------------
    // Getters and Setters
    // ---------------------------------------------------------
    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(LocalDate checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public LocalDate getDueTime() {
        return dueTime;
    }

    public void setDueTime(LocalDate dueTime) {
        this.dueTime = dueTime;
    }

    public LocalDate getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(LocalDate returnTime) {
        this.returnTime = returnTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // ---------------------------------------------------------
    // toString()
    // ---------------------------------------------------------
    @Override
    public String toString() {
        return "Checkout{" +
                "equipmentId=" + equipmentId +
                ", employeeId=" + employeeId +
                ", checkoutTime=" + checkoutTime +
                ", dueTime=" + dueTime +
                ", returnTime=" + returnTime +
                ", notes='" + notes + '\'' +
                '}';
    }
}