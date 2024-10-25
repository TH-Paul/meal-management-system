package mealplanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * MealManager is responsible for handling user interactions and operations
 * related to meals, such as adding meals, showing meals by category, creating a weekly plan,
 * and saving the meal plan to a file. It uses DatabaseManager to manage database-related tasks.
 */
public class MealManager {
    private DatabaseManager dbManager; // Dependency on DatabaseManager for database operations

    public MealManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    // Method to add a new meal by interacting with the user
    public void addMeal() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        String category = scanner.nextLine();
        while (!isValidCategory(category)) {
            System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
            category = scanner.nextLine();
        }

        System.out.println("Input the meal's name:");
        String name = scanner.nextLine();
        while (!isValidFormat(name)) {
            System.out.println("Wrong format. Use letters only!");
            name = scanner.nextLine();
        }

        System.out.println("Input the ingredients:");
        String ingredientsInput;
        String[] ingredients;
        boolean isValid;
        do {
            ingredientsInput = scanner.nextLine();
            ingredients = ingredientsInput.split(",");
            isValid = true;

            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i] = ingredients[i].trim();
                if (!isValidFormat(ingredients[i])) {
                    System.out.println("Wrong format. Use letters only!");
                    isValid = false;
                    break;
                }
            }

        } while (!isValid);

        List<String> ingredientsList = new ArrayList<>(Arrays.asList(ingredients));

        Meal meal = new Meal(category, name, ingredientsList);
        dbManager.insertMeal(meal);
        System.out.println("The meal has been added!");
    }

    // Helper method to validate the category (breakfast, lunch, dinner)
    public boolean isValidCategory(String category) {
        return category.equals("breakfast") || category.equals("lunch") || category.equals("dinner");
    }

    // Helper method to validate if the input only contains letters and spaces
    public boolean isValidFormat(String input) {
        return input.matches("[a-zA-Z]+[a-zA-Z\\s]*");
    }

    // Method to show meals from a specific category
    public void showMeals() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");
        String category = scanner.nextLine();
        while (!isValidCategory(category)) {
            System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
            category = scanner.nextLine();
        }

        List<Meal> meals = dbManager.getMealsFromCategory(category);
        if (meals.isEmpty()) {
            System.out.println("No meals found.");
        } else {
            System.out.println("Category: " + category + "\n");
            for (Meal meal : meals) {
                System.out.println(meal);
                System.out.println();
            }
        }

    }

    // Method to create a weekly plan by assigning meals to days and categories
    public void createWeeklyPlan() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String[] weekDays = {"Monday", "Tuesday", "Wednesday", "Thursday",
                "Friday", "Saturday", "Sunday"};
        String[] categories = {"breakfast", "lunch", "dinner"};
        List<Meal> meals;
        String mealName;
        Meal selectedMeal;

        dbManager.deletePlan();

        for (String day : weekDays) {
            System.out.println(day);
            for (String category : categories) {
                meals = dbManager.getMealsFromCategory(category);
                Collections.sort(meals);
                for (Meal meal : meals) {
                    System.out.println(meal.getName());
                }
                System.out.println("Choose the " + category + " for " + day + " from the list above:");

                selectedMeal = null;
                while (selectedMeal == null) {
                    mealName = scanner.nextLine();
                    selectedMeal = findMealByName(meals, mealName);
                    if (selectedMeal == null) {
                        System.out.println("This meal doesn’t exist. Choose a meal from the list above.");
                    }
                }
                dbManager.insertPlanEntry(day, category, selectedMeal.getId());
            }
            System.out.println("Yeah! We planned the meals for " + day + ".");
            System.out.println();
        }
    }

    // Helper method to find a meal by its name from a list of meals
    private Meal findMealByName(List<Meal> meals, String mealName) {
        for (Meal meal : meals) {
            if (meal.getName().equals(mealName)) {
                return meal;
            }
        }
        return null;
    }

    // Method to display the current weekly meal plan
    public void displayPlan() throws SQLException {
        dbManager.getPlanEntries();
    }

    // Method to save the ingredients list from the current plan to a file
    public void saveToFile() throws SQLException, FileNotFoundException {
        Scanner scanner = new Scanner(System.in);
        Map<String, Integer> ingredients = dbManager.getIngredientsFromPlan();
        if (ingredients.isEmpty()) {
            System.out.println("Unable to save. Plan your meals first.");
        }

        System.out.println("Input a filename:");
        String filename = scanner.nextLine();

        File file = new File(filename);
        PrintWriter printWriter = new PrintWriter(file);
        for (Map.Entry<String, Integer> entry : ingredients.entrySet()) {
            if (entry.getValue() == 1) {
                printWriter.println(entry.getKey());
            } else {
                printWriter.println(entry.getKey() + " x" + entry.getValue());
            }
        }
        printWriter.close();
    }
}
