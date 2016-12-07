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
     * Global Server
     */
//    public static final String SERVER_IP = "time2eat.eu-gb.mybluemix.net";


    //Primary Server - local
//    public final static String GET_SERVERS = "http://192.168.1.11:8080/CafeteriaServer/rest/server/getServers";

    //Primary Server - global
    public final static String GET_SERVERS = "http://time2eat.eu-gb.mybluemix.net/rest/server/getServers";


    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
    public static final String DATE_FORMAT = "dd-MM-yyyy";

    public final static String GET_CATEGORIES_URL = "http://" + DataHolder.getInstance().getServerIp() +
            ":8080/CafeteriaServer/rest/data/getCategories";

    public final static String GET_DRINKS_URL = "http://" + DataHolder.getInstance().getServerIp() +
            ":8080/CafeteriaServer/rest/data/getDrinks";

    public final static String USER_REGISTRATION_URL = "http://" + DataHolder.getInstance().getServerIp() +
            ":8080/CafeteriaServer/rest/users/insertUser";

    public final static String USER_FACEBOOK_REGISTRATION_URL = "http://" + DataHolder.getInstance().getServerIp() +
            ":8080/CafeteriaServer/rest/users/insertFacebookUser";

    public final static String UPDATE_USER_URL = "http://" + DataHolder.getInstance().getServerIp() +
            ":8080/CafeteriaServer/rest/users/updateUser";

    public final static String USER_VALIDATION_URL = "http://" + DataHolder.getInstance().getServerIp() +
            ":8080/CafeteriaServer/rest/users/isUserExist";

    public final static String GET_TOKEN = "http://" + DataHolder.getInstance().getServerIp() +
            ":8080/CafeteriaServer/rest/push/getToken";

    public final static String SET_TOKEN = "http://" + DataHolder.getInstance().getServerIp() +
            ":8080/CafeteriaServer/rest/push/attachPushIdToUser";

    public final static String SEND_ORDER = "http://" + DataHolder.getInstance().getServerIp() +
            ":8080/CafeteriaServer/rest/data/insertOrder";

    public final static String GET_FAVORITES = "http://" + DataHolder.getInstance().getServerIp() +
            ":8080/CafeteriaServer/rest/data/getFavorites";


//
//    public final static String GET_CATEGORIES_URL = "http://" + SERVER_IP +
//            "/rest/data/getCategories";
//
//    public final static String GET_DRINKS_URL = "http://" + SERVER_IP +
//            "/rest/data/getDrinks";
//
//    public final static String USER_REGISTRATION_URL = "http://" + SERVER_IP +
//            "/rest/users/insertUser";
//
//public final static String USER_FACEBOOK_REGISTRATION_URL = "http://" + SERVER_IP +
//        "/rest/users/insertFacebookUser";
//    public final static String UPDATE_USER_URL = "http://" + SERVER_IP +
//            "/rest/users/updateUser";
//
//    public final static String USER_VALIDATION_URL = "http://" + SERVER_IP +
//            "/rest/users/isUserExist";
//
//    public final static String GET_TOKEN = "http://" + SERVER_IP +
//            "/rest/push/getToken";
//
//    public final static String SET_TOKEN = "http://" + SERVER_IP +
//            "/rest/push/attachPushIdToUser";
//
//    public final static String SEND_ORDER = "http://" + SERVER_IP +
//            "/rest/data/insertOrder";
}
