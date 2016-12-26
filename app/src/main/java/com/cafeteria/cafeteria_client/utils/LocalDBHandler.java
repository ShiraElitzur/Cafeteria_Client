package com.cafeteria.cafeteria_client.utils;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cafeteria.cafeteria_client.data.Customer;
import com.cafeteria.cafeteria_client.data.Drink;
import com.cafeteria.cafeteria_client.data.Extra;
import com.cafeteria.cafeteria_client.data.Meal;
import com.cafeteria.cafeteria_client.data.Order;
import com.cafeteria.cafeteria_client.data.OrderedItem;
import com.cafeteria.cafeteria_client.data.OrderedMeal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Shira Elitzur on 08/10/2016.
 */

public class LocalDBHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "cafeteria_local_db";
    private static final int DATABASE_VERSION = 2;

    // Meals table name and columns
    private static final String MEALS_TABLE_NAME = "meals";
    private static final String ID_COL = "Id";
    private static final String TITLE_COL = "Title";
    private static final String EXSTRAS_COL = "Extras";
    private static final String DRINK_COL = "Drink";
    private static final String PRICE_COL = "Price";
    private static final String ORDER_ID_COL = "OrderId";
    private static final String USER_ID_COL = "UserId";
    private static final String CAFETERIA_ID_COL = "CafeteriaId";

    // Orders table
    private static final String ORDERS_TABLE_NAME = "orders";
    private static final String DATE_COL = "Date";
    private static final String ORDERS_TABLE_CREATE =
            "CREATE TABLE " + ORDERS_TABLE_NAME + " (" +
                    ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DATE_COL + " TEXT, " +
                    USER_ID_COL + " INTEGER, " +
                    CAFETERIA_ID_COL + " INTEGER, " +
                    PRICE_COL + " REAL );";

    // Meals table create statement
    private static final String MEALS_TABLE_CREATE =
            "CREATE TABLE " + MEALS_TABLE_NAME + " (" +
                ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE_COL + " TEXT, " +
                EXSTRAS_COL + " TEXT, " +
                DRINK_COL + " TEXT, " +
                ORDER_ID_COL + " INTEGER, " +
                PRICE_COL + " REAL, " +
                "FOREIGN KEY ("+ORDER_ID_COL+") REFERENCES "+ORDERS_TABLE_NAME+"("+ID_COL+"));";

    // Items table name and columns
    private static final String ITEMS_TABLE_NAME = "items";
    // Items table create statement
    private static final String ITEMS_TABLE_CREATE =
            "CREATE TABLE " + ITEMS_TABLE_NAME + " (" +
                ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE_COL + " TEXT, " +
                ORDER_ID_COL + " INTEGER, " +
                PRICE_COL + " REAL, " +
                "FOREIGN KEY ("+ORDER_ID_COL+") REFERENCES "+ORDERS_TABLE_NAME+"("+ID_COL+"));";

//    // Order_meals table
//    private static final String ORDER_MEAL_TABLE_NAME = "order_meal";
//    private static final String ORDER_ID_COL = "OrderId";
//    private static final String MEAL_ID_COL = "MealId";
//    private static final String ORDER_MEAL_TABLE_CREATE =
//            "CREATE TABLE " + ORDER_MEAL_TABLE_NAME + " (" +
//                ORDER_ID_COL + " INTEGER, " +
//                MEAL_ID_COL + " INTEGER, " +
//                "FOREIGN KEY ("+ORDER_ID_COL+") REFERENCES "+ORDERS_TABLE_NAME+"("+ID_COL+")," +
//                "FOREIGN KEY ("+MEAL_ID_COL+") REFERENCES "+MEALS_TABLE_NAME+"("+ID_COL+") );";
//
//    // Order_item table
//    private static final String ORDER_ITEM_TABLE_NAME = "order_item";
//    private static final String ITEM_ID_COL = "ItemId";
//    private static final String ORDER_ITEM_TABLE_CREATE =
//            "CREATE TABLE " + ORDER_ITEM_TABLE_NAME + " (" +
//                ORDER_ID_COL + " INTEGER, " +
//                ITEM_ID_COL + " INTEGER, " +
//                "FOREIGN KEY ("+ORDER_ID_COL+") REFERENCES "+ORDERS_TABLE_NAME+"("+ID_COL+")," +
//                "FOREIGN KEY ("+ITEM_ID_COL+") REFERENCES "+ITEMS_TABLE_NAME+"("+ID_COL+") );";

    public LocalDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MEALS_TABLE_CREATE);
        db.execSQL(ITEMS_TABLE_CREATE);
        db.execSQL(ORDERS_TABLE_CREATE);
