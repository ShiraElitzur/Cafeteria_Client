package com.cafeteria.cafeteria_client.data;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by anael on 17/09/16.
 */

/**
 * This class store all the item that chooses, and the meal.
 * for easy access from all screen. this class is singleton.
 */
public class DataHolder{

    private Order theOrder;
//    private List<OrderedMeal> orderedMeals = new ArrayList<>();
//    private List<Item> orderedItems = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

    public List<Drink> getDrinksList() {
        return drinksList;
    }

    public void setDrinksList(List<Drink> drinksList) {
        this.drinksList = drinksList;
    }

    private List<Drink> drinksList;


    private static DataHolder ourInstance = new DataHolder();

    public static DataHolder getInstance() {
        return ourInstance;
    }

    private DataHolder() {

    }

//
//    public void addOrderdMeal(OrderedMeal meal){
//        orderedMeals.add(meal);
//    }
//
//    public void addOrderdItem(Item item){
//        orderedItems.add(item);
//    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
//
//    public List<Item> getOrderedItems(){
//        return orderedItems;
//    }
//
//    public List<OrderedMeal> getOrderedMeals(){
//        return orderedMeals;
//    }

    public List<Category> getCategories() {
        return categories;
    }

    public Order getTheOrder() {
        return theOrder;
    }

    public void setTheOrder(Order theOrder) {
        this.theOrder = theOrder;
    }
}
