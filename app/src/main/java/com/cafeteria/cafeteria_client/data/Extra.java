package com.cafeteria.cafeteria_client.data;

import java.io.Serializable;

/**
 * Created by Shira Elitzur on 29/09/2016.
 */

public class Extra implements Serializable {
    /**
     * The id of this extra (auto generated by db)
     */
    private int id;

    /**
     * The title of this extra.
     * 1-3 words that describes the extra
     */
    private String title;

    /**
     * The price of this extra.
     */
    private Double price;

    /**
     * Represent if this item is in stock
     */
    private boolean inStock;

    /**
     * Returns the id of this extra
     * @return the id of this extra
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id for this extra
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the title of this extra
     * @return the title of this extra
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title to this extra
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the price of this extra
     * @return the price of this extra
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Sets price to this extra
     * @param price
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

}
