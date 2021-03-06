package com.cafeteria.cafeteria_client.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Shira Elitzur on 08/09/2016.
 * This class represent a food category in the cafeteria menu.
 */
public class Category implements Serializable{

    /**
     * The id of this category (auto generated by db)
     */
    private int id;

    /**
     * The title of this category
     */
    private String title;

    private String description;

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

    /**
     * The items of this category
     */
    private List<Item> items;

    /**
     * The meals of this category
     */
    private List<Meal> meals;

    /**
     * The icon of this category
     */
    private byte[] icon;

    /**
     * The constructor of Category object sets its title as the given parameter
     * @param title
     */
    public Category( String title ) {
        this.setTitle(title);
    }

    /**
     * Constructor without parameters - a must in JPA Entity
     */
    public Category() {

    }

    /**
     * Returns the id of this category
     * @return the id of this category
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id for this category
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the title of this category
     * @return the title of this category
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title for this category
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the items of this category
     * @return the items of this category
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * Sets items list to this category
     * @param items
     */
    public void setItems(List<Item> items) {
        this.items = items;
    }

    /**
     * Returns the meals of this category
     * @return
     */
    public List<Meal> getMeals() {
        return meals;
    }

    /**
     * Sets meals list to this category
     * @param meals
     */
    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", items=" + items +
                ", meals=" + meals +
                ", icon=" + Arrays.toString(icon) +
                '}';
    }
}
