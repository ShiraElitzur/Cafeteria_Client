package com.cafeteria.cafeteria_client.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shira Elitzur on 08/09/2016.
 */
public class Meal implements Serializable {

    private int mealId;
    private String mealName;
    private List<Item> items = new ArrayList<>();

    /**
     * Default Constructor, initialize a Meal object
     */
    public Meal(){}

    /**
     * This constructor initializes a Meal object, and sets the meal name
     *
     * @param mealName the name of the meal
     */
    public Meal(String mealName){
        setMealName(mealName);
    }

    /**
     * Returns the id of the meal
     * @return the id of the meal
     */
    public int getMealId() {
        return mealId;
    }

    /**
     * Sets the id of the meal
     * @param mealId the id of the meal
     */
    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    /**
     * Returns the name of the meal
     * @return the name of the meal
     */
    public String getMealName() {
        return mealName;
    }

    /**
     * Sets the name of the meal
     * @param mealName the name of the meal
     */
    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    /**
     * Returns a list containing items on this meal
     * @return list of items
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * Sets a list containing items on this meal
     * @param items list of items
     */
    public void setItems(List<Item> items) {
        this.items = items;
    }

    /**
     * This method add an item to the items list.
     * @param item item of the meal
     */
    public void addItem(Item item){
        items.add(item);
    }
}
