package com.cafeteria.cafeteria_client.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.cafeteria.cafeteria_client.data.Category;
import com.cafeteria.cafeteria_client.data.Drink;
import com.cafeteria.cafeteria_client.data.Extra;
import com.cafeteria.cafeteria_client.data.Item;
import com.cafeteria.cafeteria_client.data.Meal;
import com.cafeteria.cafeteria_client.data.Order;

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

    private String serverIp = "";
    private Order theOrder;
    private List<Category> categories = new ArrayList<>();
//    private Bitmap bitmap;
    private Bitmap bitmap;
    private List<Drink> drinksList;
    private List<Meal> favorites;

//    public Bitmap getBitmap() {
//        Log.d("DATAHOLDER-getbitmap","bitmap: " + bitmap);
//        return bitmap;
//    }
//
//    public void setBitmap(Bitmap bitmap) {
//        this.bitmap = bitmap;
//    }
//
//    public void setImgByte(byte[] imgByte){
//        Bitmap b = BitmapFactory.decodeByteArray(imgByte,0,imgByte.length);
//        Bitmap scaled = Bitmap.createScaledBitmap(b,350,350,false);
//        this.bitmap = scaled;
//        Log.d("DATAHOLDER-setbyte","bitmap: " + bitmap);
//    }

    public List<Drink> getDrinksList() {
        return drinksList;
    }

    public void setDrinksList(List<Drink> drinksList) {
        this.drinksList = drinksList;
    }

    private static DataHolder ourInstance = new DataHolder();

    public static DataHolder getInstance() {
        return ourInstance;
    }

    private DataHolder() {

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

    public String getServerIp() {
            return serverIp;
        }

    public void setServerIp(String serverIp) {
            this.serverIp = serverIp;
        }

        public List<Meal> getFavorites() {
            return this.favorites;
        }

        public void setFavorites( List<Meal> favorites ) {
            this.favorites = favorites;
        }
    }
