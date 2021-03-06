package com.cafeteria.cafeteria_client.utils;

import android.graphics.Bitmap;

import com.cafeteria.cafeteria_client.data.Cafeteria;
import com.cafeteria.cafeteria_client.data.Category;
import com.cafeteria.cafeteria_client.data.Drink;
import com.cafeteria.cafeteria_client.data.Extra;
import com.cafeteria.cafeteria_client.data.Item;
import com.cafeteria.cafeteria_client.data.Meal;
import com.cafeteria.cafeteria_client.data.Order;
import com.cafeteria.cafeteria_client.data.OrderedItem;
import com.cafeteria.cafeteria_client.data.OrderedMeal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anael on 17/09/16.
 */

/**
 * This class store all the item that chooses, and the meal.
 * for easy access from all screen. this class is singleton.
 */
public class DataHolder {

    private Cafeteria cafeteria;
    private Order theOrder;
    private List<Category> categories = new ArrayList<>();
    private Bitmap bitmap;
    private List<Drink> drinksList;
    private List<Meal> favoriteMeals;
    private List<Item> favoriteItems;
//    private List<Integer> readyOrders = new ArrayList<>();


    private static DataHolder ourInstance = new DataHolder();

    private DataHolder() {

    }

    public static DataHolder getInstance() {
        return ourInstance;
    }

    public List<Drink> getDrinksList() {
        return drinksList;
    }

    public void setDrinksList(List<Drink> drinksList) {
        this.drinksList = drinksList;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        checkItemsInventory();
    }

    public List<Category> getCategories() {
        return categories;
    }

    public Order getTheOrder() {
        return theOrder;
    }

    public void setTheOrder(Order theOrder) {
        this.theOrder = theOrder;
    }

    public void addItemToOrder(OrderedItem item){
        this.theOrder.getItems().add(item);
    }

    public void addMealToOrder(OrderedMeal meal){
        this.theOrder.getMeals().add(meal);
    }

    private void checkItemsInventory() {
        // Handle item not in stock
        List<Item> itemsToRemove = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {
            for (Item item : categories.get(i).getItems()) {
                if (item.isInStock() == false) {
                    itemsToRemove.add(item);
                }
            }
            categories.get(i).getItems().removeAll(itemsToRemove);
            itemsToRemove = new ArrayList<>();
        }

        // Handle main,serving and extras not in stock
        List<Meal> mealsToRemove = new ArrayList<>();
        List<Extra> extrasToRemove = new ArrayList<>();
        for (int i = 0; i < categories.size(); i++) {

            for (int j = 0; j < categories.get(i).getMeals().size(); j++) {

                if (categories.get(i).getMeals().get(j).getMain().isInStock() == false) {
                    mealsToRemove.add(categories.get(i).getMeals().get(j));
                } else if (categories.get(i).getMeals().get(j).getServing().isInStock() == false) {
                    mealsToRemove.add(categories.get(i).getMeals().get(j));
                } else {
                    for (Extra extra : categories.get(i).getMeals().get(j).getExtras()) {
                        if (extra.isInStock() == false) {
                            extrasToRemove.add(extra);
                        }
                    }
                }

                if (extrasToRemove.size() > 0) {
                    categories.get(i).getMeals().get(j).getExtras().removeAll(extrasToRemove);
                    extrasToRemove = new ArrayList<>();
                }
            }
            if (mealsToRemove.size() > 0) {
                categories.get(i).getMeals().removeAll(mealsToRemove);
                mealsToRemove = new ArrayList<>();
            }
        }
    }

    public List<Meal> getFavoriteMeals() {
        return this.favoriteMeals;
    }

    public void setFavoriteMeals( List<Meal> favoriteMeals ) {
        this.favoriteMeals = favoriteMeals;
    }


    public List<Item> getFavoriteItems() {
        return this.favoriteItems;
    }

    public void setFavoriteItems( List<Item> favoriteItems ) {
        this.favoriteItems = favoriteItems;
    }

    public Cafeteria getCafeteria() {
        return cafeteria;
    }

    public void setCafeteria(Cafeteria cafeteria) {
        this.cafeteria = cafeteria;
    }

    //
//    public List<Integer> getReadyOrders() {
//        return this.readyOrders;
//    }
//
//    public void addReadyOrder( int readyOrderNumber ) {
//        this.readyOrders.add(readyOrderNumber);
//    }
//
//    public void removeReadyOrder ( int readyOrderNumber ) {
//        this.readyOrders.remove(Integer.valueOf(readyOrderNumber));
//    }

}
