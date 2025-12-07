package main.java.com;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * GB Manufacturing - Equipment Checkout System (ECS)
 * Console Version - IntelliJ Ready
 *
 * Features:
 *  - Interactive Application Window
 *  - Employee and Equipment management
 *  - Checkout and return tracking
 *  - Skill-based restrictions
 *  - Randomized test mode (--test)
 *
 * Author: Michael Wright, Geoffrey Baker
 * Prepared for: GB Manufacturing Project
 * Date: October 29, 2025
 */

public class ECSConsole {
    // Default scanner object for simplification
    private static final Scanner scanner = new Scanner(System.in);
    // Create database object
    static final InMemoryDatabase db = new InMemoryDatabase();
    // Version number, used to determine if the menu should be run or not
    private static final double version = 0.1;
    // Used to test the GUI will later be changed to a CLI mode to allow for simpler CLI implementations
    private static final boolean guiMode = false;


    public static void main(String[] args) {





        //initialize the in memory test database
        db.seedTestData();

        // allows for random testing rather than running the program interactively.
        if (args.length > 0 && args[0].equalsIgnoreCase("--test")) {
            runRandomTestMode();
            return;
        }
        // Determines if program should be run in CLI mode or GUI mode.
        if (version > 1.0 || guiMode) {
            //Run the GUI on single thread as the used JavaSwing is not thread safe.
            SwingUtilities.invokeLater(() -> {
                Interface mainFrame = new Interface();
                mainFrame.show();
            });}
        else {showMenu();}
    }

    //CLI based menu, early and simpler implementation prior to creation of GUI mode
    private static void showMenu() {
        while (true) {
            System.out.println("\n=== GB Manufacturing Equipment Checkout System ===");
            System.out.println("1. View Employees");
            System.out.println("2. View Equipment");
            System.out.println("3. Checkout Equipment");
            System.out.println("4. Return Equipment");
            System.out.println("5. View Employee Equipment");
            System.out.println("6. Exit");
            System.out.print("Select an option: ");
            //Checks user input
            String choice = scanner.nextLine();
            System.out.println(); // Prints empty line, helps to break up output for easier reading
            //Runs specific methods based on user input
            switch (choice) {
                case "1":
                    listEmployees(); // see listEmployees()
                    pause();
                case "2":
                    listEquipment(); // see listEquipment();
                    pause();
                case "3":
                    checkoutEquipment(); // see checkoutEquipment();
                    pause();
                case "4":
                    returnEquipment(); // see returnEquipment();
                    pause();
                case "5":
                    viewEmployeeEquipment(); // see viewEmployeeEquipment();
                    pause();
                case "6":
                    System.out.println("Goodbye!");
                    return; // Exits program

                // acts as error handling, if anything other than a desired case is entered provides this output
                default:
                    System.out.println();
                    System.out.println("Invalid option.\nPlease choose a number from 1 to 6.\nTry again.");
                    pause();
            }
        }
    }

    // Queries database to get a list of employees
    private static void listEmployees() {
        System.out.println("\n--- Employees ---");
        db.getEmployees().values().forEach(System.out::println);
    }

    // queries database to get a list of all equipment
    private static void listEquipment() {
        System.out.println("\n--- Equipment ---");
        db.getEquipmentList().values().forEach(System.out::println);
    }

    // Initializes equipment checkout process and updates appropriate data base entries
    private static void checkoutEquipment() {
        listEmployees();
        System.out.print("\nEnter employee ID: ");
        int empId = Integer.parseInt(scanner.nextLine());
        Employee emp = db.getEmployees().get(empId);
        if (emp == null) {
            System.out.println("Invalid employee ID.");
            return;
        }

        listEquipment();
        System.out.print("\nEnter equipment ID to checkout: ");
        int eqId = Integer.parseInt(scanner.nextLine());
        Equipment eq = db.getEquipmentList().get(eqId);
        if (eq == null) {
            System.out.println("Invalid equipment ID.");
            return;
        }

        if (eq.getIsCheckedOut()) {
            System.out.println("That equipment is already checked out!");
            return;
        }

        if (!emp.canUse(eq)) {
            System.out.println("This employee does not have the required skill to use that equipment.");
            return;
        }

        eq.checkout(emp);
        emp.checkout(eq);
        System.out.println("✅ " + emp.getName() + " successfully checked out " + eq.getName());
    }

    // initializes equipment return and updates appropriate database entries
    private static void returnEquipment() {
        listEquipment();
        System.out.print("\nEnter equipment ID to return: ");
        int eqId = Integer.parseInt(scanner.nextLine());
        Equipment eq = db.getEquipmentList().get(eqId);

        if (eq == null || !eq.getIsCheckedOut()) {
            System.out.println("That equipment is not currently checked out.");
            return;
        }

        Employee emp = eq.getCurrentHolder();
        eq.checkin();
        emp.returnEquipment(eq);
        System.out.println("✅ " + eq.getName() + " returned successfully by " + emp.getName());
    }

