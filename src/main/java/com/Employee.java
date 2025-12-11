package com;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class Employee {

    private final int id;
    private final String name;
    private final Set<String> skills;
    private final List<Equipment> checkedOutEquipment = new ArrayList<>();

    // Constructor
    public Employee(int id, String name, Set<String> skills) {
        this.id = id;
        this.name = name;
        this.skills = skills;
    }

    // getters and setters
    public int getId() { return id; }

    public String getName() { return name; }

    public Set<String> getSkills() { return skills; }

    public List<Equipment> getCheckedOutEquipment() { return checkedOutEquipment; }

    public boolean canUse(Equipment e) {
        return e.getRequiredSkill() == null || skills.contains(e.getRequiredSkill());
    }

    public void checkout(Equipment e) {
        checkedOutEquipment.add(e);
    }

    public void returnEquipment(Equipment e) {
        checkedOutEquipment.remove(e);
    }

    // toString method overridden from default to include necessary information
    @Override
    public String toString() {
        return String.format("%d - %s (Skills: %s)", id, name, skills);
    }
}
