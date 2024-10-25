package mealplanner;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The DatabaseManager class handles all the database operations
 * for managing meals, ingredients, and meal plans. It establishes
 * a connection with the database, creates necessary tables, and
 * provides methods to insert, retrieve, and delete records related
 * to meals and their associated ingredients.
 */
public class DatabaseManager {
    private Connection connection;  // Holds the database connection object


    // Method to establish a connection with the database
    public void connect() throws SQLException {
        String DB_URL = "jdbc:postgresql://localhost/meals_db";
        String USER = "postgres";
        String PASS = "1111";
        connection = DriverManager.getConnection(DB_URL, USER, PASS);
        createTables();
    }

    // Method to disconnect from the database
    public void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    // Method to create necessary tables if they don't already exist
    public void createTables() throws SQLException {
        Statement stmt = connection.createStatement();
        String createMealsTable = "CREATE TABLE IF NOT EXISTS meals (" +
                "category VARCHAR(255)," +
                "meal VARCHAR(255)," +
                "meal_id INTEGER  PRIMARY KEY)";
        stmt.executeUpdate(createMealsTable);

        String createIngredientsTable = "CREATE TABLE IF NOT EXISTS ingredients (" +
                "ingredient VARCHAR(255)," +
                "ingredient_id INTEGER  PRIMARY KEY," +
                "meal_id INTEGER REFERENCES meals(meal_id))";
        stmt.executeUpdate(createIngredientsTable);

        String createPlanTable = "CREATE TABLE IF NOT EXISTS plan (" +
                "meal_option VARCHAR(255)," +
                "meal_category VARCHAR(255)," +
                "meal_id INTEGER REFERENCES meals(meal_id)," +
                "PRIMARY KEY (meal_option, meal_category))";
        stmt.executeUpdate(createPlanTable);

        stmt.close();

    }

    // Method to generate a new ID for a meal by finding the max current ID and adding 1
    public int generateNewMealId() throws SQLException {
        String query = "SELECT MAX(meal_id) FROM meals";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        int newId = 0;
        if (rs.next()) {
            newId = rs.getInt(1) + 1;
        }
        rs.close();
        stmt.close();
        return newId;
    }

    // Method to generate a new ID for an ingredient by finding the max current ID and adding 1
    public int generateNewIngredientId() throws SQLException {
        String query = "SELECT MAX(ingredient_id) FROM ingredients";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        int newId = 0;
        if (rs.next()) {
            newId = rs.getInt(1) + 1;
        }
        rs.close();
        statement.close();
        return newId;
    }

    // Method to insert a new meal into the 'meals' and 'ingredients' tables
    public void insertMeal(Meal meal) throws SQLException {
        int newId = generateNewMealId();
        meal.setId(newId);

        // Prepare SQL query to insert the meal into the 'meals' table
        String insertQuery = "INSERT INTO meals (category, meal, meal_id) VALUES (?, ?, ?)";
        PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
        insertStmt.setString(1, meal.getCategory());
        insertStmt.setString(2, meal.getName());
        insertStmt.setInt(3, meal.getId());
        insertStmt.executeUpdate();
        insertStmt.close();

        // Prepare SQL query to insert each ingredient of the meal into the 'ingredients' table
        String insertIngredientQuery = "INSERT INTO ingredients (ingredient, ingredient_id, meal_id)" +
                " VALUES (?, ?, ?)";
        PreparedStatement insertIngredientStmt = connection.prepareStatement(insertIngredientQuery);
        for (String ingredient : meal.getIngredients()) {
            insertIngredientStmt.setString(1, ingredient);
            insertIngredientStmt.setInt(2, generateNewIngredientId());
            insertIngredientStmt.setInt(3, meal.getId());
            insertIngredientStmt.executeUpdate();
        }
        insertIngredientStmt.close();

    }

