package com.data;

public class Equipment {

    private final int id;              // unique identifier
    private final String name;         // equipment name
    private final String requiredSkill; // skill needed to use it
    private final boolean isCheckedOut; // from DB: 0 = false, 1 = true

    public Equipment(int id, String name, String requiredSkill, boolean isCheckedOut) {
        this.id = id;
        this.name = name;
        this.requiredSkill = requiredSkill;
        this.isCheckedOut = isCheckedOut;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRequiredSkill() {
        return requiredSkill;
    }

    public boolean isCheckedOut() {
        return isCheckedOut;
    }

    @Override
    public String toString() {
        return String.format(
                "[%d] %s — %s%s",
                id,
                name,
                requiredSkill == null ? "No skill required" : "Requires: " + requiredSkill,
                isCheckedOut ? " (Checked Out)" : ""
        );
    }
}