=====================================================
 GB MANUFACTURING EQUIPMENT CHECKOUT SYSTEM (ECS)
=====================================================

Author: Michael Wright
Prepared For: GB Manufacturing - Team 1
Date: October 29, 2025
Version: 1.0 (Console Edition)
Language: Java 11+
IDE: IntelliJ IDEA Community Edition

-----------------------------------------------------
PROJECT OVERVIEW
-----------------------------------------------------
The Equipment Checkout System (ECS) was developed to
replace GB Manufacturing’s outdated pen-and-paper
tracking process. The ECS application allows employees
to check out, return, and manage equipment digitally
through an easy-to-use console interface.

This version includes:
 - In-memory database (no setup required)
 - Employee and equipment tracking
 - Skill-based restrictions
 - Historical tracking of last employee per equipment
 - Built-in Test Mode (--test)
 - Fully runnable JAR build (no dependencies)

-----------------------------------------------------
HOW TO RUN (IN INTELLIJ)
-----------------------------------------------------
1. Open IntelliJ IDEA.
2. Create a new Java project (Java 11+).
3. Inside the `src` folder, create a new file named:
      ECSConsole.java
4. Copy and paste the ECS source code provided.
5. Click the green ▶ icon beside the main() method
   or right-click and select "Run ECSConsole.main()".

-----------------------------------------------------
HOW TO RUN TEST MODE
-----------------------------------------------------
To run the built-in test mode:
 - Go to: Run → Edit Configurations
 - Under “Program Arguments,” enter:
       --test
 - Click Run ▶

This will automatically run randomized checkout and
return operations and display a test summary.

-----------------------------------------------------
HOW TO BUILD A JAR
-----------------------------------------------------
1. Go to File → Project Structure → Artifacts.
2. Click + → JAR → From modules with dependencies.
3. Select ECSConsole as the main class.
4. Click Apply → OK.
5. Go to Build → Build Artifacts → Build.

Your runnable JAR will be located at:
   out/artifacts/GBM_ECS_Project_jar/

-----------------------------------------------------
HOW TO RUN THE JAR FILE
-----------------------------------------------------
In your terminal, navigate to the JAR’s folder and run:
   java -jar GBM_ECS_Project.jar
   java -jar GBM_ECS_Project.jar --test

-----------------------------------------------------
NOTES
-----------------------------------------------------
- This is a standalone console version for testing.
- No database or external files required.
- All data resets on program restart.

-----------------------------------------------------
CONTACT
-----------------------------------------------------
Author: Michael Wright
Project Sponsor: Team 1
Organization: GB Manufacturing
Date: 10/29/2025