    // Method to retrieve all meals from the 'meals' table
    public List<Meal> getAllMeals() throws SQLException {
        List<Meal> meals = new ArrayList<>();

        String query = "SELECT * FROM meals";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            String category = rs.getString("category");
            String name = rs.getString("meal");
            int id = rs.getInt("meal_id");

            List<String> ingredients = getIngredientsForMeal(id);
            Meal meal = new Meal(category, name, ingredients, id);
            meals.add(meal);
        }

        rs.close();
        stmt.close();

        return meals;
    }

    // Method to get ingredients for a specific meal based on meal_id
    public List<String> getIngredientsForMeal(int mealId) throws SQLException {
        List<String> ingredients = new ArrayList<>();
        String query = "SELECT * FROM ingredients WHERE meal_id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, mealId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String ingredient = rs.getString("ingredient");
            ingredients.add(ingredient);
        }

        rs.close();
        stmt.close();

        return ingredients;
    }

    // Method to retrieve meals filtered by category from the 'meals' table
    public List<Meal> getMealsFromCategory(String category) throws SQLException {
        List<Meal> meals = new ArrayList<>();
        String query = "SELECT * FROM meals WHERE category = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, category);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String name = rs.getString("meal");
            List<String> ingredients = getIngredientsForMeal(rs.getInt("meal_id"));
            Meal meal = new Meal(category, name, ingredients, rs.getInt("meal_id"));
            meals.add(meal);
        }

        rs.close();
        stmt.close();
        return meals;
    }

    // Method to insert an entry into the 'plan' table, associating a meal with a day and category
    public void insertPlanEntry(String day, String category, int mealId) throws SQLException {
        String insertQuery = "INSERT INTO plan (meal_option, meal_category, meal_id) VALUES (?, ?, ?)";
        PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
        insertStmt.setString(1, day);
        insertStmt.setString(2, category);
        insertStmt.setInt(3, mealId);
        insertStmt.executeUpdate();
        insertStmt.close();

    }

    // Method to delete the plan from the 'plan' table
    public void deletePlan() throws SQLException {
        String deleteQuery = "DELETE FROM plan";
        Statement deleteStatement = connection.createStatement();
        deleteStatement.executeUpdate(deleteQuery);
        deleteStatement.close();
    }

    // Method to get the name of a meal by its ID
    public String getMealNameById(int mealId) throws SQLException {
        String query = "SELECT * FROM meals WHERE meal_id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, mealId);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        return rs.getString("meal");

    }

    // Method to print all plan entries, grouped by day
    public void getPlanEntries() throws SQLException {
        String query = "SELECT * FROM plan";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet rs = preparedStatement.executeQuery();

        if (!rs.isBeforeFirst()) {
            System.out.println("No meals found");
        }

        String currentDay = "";

        while (rs.next()) {
            String day = rs.getString("meal_option");
            String category = rs.getString("meal_category");
            int mealId = rs.getInt("meal_id");

            if (!day.equals(currentDay)) {
                currentDay = day;
                System.out.println("\n" + day);
            }

            System.out.println(category.substring(0, 1).toUpperCase() + category.substring(1) +
                    ": " + getMealNameById(mealId));
        }
        rs.close();
        preparedStatement.close();
    }

    // Method to get the list of ingredients needed for a plan, with a count of how many times each ingredient appears
    public Map<String, Integer> getIngredientsFromPlan() throws SQLException {
        String query = "SELECT p.meal_id, i.ingredient FROM plan p" +
                " JOIN ingredients i ON i.meal_id = p.meal_id";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet rs = preparedStatement.executeQuery();
        Map<String, Integer> ingredients = new HashMap<>();

        while (rs.next()) {
            String ingredient = rs.getString("ingredient");

            if (!ingredients.containsKey(ingredient)) {
                ingredients.put(ingredient, 1);
            } else {
                ingredients.put(ingredient, ingredients.get(ingredient) + 1);
            }
        }
        rs.close();
        preparedStatement.close();
        return ingredients;
    }
}
