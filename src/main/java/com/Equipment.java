package main.java.com;

class Equipment {
    private final int id; //unique identifier for specific piece of equipment
    private final String name; //name of equipment
    private final String requiredSkill; //required skills to be able to use equipment
    private boolean isCheckedOut; // checkout status for reference in other classes
    private Employee currentHolder; //used for tracking purposes
    private Employee lastHolder; //also for tracking purposes in report generation

    //Constructor
    public Equipment(int id, String name, String requiredSkill) {
        this.id = id;
        this.name = name;
        this.requiredSkill = requiredSkill;
        this.isCheckedOut = false;
    }

    //Getters and setters
    public int getId() { return id; }

    public String getName() { return name; }

    public String getRequiredSkill() { return requiredSkill; }

    public boolean getIsCheckedOut() { return isCheckedOut; }

    public Employee getCurrentHolder() { return currentHolder; }

    public Employee getLastHolder() { return lastHolder; }

    // Checkout method
    public void checkout(Employee e) {
        this.isCheckedOut = true;
        this.currentHolder = e;
        this.lastHolder = e;
    }

    // Check in method
    public void checkin() {
        this.isCheckedOut = false;
        this.currentHolder = null;
    }

    // toString method overridden to add necessary information
    @Override
    public String toString() {
        String status = isCheckedOut
                ? "Checked out by " + currentHolder.getName()
                : "Available";
        return String.format("[%d] %s (%s) - %s", id, name,
                requiredSkill == null ? "No skill required" : "Requires: " + requiredSkill, status);
    }
}