//        db.execSQL(ORDER_MEAL_TABLE_CREATE);
//        db.execSQL(ORDER_ITEM_TABLE_CREATE);
        Log.e("SQLITE","OnCreate DB");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.e("SQLITE","DB Upgrade from v."+i+" to v."+i1);
        db.delete(MEALS_TABLE_NAME,null,null);
        db.delete(ITEMS_TABLE_NAME,null,null);
        db.delete(ORDERS_TABLE_NAME,null,null);
//        db.delete(ORDER_MEAL_TABLE_NAME,null,null);
//        db.delete(ORDER_ITEM_TABLE_NAME,null,null);
        onCreate(db);
    }

    public int countOrders( int userId ) {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT COUNT(*) FROM " + ORDERS_TABLE_NAME +" WHERE " + USER_ID_COL + " = "+userId +
                " AND "+ CAFETERIA_ID_COL +" = " + DataHolder.getInstance().getCafeteria().getId();
        Cursor cursor = db.rawQuery(query,null);
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0); //The 0 is the column index, we only have 1 column, so the index is 0
        }
        cursor.close();
        return count;
    }

    public void deleteOldestOrder( int userId ) {

        SQLiteDatabase db = getReadableDatabase();
        String delete = "DELETE FROM "+ ORDERS_TABLE_NAME + " WHERE " + USER_ID_COL + " = " + userId +
                " AND "+ CAFETERIA_ID_COL +" = " + DataHolder.getInstance().getCafeteria().getId()+ " AND "+
                ID_COL + " IN (SELECT ID FROM "+ ORDERS_TABLE_NAME + " ORDER BY " + DATE_COL + " ASC LIMIT 1)";
        db.execSQL(delete);
    }

    public void insertOrder(int userId, Order order) {
        SQLiteDatabase db = getWritableDatabase();
        long orderId;
        long mealId;
        long itemId;
        int ordersInDb = countOrders(userId);
        Log.e("DEBUG","orders in db for user - " + ordersInDb);
        if( ordersInDb >= ApplicationConstant.SQLITE_LIMIT ) {
            deleteOldestOrder(userId);
        }
        Log.e("DEBUG","orders in db for user - " + ordersInDb);

        // insert new record to the orders table
        ContentValues values = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat(ApplicationConstant.DATE_TIME_SQLITE_FORMAT);
        values.put(DATE_COL, sdf.format(order.getDate()));
        values.put(USER_ID_COL, userId);
        values.put(CAFETERIA_ID_COL, DataHolder.getInstance().getCafeteria().getId());
        values.put(PRICE_COL, order.getPayment());
        db.insert(ORDERS_TABLE_NAME, null, values);

        // retrieve the id of the new order
        orderId = getLastId(ORDERS_TABLE_NAME);
        // loop through the meals of this order
        for(OrderedMeal meal : order.getMeals()){
            String extras = "";
            if (meal.getChosenExtras() != null) {
                for (Extra extra : meal.getChosenExtras()) {
                    extras += extra.getTitle() + " ,";
                }
            }
            if (!extras.equals("")){
                extras = extras.substring(0, extras.length()-1);
            }
            // insert new record to the meals table
            values = new ContentValues();
            values.put(TITLE_COL, meal.getTitle());
            values.put(EXSTRAS_COL, extras);
            if(meal.getChosenDrink() != null) {
                values.put(DRINK_COL, meal.getChosenDrink().getTitle());
            }
            values.put(PRICE_COL, meal.getTotalPrice());
            Log.e("SQLITE","Insert Meal - " + meal.getTitle() + " price - "+meal.getTotalPrice());
            values.put(ORDER_ID_COL, orderId);
            db.insert(MEALS_TABLE_NAME, null, values);
//            mealId = getLastId(MEALS_TABLE_NAME);
//
//            // insert new record to order_meal table
//            values = new ContentValues();
//            values.put(ORDER_ID_COL, orderId);
//            values.put(MEAL_ID_COL, mealId);
//            db.insert(ORDER_MEAL_TABLE_NAME, null, values);
        }

        // loop through the items of this order
        for(OrderedItem item : order.getItems()){
            // insert new record to the items table
            values = new ContentValues();
            values.put(TITLE_COL, item.getParentItem().getTitle());
            values.put(PRICE_COL, item.getParentItem().getPrice());
            Log.e("SQLITE","Insert Item - " + item.getParentItem().getTitle() + " price - "+item.getParentItem().getPrice());
            values.put(ORDER_ID_COL, orderId);
            db.insert(ITEMS_TABLE_NAME, null, values);
//            itemId = getLastId(ITEMS_TABLE_NAME);
//
//            // insert new record to order_item table
//            values = new ContentValues();
//            values.put(ORDER_ID_COL, orderId);
//            values.put(ITEM_ID_COL, itemId);
//            db.insert(ORDER_ITEM_TABLE_NAME, null, values);
        }
        db.close();
    }

    private long getLastId( String tableName ) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT Id FROM " + tableName + " ORDER BY Id DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query,null);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
        }
        cursor.close();
        return -1;
    }

    public List<Order> selectOrders( int userId ) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Order> orderList = new ArrayList<Order>();
        String selectQuery = "SELECT * FROM " + ORDERS_TABLE_NAME + "WHERE "+USER_ID_COL +"="+userId+
                " AND "+ CAFETERIA_ID_COL +" = " + DataHolder.getInstance().getCafeteria().getId()+ " ORDER BY " + DATE_COL + " DESC";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                SimpleDateFormat sdf = new SimpleDateFormat(ApplicationConstant.DATE_TIME_SQLITE_FORMAT);
                try {
                    order.setDate(sdf.parse(cursor.getString(1)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                order.setId(cursor.getInt(0));
                // payment - needs to fix that
                order.setPayment(cursor.getDouble(2));

                List<OrderedMeal> meals = new ArrayList<>();
                OrderedMeal meal;
                Meal parentMeal;
                Cursor mealCursor = db.rawQuery("SELECT * FROM " +MEALS_TABLE_NAME+ " WHERE " +ORDER_ID_COL+ " = "+order.getId(),null);
                while (mealCursor.moveToNext()) {
                    meal = new OrderedMeal();
                    meal.setTitle(mealCursor.getString(1));
                    meal.setComment(mealCursor.getString(2)); // chosen extras on one string
                    Drink drink = new Drink();
                    drink.setTitle(mealCursor.getString(3));
                    meal.setChosenDrink(drink);
                    meal.setTotalPrice(mealCursor.getDouble(5));
                    Log.e("SQLITE","Select Meal - " + meal.getTitle() + " price - "+meal.getTotalPrice());
                    meals.add(meal);
                }
                mealCursor.close();
                order.setMeals(meals);

                List<OrderedItem> items = new ArrayList<>();
                OrderedItem item;
                Cursor itemCursor = db.rawQuery("SELECT * FROM " +ITEMS_TABLE_NAME+ " WHERE " +ORDER_ID_COL+ " = "+order.getId(),null);
                while (itemCursor.moveToNext()) {
                    item = new OrderedItem();
                    item.getParentItem().setTitle(itemCursor.getString(1));
                    item.getParentItem().setPrice(itemCursor.getDouble(3));
                    Log.e("SQLITE","Select Item - " + item.getParentItem().getTitle() + " price - "+item.getParentItem().getPrice());
                    items.add(item);
                }
                itemCursor.close();
                order.setItems(items);

                orderList.add(order);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return orderList;
    }

    public double getPaymentForMonth( int month, int userId ) {
        double payment = 0;
        SQLiteDatabase db = getReadableDatabase();
        Calendar first = Calendar.getInstance();
        first.set(Calendar.MONTH, month);
        first.set(Calendar.DAY_OF_MONTH,1);
        Calendar last = Calendar.getInstance();
        last.set(Calendar.MONTH, month);
        last.set(Calendar.DAY_OF_MONTH,30);
        //c.set(Calendar.DAY_OF_MONTH, 1);
        month++;
        String monthString = month+"";
        Log.e("DEBUG","order for month - " + month);

        SimpleDateFormat toDate = new SimpleDateFormat(ApplicationConstant.DATE_TIME_SQLITE_FORMAT);
        String dateStartTxt = toDate.format(first.getTime());
        String dateEndTxt = toDate.format(last.getTime());

//        Log.e("DEBUG","orders from - " + dateStartTxt + " to - " +dateEndTxt);
        String query = "SELECT SUM("+PRICE_COL+") FROM " + ORDERS_TABLE_NAME +" WHERE " + USER_ID_COL + " = "+userId +
                " AND "+ CAFETERIA_ID_COL +" = " + DataHolder.getInstance().getCafeteria().getId()+
                " AND "+ DATE_COL + " BETWEEN " + "'" + dateStartTxt + "'" + " AND "
                + "'" + dateEndTxt + "'";
        Cursor cursor = db.rawQuery(query,null);
        if (cursor != null && cursor.moveToFirst()) {
            payment = cursor.getDouble(0); //The 0 is the column index, we only have 1 column, so the index is 0
        }
        cursor.close();
        Log.e("DEBUG","payment for month - " + payment);
        return payment;
    }

    public List<Order> selectOrdersByDate(int userId, Calendar dateStart, Calendar dateEnd) {

        dateStart.add(Calendar.DATE,-1);
        dateEnd.add(Calendar.DATE,1);

        SimpleDateFormat toDate = new SimpleDateFormat(ApplicationConstant.DATE_TIME_SQLITE_FORMAT);
        String dateStartTxt = toDate.format(dateStart.getTime());
        String dateEndTxt = toDate.format(dateEnd.getTime());

        SQLiteDatabase db = this.getReadableDatabase();
        List<Order> orderList = new ArrayList<Order>();
//        String selectQuery = "SELECT * FROM " + ORDERS_TABLE_NAME  + " WHERE " + DATE_COL + " BETWEEN " + "'" + dateStartTxt + "'" + " AND "
//                + "'" + dateEndTxt + "'";

        String selectQuery = "SELECT * FROM " + ORDERS_TABLE_NAME  + " WHERE " +USER_ID_COL+ " = "+userId+
                " AND "+ CAFETERIA_ID_COL +" = " + DataHolder.getInstance().getCafeteria().getId()+  " AND "+ DATE_COL + " BETWEEN " + "'" + dateStartTxt + "'" + " AND "
                 + "'" + dateEndTxt + "'";
        Log.d("Query","orders size: " + selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                SimpleDateFormat sdf = new SimpleDateFormat(ApplicationConstant.DATE_TIME_SQLITE_FORMAT);
                try {
                    order.setDate(sdf.parse(cursor.getString(1)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                order.setId(cursor.getInt(0));
                // payment - needs to fix that
                order.setPayment(cursor.getDouble(2));

                List<OrderedMeal> meals = new ArrayList<>();
                OrderedMeal meal;
                Meal parentMeal;
                Cursor mealCursor = db.rawQuery("SELECT * FROM " +MEALS_TABLE_NAME+ " WHERE " +ORDER_ID_COL+ " = "+order.getId(),null);
                while (mealCursor.moveToNext()) {
                    meal = new OrderedMeal();
                    meal.setTitle(mealCursor.getString(1));
                    meal.setComment(mealCursor.getString(2)); // chosen extras on one string
                    Drink drink = new Drink();
                    drink.setTitle(mealCursor.getString(3));
                    meal.setChosenDrink(drink);
                    meal.setTotalPrice(mealCursor.getDouble(5));
                    Log.e("SQLITE","Select Meal - " + meal.getTitle() + " price - "+meal.getTotalPrice());
                    meals.add(meal);
                }
                mealCursor.close();
                order.setMeals(meals);

                List<OrderedItem> items = new ArrayList<>();
                OrderedItem item;
                Cursor itemCursor = db.rawQuery("SELECT * FROM " +ITEMS_TABLE_NAME+ " WHERE " +ORDER_ID_COL+ " = "+order.getId(),null);
                while (itemCursor.moveToNext()) {
                    item = new OrderedItem();
                    item.getParentItem().setTitle(itemCursor.getString(1));
                    item.getParentItem().setPrice(itemCursor.getDouble(3));
                    Log.e("SQLITE","Select Item - " + item.getParentItem().getTitle() + " price - "+item.getParentItem().getPrice());
                    items.add(item);
                }
                itemCursor.close();
                order.setItems(items);

                orderList.add(order);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return orderList;
    }

    public List<Order> selectLastOrders( int userId ) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Order> orderList = new ArrayList<Order>();

        String selectQuery = "SELECT * FROM " + ORDERS_TABLE_NAME + " WHERE "+USER_ID_COL +"="+userId+
                " AND "+ CAFETERIA_ID_COL +" = " + DataHolder.getInstance().getCafeteria().getId()+ " ORDER BY " + DATE_COL + " DESC LIMIT 30";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                SimpleDateFormat sdf = new SimpleDateFormat(ApplicationConstant.DATE_TIME_SQLITE_FORMAT);
                try {
                    order.setDate(sdf.parse(cursor.getString(1)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                order.setId(cursor.getInt(0));
                // payment - needs to fix that
                order.setPayment(cursor.getDouble(2));

                List<OrderedMeal> meals = new ArrayList<>();
                OrderedMeal meal;
                Meal parentMeal;
                Cursor mealCursor = db.rawQuery("SELECT * FROM " +MEALS_TABLE_NAME+ " WHERE " +ORDER_ID_COL+ " = "+order.getId(),null);
                while (mealCursor.moveToNext()) {
                    meal = new OrderedMeal();
                    meal.setTitle(mealCursor.getString(1));
                    meal.setComment(mealCursor.getString(2)); // chosen extras on one string
                    Drink drink = new Drink();
                    drink.setTitle(mealCursor.getString(3));
                    meal.setChosenDrink(drink);
                    meal.setTotalPrice(mealCursor.getDouble(5));

                    meals.add(meal);
                }
                mealCursor.close();
                order.setMeals(meals);

                List<OrderedItem> items = new ArrayList<>();
                OrderedItem item;
                Cursor itemCursor = db.rawQuery("SELECT * FROM " +ITEMS_TABLE_NAME+ " WHERE " +ORDER_ID_COL+ " = "+order.getId(),null);
                while (itemCursor.moveToNext()) {
                    item = new OrderedItem();
                    item.getParentItem().setTitle(itemCursor.getString(1));
                    item.getParentItem().setPrice(itemCursor.getDouble(3));
                    items.add(item);
                }
                itemCursor.close();
                order.setItems(items);

                orderList.add(order);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return orderList;
    }

}
