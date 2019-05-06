package com.dotcode.duoline.axdlockers.Utils;

import android.content.Context;
import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Helper {
    //----------------------
    public static final int REQUEST_CHECK_USER = 1;
    public static final int REQUEST_LOCKERS = 2;
    public static final int REQUEST_ADDRESSES = 3;
    public static final int REQUEST_CITIES = 4;
    public static final int REQUEST_INSERT_ADDRESS = 5;
    public static final int REQUEST_INSERT_LOCKER = 6;
    public static final int REQUEST_LOCKER_HISTORIES = 7;
    public static final int REQUEST_FILTERED_RESIDENTS = 8;
    public static final int REQUEST_CHECK_BUILDING = 9;
    public static final int REQUEST_DELETE_LOCKER_BUILDING_RESIDENT = 10;
    public static final int REQUEST_DELETE_LOCKER_HISTORY = 11;
    public static final int REQUEST_LOCKER_BUILDING_RESIDENT = 12;
    public static final int REQUEST_INSERT_LOCKER_BUILDING_RESIDENT = 13;
    public static final int REQUEST_INSERT_LOCKER_HISTORY = 14;
    public static final int REQUEST_INSERT_NOTIFICATION = 15;
    public static final int REQUEST_GET_OWNED_BUILDINGS = 16;

    public static final int STATUS_NOT_CONFIRMED = 1;
    /**
     * @return true if user is logged in application (user's credentials are saved in SharedPreferences)
     */
    public static boolean isUserLogged(Context ctx){

        return (SaveSharedPreferences.getUserId(ctx) != 0) ? true : false;
    }

    public static String addFilter(String string, List<String> operators){
        if(operators.size() == 0) {
            return "filter[" + string + "]";
        } else {
            String operatorsString = "";
            for (String value : operators){
                operatorsString = operatorsString + "[" + value + "]";
            }
            return "filter[" + string + "]" + operatorsString;
        }
    }

    public static String getEncyptedPassword(String clearPaswword){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            byte[] digest = md.digest(clearPaswword.getBytes());
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            return "";
        }
    }
}
