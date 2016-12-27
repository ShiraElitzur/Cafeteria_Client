package com.cafeteria.cafeteria_client.utils;

/**
 * Created by anael on 09/10/16.
 */

public class ApplicationConstant {
    /**
     * Constant for Web Services
     */

    /**
     * Anael PRIMARY SERVER IP
     */
//    public final static String SERVER_IP = "192.168.43.140";

    /**
     * Shira PRIMARY SERVER IP
     */
//    public final static String SERVER_IP = "192.168.43.231";
//    public final static String SERVER_IP = "35.162.198.159";
    /**
     * Moshe IP
     */
    //public final static String SERVER_IP = "10.0.0.146";

    /**
     * Global Cafeteria
     */
//    public static final String SERVER_IP = "time2eat.eu-gb.mybluemix.net";


    //Primary Cafeteria - local
//    public final static String GET_SERVERS = "http://192.168.1.19:8080/CafeteriaServer/rest/server/getServers";

    //Primary Cafeteria - global
    public final static String GET_SERVERS = "http://time2eat.eu-gb.mybluemix.net/rest/server/getServers";

    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
    public static final String DATE_TIME_SQLITE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String TIME_FORMAT = "HH:mm";
    public static final int SQLITE_LIMIT = 100;


//    public final static String GET_CATEGORIES_URL = ":8080/CafeteriaServer/rest/data/getCategories";
//
//    public final static String GET_DRINKS_URL = ":8080/CafeteriaServer/rest/data/getDrinks";
//
//    public final static String USER_REGISTRATION_URL = ":8080/CafeteriaServer/rest/users/insertUser";
//
//    public final static String USER_FACEBOOK_REGISTRATION_URL = ":8080/CafeteriaServer/rest/users/insertFacebookUser";
//
//    public final static String UPDATE_USER_URL = ":8080/CafeteriaServer/rest/users/updateUser";
//
//    public final static String USER_VALIDATION_URL = ":8080/CafeteriaServer/rest/users/isUserExist";
//
//    public final static String GET_TOKEN = ":8080/CafeteriaServer/rest/push/getToken";
//
//    public final static String SET_TOKEN = ":8080/CafeteriaServer/rest/push/attachPushIdToUser";
//
//    public final static String SEND_ORDER = ":8080/CafeteriaServer/rest/data/insertOrder";
//
//    public final static String GET_FAVORITE_MEALS = ":8080/CafeteriaServer/rest/data/getFavoriteMeals";
//
//    public final static String GET_FAVORITE_ITEMS = ":8080/CafeteriaServer/rest/data/getFavoriteItems";
//
//    public final static String VALIDATE_OR_SIGN_UP = ":8080/CafeteriaServer/rest/users/validateOrSignUpUser";
//
//    public final static String FORGOT_PASSWORD = ":8080/CafeteriaServer/rest/email/forgotPassword";


    public final static String GET_CATEGORIES_URL = "/rest/data/getCategories";

    public final static String GET_DRINKS_URL = "/rest/data/getDrinks";

    public final static String USER_REGISTRATION_URL = "/rest/users/insertUser";

    public final static String USER_FACEBOOK_REGISTRATION_URL = "/rest/users/insertFacebookUser";

    public final static String UPDATE_USER_URL = "/rest/users/updateUser";

    public final static String USER_VALIDATION_URL = "/rest/users/isUserExist";

    public final static String GET_TOKEN = "/rest/push/getToken";

    public final static String SET_TOKEN = "/rest/push/attachPushIdToUser";

    public final static String SEND_ORDER = "/rest/data/insertOrder";

    public final static String GET_FAVORITE_MEALS = "/rest/data/getFavoriteMeals";

    public final static String GET_FAVORITE_ITEMS = "/rest/data/getFavoriteItems";

    public final static String VALIDATE_OR_SIGN_UP = "/rest/users/validateOrSignUpUser";

    public final static String FORGOT_PASSWORD = "/rest/email/forgotPassword";

    public static String getAddress(String name){
        switch(name){
            case GET_CATEGORIES_URL:
                return "http://" + DataHolder.getInstance().getCafeteria().getServerIp() + GET_CATEGORIES_URL;
            case GET_DRINKS_URL:
                return "http://" + DataHolder.getInstance().getCafeteria().getServerIp() + GET_DRINKS_URL;
            case USER_REGISTRATION_URL:
                return "http://" + DataHolder.getInstance().getCafeteria().getServerIp() + USER_REGISTRATION_URL;
            case USER_FACEBOOK_REGISTRATION_URL:
                return "http://" + DataHolder.getInstance().getCafeteria().getServerIp() + USER_FACEBOOK_REGISTRATION_URL;
            case UPDATE_USER_URL:
                return "http://" + DataHolder.getInstance().getCafeteria().getServerIp() + UPDATE_USER_URL;
            case USER_VALIDATION_URL:
                return "http://" + DataHolder.getInstance().getCafeteria().getServerIp() + USER_VALIDATION_URL;
            case GET_TOKEN:
                return "http://" + DataHolder.getInstance().getCafeteria().getServerIp() + GET_TOKEN;
            case SET_TOKEN:
                return "http://" + DataHolder.getInstance().getCafeteria().getServerIp() + SET_TOKEN;
            case SEND_ORDER:
                return "http://" + DataHolder.getInstance().getCafeteria().getServerIp() + SEND_ORDER;
            case GET_FAVORITE_MEALS:
                return "http://" + DataHolder.getInstance().getCafeteria().getServerIp() + GET_FAVORITE_MEALS;
            case GET_FAVORITE_ITEMS:
                return "http://" + DataHolder.getInstance().getCafeteria().getServerIp() + GET_FAVORITE_ITEMS;
            case VALIDATE_OR_SIGN_UP:
                return "http://" + DataHolder.getInstance().getCafeteria().getServerIp() + VALIDATE_OR_SIGN_UP;
            case FORGOT_PASSWORD:
                return "http://" + DataHolder.getInstance().getCafeteria().getServerIp() + FORGOT_PASSWORD;
            default:
                return name;
        }
    }
}
