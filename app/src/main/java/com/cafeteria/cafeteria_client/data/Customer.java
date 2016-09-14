package com.cafeteria.cafeteria_client.data;

import com.cafeteria.cafeteria_client.data.Order;

import java.util.ArrayList;


/**
 * @author Shira Elitzur
 *
 * An instance of this class represent a customer (buyer)
 *
 */
public class Customer {

    /**
     * The id of the customer (auto generated by db)
     */
    private int id;

    /**
     * The first name of the customer
     */
    private String firstName;

    /**
     * The last name of the customer
     */
    private String lastName;

    /**
     * The email address of the customer
     */
    private String email;

    /**
     * The phone number of the customer
     */
    private String phoneNumber;

    /**
     * The password of this customer
     */
    private String password;

    /**
     * The orders of this customer
     */
    private ArrayList<Order> orders;


    /**
     * Returns the id of the customer
     * @return the id of the customer
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id for the customer
     * @param id an integer for the customer identification
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the first name of the customer
     * @return the first name of the customer
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets first name for the customer
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name of the customer
     * @return the last name of the customer
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets last name for the customer
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the email address of the customer
     * @return the email address of the customer
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email for the customer
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the phone number of the customer
     * @return the phone number of the customer
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets phone number for the customer
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the password of this customer
     * @return the password of this customer
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password for this customer
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the orders of this customer
     * @return the orders of this customer
     */
    public ArrayList<Order> getOrders() {
        return orders;
    }

    /**
     * Sets list of order to this customer
     * @param orders
     */
    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

}
