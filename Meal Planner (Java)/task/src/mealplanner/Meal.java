package mealplanner;

import java.util.List;

public class Meal implements Comparable<Meal> {
    private String category;
    private String name;
    private List<String> ingredients;
    private int id;


    public Meal(String category, String name, List<String> ingredients) {
        this.category = category;
        this.name = name;
        this.ingredients = ingredients;
    }

    public Meal(String category, String name, List<String> ingredients, int id) {
        this.category = category;
        this.name = name;
        this.ingredients = ingredients;
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        String ingredientsString = "";
        for (String s : this.ingredients) {
            ingredientsString += "\n" + s ;
        }
        return "\nName: " + this.name + "\nIngredients: " + ingredientsString;
    }

    // Implement compareTo() to compare Meal objects based on their name
    @Override
    public int compareTo(Meal o) {
        return this.name.compareTo(o.name);
    }
}