    // Provides information regarding what equipment is checked out to a given employee
    private static void viewEmployeeEquipment() {
        listEmployees();
        System.out.print("\nEnter employee ID to view their equipment: ");
        int empId = Integer.parseInt(scanner.nextLine());
        Employee emp = db.getEmployees().get(empId);

        if (emp == null) {
            System.out.println("Invalid employee ID.");
            return;
        }

        System.out.println("\n--- Equipment currently checked out by " + emp.getName() + " ---");
        if (emp.getCheckedOutEquipment().isEmpty()) {
            System.out.println("None.");
        } else {
            emp.getCheckedOutEquipment().forEach(System.out::println);
        }
    }

    // --- TEST MODE ---
    private static void runRandomTestMode() {
        System.out.println("Running ECS Test Mode...");

        Random rand = new Random();
        List<Employee> employees = new ArrayList<>(db.getEmployees().values());
        List<Equipment> equipment = new ArrayList<>(db.getEquipmentList().values());

        // Randomly checkout and return equipment
        for (int i = 0; i < 10; i++) {
            Employee emp = employees.get(rand.nextInt(employees.size()));
            Equipment eq = equipment.get(rand.nextInt(equipment.size()));

            if (!eq.getIsCheckedOut() && emp.canUse(eq)) {
                eq.checkout(emp);
                emp.checkout(eq);
                System.out.printf("Test: %s checked out %s%n", emp.getName(), eq.getName());
            } else if (eq.getIsCheckedOut()) {
                eq.getCurrentHolder().returnEquipment(eq);
                eq.checkin();
                System.out.printf("Test: %s returned %s%n", emp.getName(), eq.getName());
            }
        }

        System.out.println("\n--- Test Summary ---");
        listEquipment();
        System.out.println("\nTest Mode complete.");
    }
    public static void pause() {
        System.out.println("Press any key to continue...");
        scanner.nextLine();
    }
}

// This is the GUI, eventually this will become the meat of the main class, this will query a class that exist for the
// purpose of interacting with the databases and updating information. The main class will then no longer directly
// manipulate the main class.
class Interface {
    //create the variables corresponding to the pieces that will make up the primary GUI window
    private JFrame window;
    private JTextField eqSearchTextField;
    private JPanel menuPanel;
    private JPanel cardPanel;
    private JPanel searchPanel;
    private JPanel reportPanel;
    private JPanel checkoutPanel;
    private JTextPane searchTextArea;
    private JScrollPane searchScroll;

    // Launches the application window
    public Interface() {
        initWindow();
    }

    // defines what the window will contain and where those items will be located
    public void initWindow() {
        //create window
        window = new JFrame();

        //Set window default behavior
        window.setTitle("Equipment Checkout System"); //Title shown at top of window
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Terminates window
        window.setSize(800, 600); //Sets default application size to 800x600 pixels
        window.setLayout(new BorderLayout()); //Sets window layout to Boarder
        window.setResizable(false);

        //------------------- Start of Card Panel -------------------//
        // This panel will be used to hold the different options that can be selected by the menu panel.
        cardPanel = new JPanel();
        cardPanel.setLayout(new CardLayout());

        window.add(cardPanel, BorderLayout.CENTER); // Adds to the center panel in the

        //------------------- Start of Menu Panel -------------------//
        // This panel will contain a series of buttons that will change the content to the card panel.
        menuPanel = new JPanel();
        menuPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        window.add(menuPanel, BorderLayout.WEST);

        menuPanel.setBackground(Color.blue);
        // add label at top of panel
        JLabel label = new JLabel("Menu");
        label.setForeground(Color.white);
        menuPanel.add(label, BorderLayout.CENTER);
        // add button that does things
        Button button1 = new Button("Button 1");
        menuPanel.add(button1);
        // add second button that does things
        Button button2 = new Button("Button 2");
        menuPanel.add(button2);
        // add third button that does things
        Button button3 = new Button("Button 3");
        menuPanel.add(button3);
        // set panel size
        menuPanel.setPreferredSize(new Dimension(85,600));

        //------------------Start of Search Panel----------------------//
        // This panel will contain all information related to search queries
        searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        // This adds the search Panel to the card panel
        cardPanel.add(searchPanel, "searchPanel");

        // Create Search Label
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(Color.DARK_GRAY);

        // Create Text pane to display search results
        searchTextArea = new JTextPane();
        searchTextArea.setEditable(false);
        searchTextArea.setPreferredSize(new Dimension(200,350));

        //Create Scroll pane for search results
        searchScroll = new JScrollPane(searchTextArea);


        eqSearchTextField = createJTextField();
        eqSearchTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent search) {
                searchTextArea.setText(String.valueOf(ECSConsole.db.getEquipment(Integer.parseInt(eqSearchTextField.getText()))));
                eqSearchTextField.setText("");
            }});


        //add previously created items to the panel
        searchPanel.add(searchLabel, BorderLayout.NORTH);
        searchPanel.add(eqSearchTextField, BorderLayout.NORTH);
        searchPanel.add(searchScroll, BorderLayout.CENTER);


        // Centers the created window in the center of the main monitor
        window.setLocationRelativeTo(null); // Centers the window on the main screen.
    }

    public void show() {
        window.setVisible(true);
    }

    //----------------------- Create and configure generic JTextField -------------------//

    public JTextField createJTextField() {
        JTextField textField = new JTextField(10);

        textField.setFont(new Font("Arial", Font.BOLD, 16));



        return textField;
    }

}
