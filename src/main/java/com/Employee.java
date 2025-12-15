package com;

import java.util.Set;

public class Employee {

    private final int id;
    private final String name;
    private final Set<String> skills;
    private final boolean supervisor;
    private final String passHash;

    public Employee(int id, String name, Set<String> skills, boolean supervisor, String passHash) {
        this.id = id;
        this.name = name;
        this.skills = skills;
        this.supervisor = supervisor;
        this.passHash = passHash;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public boolean isSupervisor() {
        return supervisor;
    }

    public String getPassHash() {
        return passHash;
    }

    public boolean canUse(Equipment e) {
        return e.getRequiredSkill() == null || skills.contains(e.getRequiredSkill());
    }

    @Override
    public String toString() {
        return String.format(
                "%d - %s (Skills: %s, Supervisor: %s)",
                id, name, skills, supervisor
        );
    }
}