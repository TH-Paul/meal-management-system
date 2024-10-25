# Meal Management System

A Java-based application designed to manage meal plans, allowing users to add, categorize, and plan meals for the week, as well as store meal data in a database.


## Introduction

The Meal Management System is a command-line application that helps users manage meal data efficiently, including functionalities to add new meals, categorize them (e.g., breakfast, lunch, dinner), create a weekly meal plan, and export ingredients needed for the plan.

## Features

1. **Add Meal**: Allows users to add meals with a specified category and ingredients.
2. **Show Meals**: Lists meals by category (breakfast, lunch, dinner).
3. **Create Weekly Plan**: Users can create a meal plan by assigning meals to specific days and categories.
4. **List Plan**: Displays the current weekly meal plan.
5. **Save Plan**: Exports ingredients needed for the current plan to a specified file.

## Project Structure

### 1. `Main.java`
   - Entry point of the application.
   - Provides a command-line interface for the user to interact with the system.
   - Supports commands like `add`, `show`, `plan`, `list plan`, `save`, and `exit`.

### 2. `DatabaseManager.java`
   - Handles database operations, including connecting, disconnecting, and creating tables.
   - Manages meal and ingredient data in the database, allowing insertion, retrieval, and deletion.
   - Methods include:
     - `connect()` and `disconnect()`: Establish and close the database connection.
     - `insertMeal(Meal meal)`: Adds a meal to the database.
     - `getAllMeals()` and `getMealsFromCategory(String category)`: Retrieve meals from the database.
     - `insertPlanEntry(String day, String category, int mealId)`: Inserts meal plans into the database.
     - `getIngredientsFromPlan()`: Retrieves ingredients for the planned meals.

### 3. `Meal.java`
   - Defines the `Meal` model with properties for category, name, ingredients, and ID.
   - Implements `Comparable<Meal>` for sorting by name.
   - Overridden `toString()` method for custom display format.

### 4. `MealManager.java`
   - Manages user interactions related to meals, such as adding and displaying meals.
   - Provides functionality for creating and viewing weekly plans.
   - Key methods include:
     - `addMeal()`: Guides the user to input and add meal data.
     - `showMeals()`: Displays meals by category.
     - `createWeeklyPlan()`: Creates a plan by assigning meals to days and categories.
     - `displayPlan()`: Displays the weekly plan.
     - `saveToFile()`: Exports ingredients required for the plan to a file.

## Requirements

- **Java 8** or higher
- **PostgreSQL** with a database named `meals_db`
- JDBC driver for PostgreSQL