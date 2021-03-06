package com.cafeteria.cafeteria_client.data;

import java.io.Serializable;

/**
 * Created by ashom on 16-Nov-16.
 */

public class ServingForm implements Serializable {

    /**
     * The id of this serving form (auto generated by db)
     */
    private int id;

    /**
     * The title of this serving form.
     */
    private String title;

    /**
     * Represent if this item is in stock
     */
    private boolean inStock = true;

    /**
     * Returns the id of this serving form
     * @return the id of this serving form
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id for this serving form
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the title of this serving form
     * @return the title of this serving form
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title to this serving form
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    @Override
    public String toString() {
        return "ServingForm{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", inStock=" + inStock +
                '}';
    }
}
