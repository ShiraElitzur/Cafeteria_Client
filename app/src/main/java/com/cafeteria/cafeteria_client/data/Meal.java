package com.cafeteria.cafeteria_client.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Shira Elitzur
 *
 * An instance of this class represent A meal that the cafeteria menu offers.
 * Meal can be a collection of items or one main item + extra items
 *
 */
public class Meal implements Serializable {

    /**
     * The id of the meal (auto generated by db)
     */
    private int id;

    /**
     * The main item of the meal (optional)
     * For example : Chicken
     */
    private Main main;

    /**
     * The title of this meal
     */
    private String title;

    /**
     * List of the items that the meal contains, the options for extras.
     * For example : Fries, Salad, Rice, Potatoes... from this
     * list the customer can choose *extraAmount*
     */
    private List<Extra> extras;

    /**
     * Extra amount means how many extras the customer can choose
     */
    private int extraAmount;

    /**
     * The price of this meal
     */
    private double price;

    /**
     * Returns the id of this meal
     * @return the id of this meal
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id for this meal
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the main item of this meal
     * @return the main item of this meal
     */
    public Main getMain() {
        return main;
    }

    /**
     * Sets main item for this meal
     * @param main
     */
    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * Returns the title of this meal
     * @return the title of this meal
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title for this meal
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the items that this meal contains or the extras for meal with main+extras
     * @return the items that this meal contains
     */
    public List<Extra> getExtras() {
        return extras;
    }

    /**
     * Sets the items list for this meal
     * @param extras
     */
    public void setExtras(List<Extra> extras) {
        this.extras = extras;
    }


    /**
     * Returns the price of this meal
     * @return the price of this meal
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets price for this meal
     * @param price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Returns the amount of extras that the customer can choose in this meal
     * @return the amount of extras
     */
    public int getExtraAmount() {
        return extraAmount;
    }

    /**
     * Sets the amount of extras that the customer can choose in this meal
     * @param extraAmount
     */
    public void setExtraAmount(int extraAmount) {
        this.extraAmount = extraAmount;
    }

}
