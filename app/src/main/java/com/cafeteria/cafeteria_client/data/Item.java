package com.cafeteria.cafeteria_client.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shira Elitzur on 08/09/2016.
 */
public class Item implements Serializable {

    /**
     * The id of the item
     */
    private int itemId;
    /**
     * The name of the item
     */
    private String itemName;
    /**
     * Does this item has a meal
     */
    private boolean isMeal;
    /**
     * A list of the meals contains the item
     */
    private List<Meal> meals = new ArrayList<>();

    /**
     * Default Constructor, initialize an Item object
     */
    public Item(){ }

    /**
     * This constructor initializes a Meal object, and sets the meal name
     * @param itemName the name of the item
     */
    public Item(String itemName){
        setItemName(itemName);
    }

    /**
     * Returns the id of the item
     * @return the id of the item
     */
    public int getItemId() {
        return itemId;
    }

    /**
     * Sets the id of the item
     * @param itemId the id of the item
     */
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    /**
     * Returns the item name
     * @return the item name
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * Sets the item name
     * @param itemName the item name
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * Returns if this item has a meal
     * @return if this item has a meal
     */
    public boolean isMeal() {
        return isMeal;
    }

    /**
     * Sets if this item has a meal
     * @param meal is this item has a meal
     */
    public void setMeal(boolean meal) {
        isMeal = meal;
    }

    /**
     * Returns a list of meals containing this item
     * @return list of meals
     */
    public List<Meal> getMeals() {
        return meals;
    }

    /**
     * Sets a list of meals containing this item
     * @param meals list of meals
     */
    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

    /**
     * This method adds a meal to the meals list
     * @param meal a meal containing this item
     */
    public void addMeal(Meal meal){
        meals.add(meal);
    }
}
