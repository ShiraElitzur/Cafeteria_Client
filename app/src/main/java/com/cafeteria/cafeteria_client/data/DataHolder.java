package com.cafeteria.cafeteria_client.data;

import com.cafeteria.cafeteria_client.interfaces.OrderItem;

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

    private List<OrderItem> items =  new ArrayList<>();
    private List<OrderedMeal> orderedMeals = new ArrayList<>();
    private List<Item> orderedItems = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();


    private static DataHolder ourInstance = new DataHolder();

    public static DataHolder getInstance() {
        return ourInstance;
    }

//    private DataHolder() {
//        //init for OrderActivity
//        Item item = new Item();
//        item.setTitle("במבה");
//        item.setPrice(4.5);
//        orderedItems.add(item);
//        item = new Item();
//        item.setTitle("פחית קולה");
//        item.setPrice(5.0);
//        orderedItems.add(item);
//        item = new Item();
//        item.setTitle("פסק זמן מיני");
//        item.setPrice(4.0);
//        orderedItems.add(item);
//        item = new Item();
//        item.setTitle("קוראסון");
//        item.setPrice(7.0);
//        orderedItems.add(item);
//
//        OrderedMeal orderedMeal = new OrderedMeal();
//        Meal parentMeal = new Meal();
//        parentMeal.setTitle("שניצל בצלחת");
//        orderedMeal.setParentMeal(parentMeal);
//        List<Item> mealItems = new ArrayList<Item>();
//        item = new Item();
//        item.setTitle("אורז");
//        mealItems.add(item);
//        item = new Item();
//        item.setTitle("אפונה");
//        mealItems.add(item);
//        orderedMeal.setChosenExtras(mealItems);
//        orderedMeals.add(orderedMeal);
//
//        orderedMeal = new OrderedMeal();
//        parentMeal = new Meal();
//        parentMeal.setTitle("פרגית בבגט");
//        orderedMeal.setParentMeal(parentMeal);
//        mealItems = new ArrayList<Item>();
//        item = new Item();
//        item.setTitle("חומוס");
//        mealItems.add(item);
//        item = new Item();
//        item.setTitle("ציפס");
//        mealItems.add(item);
//        orderedMeal.setChosenExtras(mealItems);
//        orderedMeals.add(orderedMeal);
//
//        items.addAll(orderedMeals);
//        items.addAll(orderedItems);
//
//        //init for CategoryFragment
//        Category cat = new Category();
//        cat.setTitle("בשרי");
//        cat.setDescription("ארוחות בשריות מושקעות");
//        // init items in category
//        List<Item> items = new ArrayList<>();
//        List<Meal> meals = new ArrayList<>();
//        Meal meal = new Meal();
//        Main main = new Main();
//        item = new Item();
//        List<Drink> drinks = new ArrayList<>();
//        Drink drink = new Drink();
//        drink.setTitle("קוקה קולה");
//        drink.setPrice(7.5);
//        drinks.add(drink);
//        drink = new Drink();
//        drink.setTitle("פאנטה");
//        drink.setPrice(7.5);
//        drinks.add(drink);
//        drink = new Drink();
//        drink.setTitle("ענבים");
//        drink.setPrice(6.5);
//        drinks.add(drink);
//        drink = new Drink();
//        drink.setTitle("ספרייט");
//        drink.setPrice(7.5);
//        drinks.add(drink);
//
//        List<Item> extras = new ArrayList<Item>();
//        Item extra = new Item();
//        extra.setTitle("אורז");
//        extra.setPrice(5);
//        extras.add(extra);
//        extra = new Item();
//        extra.setTitle("צ'יפס");
//        extra.setPrice(5);
//        extras.add(extra);
//        extra = new Item();
//        extra.setTitle("ירקות");
//        extra.setPrice(5);
//        extras.add(extra);
//
//        item.setTitle("שניצל");
//        item.setStandAlone(false);
//        meal.setTitle("שניצל בצלחת");
//        meal.setExtraAmount(2);
//        meal.setExtras(extras);
//        meal.setDrinkOptions(drinks);
//        meal.setPrice(35.66657);
//        main.setTitle("שניצל");
//        meal.setMain(main);
//        meals.add(meal);
//        meal = new Meal();
//        meal.setTitle("שניצל בבאגט");
//        main = new Main();
//        main.setTitle("שניצל");
//        meal.setMain(main);
//        meals.add(meal);
//        meal = new Meal();
//        meal.setTitle("שניצל בפיתה");
//        main = new Main();
//        main.setTitle("שניצל");
//        meal.setMain(main);
//        meals.add(meal);
//        items.add(item);
//
//        item = new Item();
//        item.setTitle("המבורגר");
//        item.setStandAlone(false);
//        meal = new Meal();
//        meal.setTitle("המבורגר בצלחת");
//        main = new Main();
//        main.setTitle("המבורגר");
//        meal.setMain(main);
//        meals.add(meal);
//        meal = new Meal();
//        meal.setTitle("המבורגר בבאגט");
//        main = new Main();
//        main.setTitle("המבורגר");
//        meal.setMain(main);
//        meals.add(meal);
//        meal = new Meal();
//        meal.setTitle("המבורגר בפיתה");
//        main = new Main();
//        main.setTitle("המבורגר");
//        meal.setMain(main);
//        meals.add(meal);
//        items.add(item);
//
//        item = new Item();
//        item.setTitle("פרגית");
//        item.setStandAlone(false);
//        meal = new Meal();
//        meal.setTitle("פרגית בצלחת");
//        main = new Main();
//        main.setTitle("פרגית");
//        meal.setMain(main);
//        meals.add(meal);
//        meal = new Meal();
//        meal.setTitle("פרגית בבאגט");
//        main = new Main();
//        main.setTitle("פרגית");
//        meal.setMain(main);
//        meals.add(meal);
//        meal = new Meal();
//        meal.setTitle("פרגית בפיתה");
//        main = new Main();
//        main.setTitle("פרגית");
//        meal.setMain(main);
//        meals.add(meal);
//        items.add(item);
//
//        item = new Item();
//        item.setTitle("לאפה שווארמה");
//        item.setStandAlone(true);
//        items.add(item);
//
//        // set items and meals to category
//        cat.setItems(items);
//        cat.setMeals(meals);
//        categories.add(cat);
//
//        items = new ArrayList<Item>(drinks);
//
//
//        cat = new Category();
//        cat.setTitle("חלבי");
//        cat.setDescription("ארוחות חלביות מדהימות");
//        categories.add(cat);
//        cat = new Category();
//        cat.setTitle("סלטים");
//        cat.setDescription("מבחר סלטים בהרכבה");
//        categories.add(cat);
//        cat = new Category();
//        cat.setTitle("שתיה חמה");
//        cat.setDescription("כל סוגי השתייה החמה");
//        categories.add(cat);
//        cat = new Category();
//        cat.setTitle("שתיה קרה");
//        cat.setDescription("כל סוגי השתיה הקרה");
//        cat.setItems(items);
//        categories.add(cat);
//        cat = new Category();
//        cat.setTitle("מאפים");
//        cat.setDescription("כל סוגי המאפים");
//        categories.add(cat);
//        cat = new Category();
//        cat.setTitle("חטיפים");
//        cat.setDescription("במבה,ביסלי,פסק זמן...");
//        categories.add(cat);
//
//    }

    private DataHolder() {
        //init for OrderActivity
        Item item = new Item();
        item.setTitle("במבה");
        item.setPrice(4.5);
        orderedItems.add(item);
        item = new Item();
        item.setTitle("פחית קולה");
        item.setPrice(5.0);
        orderedItems.add(item);
        item = new Item();
        item.setTitle("פסק זמן מיני");
        item.setPrice(4.0);
        orderedItems.add(item);
        item = new Item();
        item.setTitle("קוראסון");
        item.setPrice(7.0);
        orderedItems.add(item);

        OrderedMeal orderedMeal = new OrderedMeal();
        Meal parentMeal = new Meal();
        parentMeal.setTitle("שניצל בצלחת");
        orderedMeal.setParentMeal(parentMeal);
        List<Item> mealItems = new ArrayList<Item>();
        item = new Item();
        item.setTitle("אורז");
        mealItems.add(item);
        item = new Item();
        item.setTitle("אפונה");
        mealItems.add(item);
        orderedMeal.setChosenExtras(mealItems);
        orderedMeals.add(orderedMeal);

        orderedMeal = new OrderedMeal();
        parentMeal = new Meal();
        parentMeal.setTitle("פרגית בבגט");
        orderedMeal.setParentMeal(parentMeal);
        mealItems = new ArrayList<Item>();
        item = new Item();
        item.setTitle("חומוס");
        mealItems.add(item);
        item = new Item();
        item.setTitle("ציפס");
        mealItems.add(item);
        orderedMeal.setChosenExtras(mealItems);
        orderedMeals.add(orderedMeal);

        items.addAll(orderedMeals);
        items.addAll(orderedItems);

        //init for CategoryFragment
        item = new Item();
        Category cat = new Category();
        cat.setTitle("בשרי");
        cat.setDescription("ארוחות בשריות מושקעות");
        // init items in category
        List<Item> items = new ArrayList<>();
        List<Meal> meals = new ArrayList<>();
        Meal meal = new Meal();
        Main main = new Main();
        item = new Item();
        List<Drink> drinks = new ArrayList<>();
        Drink drink = new Drink();
        drink.setTitle("קוקה קולה");
        drink.setPrice(7.5);
        drinks.add(drink);
        drink = new Drink();
        drink.setTitle("פאנטה");
        drink.setPrice(7.5);
        drinks.add(drink);
        drink = new Drink();
        drink.setTitle("ענבים");
        drink.setPrice(6.5);
        drinks.add(drink);
        drink = new Drink();
        drink.setTitle("ספרייט");
        drink.setPrice(7.5);
        drinks.add(drink);

        List<Item> extras = new ArrayList<Item>();
        Item extra = new Item();
        extra.setTitle("אורז");
        extra.setPrice(5);
        extras.add(extra);
        extra = new Item();
        extra.setTitle("צ'יפס");
        extra.setPrice(5);
        extras.add(extra);
        extra = new Item();
        extra.setTitle("ירקות");
        extra.setPrice(5);
        extras.add(extra);

        item.setTitle("שניצל");
        item.setStandAlone(false);
        meal.setTitle("שניצל בצלחת");
        meal.setExtraAmount(2);
        meal.setExtras(extras);
        meal.setDrinkOptions(drinks);
        meal.setPrice(35.66657);
        main.setTitle("שניצל");
        meal.setMain(main);
        meals.add(meal);
        meal = new Meal();
        meal.setTitle("שניצל בבאגט");
        main = new Main();
        meal.setExtraAmount(2);
        meal.setExtras(extras);
        meal.setDrinkOptions(drinks);
        meal.setPrice(35.66657);
        main.setTitle("שניצל");
        meal.setMain(main);
        meals.add(meal);
        meal = new Meal();
        meal.setTitle("שניצל בפיתה");
        main = new Main();
        meal.setExtraAmount(2);
        meal.setExtras(extras);
        meal.setDrinkOptions(drinks);
        meal.setPrice(35.66657);
        main.setTitle("שניצל");
        meal.setMain(main);
        meals.add(meal);
        items.add(item);

        item = new Item();
        item.setTitle("המבורגר");
        item.setStandAlone(false);
        meal = new Meal();
        meal.setTitle("המבורגר בצלחת");
        main = new Main();
        meal.setExtraAmount(2);
        meal.setExtras(extras);
        meal.setDrinkOptions(drinks);
        meal.setPrice(45.5);
        main.setTitle("המבורגר");
        meal.setMain(main);
        meals.add(meal);
        meal = new Meal();
        meal.setTitle("המבורגר בבאגט");
        main = new Main();
        meal.setExtraAmount(2);
        meal.setExtras(extras);
        meal.setDrinkOptions(drinks);
        meal.setPrice(45.5);
        main.setTitle("המבורגר");
        meal.setMain(main);
        meals.add(meal);
        meal = new Meal();
        meal.setTitle("המבורגר בפיתה");
        main = new Main();
        meal.setExtraAmount(2);
        meal.setExtras(extras);
        meal.setDrinkOptions(drinks);
        meal.setPrice(45.5);
        main.setTitle("המבורגר");
        meal.setMain(main);
        meals.add(meal);
        items.add(item);

        item = new Item();
        item.setTitle("פרגית");
        item.setStandAlone(false);
        meal = new Meal();
        meal.setTitle("פרגית בצלחת");
        main = new Main();
        meal.setExtraAmount(2);
        meal.setExtras(extras);
        meal.setDrinkOptions(drinks);
        meal.setPrice(55.5);
        main.setTitle("פרגית");
        meal.setMain(main);
        meals.add(meal);
        meal = new Meal();
        meal.setTitle("פרגית בבאגט");
        main = new Main();
        meal.setExtraAmount(2);
        meal.setExtras(extras);
        meal.setDrinkOptions(drinks);
        meal.setPrice(55.5);
        main.setTitle("פרגית");
        meal.setMain(main);
        meals.add(meal);
        meal = new Meal();
        meal.setTitle("פרגית בפיתה");
        main = new Main();
        meal.setExtraAmount(2);
        meal.setExtras(extras);
        meal.setDrinkOptions(drinks);
        meal.setPrice(55.5);
        main.setTitle("פרגית");
        meal.setMain(main);
        meals.add(meal);
        items.add(item);

        // set items and meals to category
        cat.setItems(items);
        cat.setMeals(meals);
        categories.add(cat);

        List<Item> drinksAsItems = new ArrayList<Item>(drinks);

        extras = new ArrayList<Item>();
        extra = new Item();
        extra.setTitle("זיתים ירוקים");
        extra.setPrice(5);
        extras.add(extra);
        extra = new Item();
        extra.setTitle("פטריות");
        extra.setPrice(5);
        extras.add(extra);
        extra = new Item();
        extra.setTitle("עגבניות");
        extra.setPrice(5);
        extras.add(extra);

        meals = new ArrayList<>();
        items = new ArrayList<>();
        cat = new Category();
        cat.setTitle("חלבי");
        item = new Item();
        item.setTitle("פיצה");
        item.setStandAlone(false);
        meal = new Meal();
        meal.setTitle("פיצה משפחתית");
        main = new Main();
        meal.setExtraAmount(2);
        meal.setExtras(extras);
        meal.setDrinkOptions(drinks);
        meal.setPrice(25.0);
        main.setTitle("פיצה");
        meal.setMain(main);
        meals.add(meal);
        items.add(item);
        cat.setDescription("ארוחות חלביות מדהימות");
        cat.setItems(items);
        cat.setMeals(meals);
        categories.add(cat);

        meals = new ArrayList<>();
        items = new ArrayList<>();
        cat = new Category();
        cat.setTitle("סלטים");

        item = new Item();
        item.setTitle("סלט");
        item.setStandAlone(false);
        meal = new Meal();
        meal.setTitle("סלט פסטה");
        main = new Main();
        meal.setExtraAmount(0);
        extras = new ArrayList<Item>();
        extra = new Item();
        extra.setTitle("בטטה");
        extra.setPrice(5);
        extras.add(extra);
        extra = new Item();
        extra.setTitle("חציל");
        extra.setPrice(5);
        extras.add(extra);
        extra = new Item();
        extra.setTitle("ביצה קשה");
        extra.setPrice(5);
        extras.add(extra);
        meal.setExtras(extras);
        meal.setDrinkOptions(drinks);
        meal.setPrice(25.0);
        main.setTitle("סלט");
        meal.setMain(main);
        meals.add(meal);
        items.add(item);

        cat.setDescription("מבחר סלטים בהרכבה");
        cat.setItems(items);
        cat.setMeals(meals);
        categories.add(cat);
        cat = new Category();
        meals = new ArrayList<>();
        items = new ArrayList<>();
        cat.setTitle("שתיה חמה");

        item = new Item();
        item.setTitle("הפוך קטן");
        item.setStandAlone(true);
        item.setPrice(5);
        items.add(item);
        item = new Item();
        item.setTitle("הפוך גדול");
        item.setPrice(7.5);
        item.setStandAlone(true);
        items.add(item);
        item = new Item();
        item.setTitle("קפה שחור");
        item.setPrice(4);
        item.setStandAlone(true);
        items.add(item);
        item = new Item();
        item.setTitle("נס על חלב");
        item.setPrice(6);
        item.setStandAlone(true);
        items.add(item);

        cat.setDescription("כל סוגי השתייה החמה");
        cat.setItems(items);
        categories.add(cat);
        cat = new Category();
        meals = new ArrayList<>();
        items = new ArrayList<>();
        cat.setTitle("שתיה קרה");
        cat.setDescription("כל סוגי השתיה הקרה");
        cat.setItems(drinksAsItems);
        categories.add(cat);
        cat = new Category();
        meals = new ArrayList<>();
        items = new ArrayList<>();
        cat.setTitle("מאפים");

        item = new Item();
        item.setTitle("קוראסון שקדים");
        item.setStandAlone(true);
        item.setPrice(10);
        items.add(item);
        item = new Item();
        item.setTitle("קוראסון שוקלד");
        item.setPrice(10);
        item.setStandAlone(true);
        items.add(item);
        item = new Item();
        item.setTitle("קוראסון קינמון");
        item.setPrice(10);
        item.setStandAlone(true);
        items.add(item);
        item = new Item();
        item.setTitle("רוגעלך");
        item.setPrice(5);
        item.setStandAlone(true);
        items.add(item);

        cat.setDescription("כל סוגי המאפים");
        cat.setItems(items);
        categories.add(cat);
        cat = new Category();
        meals = new ArrayList<>();
        items = new ArrayList<>();
        cat.setTitle("חטיפים");

        item = new Item();
        item.setTitle("במבה");
        item.setPrice(5.5);
        item.setStandAlone(true);
        items.add(item);
        item = new Item();
        item.setTitle("ביסלי");
        item.setPrice(5.5);
        item.setStandAlone(true);
        items.add(item);
        item = new Item();
        item.setTitle("פסק זמן");
        item.setPrice(6.5);
        item.setStandAlone(true);
        items.add(item);
        item = new Item();
        item.setTitle("קליק");
        item.setPrice(6.5);
        item.setStandAlone(true);
        items.add(item);
        cat.setItems(items);
        cat.setDescription("במבה,ביסלי,פסק זמן");
        categories.add(cat);

    }


    public void addOrderdMeal(OrderedMeal meal){
        orderedMeals.add(meal);
    }

    public void addOrderdItem(Item item){
        orderedItems.add(item);
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<OrderItem> getItems(){
        return items;
    }

    public List<Item> getOrderedItems(){
        return orderedItems;
    }

    public List<OrderedMeal> getOrderedMeals(){
        return orderedMeals;
    }

    public List<Category> getCategories() {
        return categories;
    }
}
