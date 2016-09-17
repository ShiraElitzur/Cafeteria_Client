package com.cafeteria.cafeteria_client.interfaces;

import java.io.Serializable;

/**
 * Created by Shira Elitzur on 15/09/2016.
 */
public interface OrderItem  extends Serializable{
    public String getTitle();
    public double getPrice();
}
