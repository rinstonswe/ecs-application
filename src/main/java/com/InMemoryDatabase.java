package main.java.com;

/*
This is used to create an in memory database for testing and demonstration purposes.
In a production environment replace references to the inMemoryDatabase to point appropriately to your existing databases
and add appropriate query for the given database.
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class InMemoryDatabase {
    private final Map<Integer, Employee> employees = new HashMap<>();
    private final Map<Integer, Equipment> equipmentList = new HashMap<>();

    public Map<Integer, Employee> getEmployees() { return employees; }

    public Map<Integer, Equipment> getEquipmentList() { return equipmentList; }

    public void seedTestData() {
        employees.put(1, new Employee(1, "John Carter", Set.of("electrician", "welder")));
        employees.put(2, new Employee(2, "Maria Lopez", Set.of("plumber", "carpenter")));
        employees.put(3, new Employee(3, "Alex Smith", Set.of("painter")));
        employees.put(4, new Employee(4, "Sarah Johnson", Set.of("carpenter", "electrician")));

        equipmentList.put(100, new Equipment(100, "Welding Torch", "welder"));
        equipmentList.put(101, new Equipment(101, "Electric Drill", "electrician"));
        equipmentList.put(102, new Equipment(102, "Hammer", null));
        equipmentList.put(103, new Equipment(103, "Pipe Wrench", "plumber"));
        equipmentList.put(104, new Equipment(104, "Paint Sprayer", "painter"));
    }

    public String getEquipment(int id) {
        for (Equipment equipment : equipmentList.values()) {
            if (equipment.getId() == id) {
                return equipment.toString();
            }

        }
        return "No equipment with id " + id;
    }
}