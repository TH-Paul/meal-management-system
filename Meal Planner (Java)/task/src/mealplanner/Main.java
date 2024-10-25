package mealplanner;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Scanner;


/**
 * The Main class is the entry point of the meal planner application.
 * It provides a command-line interface where the user can interact with the system
 * by adding meals, displaying meals by category, creating a weekly plan, listing the plan,
 * saving the plan to a file, or exiting the application.
 */
public class Main {
  public static void main(String[] args) throws SQLException, FileNotFoundException {

      // Create a new instance of DatabaseManager and establish a connection to the database
      DatabaseManager db = new DatabaseManager();
      db.connect();

      // Create a new instance of MealManager that will interact with the database
      MealManager mealManager = new MealManager(db);

    Scanner scanner = new Scanner(System.in);
    String input;

      // Infinite loop to continually prompt the user for input until they choose to exit
      label:
      while (true) {
        System.out.println("What would you like to do (add, show, plan, list plan, save, exit)?");
        input = scanner.nextLine();
          switch (input) {
              case "add":
                  mealManager.addMeal();
                  break;
              case "show":
                  mealManager.showMeals();
                  break;
              case "plan":
                  mealManager.createWeeklyPlan();
                  break;
              case "list plan":
                  mealManager.displayPlan();
                  break;
              case "save":
                  mealManager.saveToFile();
                  break;
              case "exit":
                  System.out.println("Bye!");
                  break label;
          }
      }
      db.disconnect();
  }
}