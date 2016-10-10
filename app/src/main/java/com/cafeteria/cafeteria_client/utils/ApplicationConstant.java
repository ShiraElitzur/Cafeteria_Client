package com.cafeteria.cafeteria_client.utils;

/**
 * Created by anael on 09/10/16.
 */

public class ApplicationConstant {

    /**
     * Constant for Web Services
     */

    /**
     * Anael IP
     */
    //public final static String SERVER_IP = "192.168.1.11";

    /**
     * Shira IP
     */
    public final static String SERVER_IP = "192.168.43.231";

    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public final static String GET_CATEGORIES_URL = "http://" + SERVER_IP +
            ":8080/CafeteriaServer/rest/data/getCategories";

    public final static String GET_DRINKS_URL = "http://" + SERVER_IP +
            ":8080/CafeteriaServer/rest/data/getDrinks";

    public final static String USER_REGISTRATION_URL = "http://" + SERVER_IP +
            ":8080/CafeteriaServer/rest/users/insertUser";

    public final static String USER_VALIDATION_URL = "http://" + SERVER_IP +
            ":8080/CafeteriaServer/rest/users/isUserExist";

}
