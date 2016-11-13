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
    //public final static String SERVER_IP = "192.168.43.91";

    /**
     * Shira IP
     */
    public final static String SERVER_IP = "192.168.43.231";
//    public final static String SERVER_IP = "35.162.198.159";
    /**
     * Moshe IP
     */
    //public final static String SERVER_IP = "10.0.0.146";



    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public final static String GET_CATEGORIES_URL = "http://" + SERVER_IP +
            ":8080/CafeteriaServer/rest/data/getCategories";

    public final static String GET_DRINKS_URL = "http://" + SERVER_IP +
            ":8080/CafeteriaServer/rest/data/getDrinks";

    public final static String USER_REGISTRATION_URL = "http://" + SERVER_IP +
            ":8080/CafeteriaServer/rest/users/insertUser";

    public final static String UPDATE_USER_URL = "http://" + SERVER_IP +
            ":8080/CafeteriaServer/rest/users/updateUser";

    public final static String USER_VALIDATION_URL = "http://" + SERVER_IP +
            ":8080/CafeteriaServer/rest/users/isUserExist";

    public final static String GET_TOKEN = "http://" + SERVER_IP +
            ":8080/CafeteriaServer/rest/push/getToken";

    public final static String SET_TOKEN = "http://" + SERVER_IP +
            ":8080/CafeteriaServer/rest/push/attachPushIdToUser";

    public final static String SEND_ORDER = "http://" + SERVER_IP +
            ":8080/CafeteriaServer/rest/data/insertOrder";
}
