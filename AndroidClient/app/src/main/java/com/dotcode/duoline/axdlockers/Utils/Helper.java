package com.dotcode.duoline.axdlockers.Utils;

import android.content.Context;
import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Helper {
    public static final String KEY_EMAIL= "email";
    public static final String KEY_PASSWORD = "password";
    //----------------------
    public static final int REQUEST_CHECK_USER = 1;
    public static final int REQUEST_LOCKERS = 2;
    public static final int REQUEST_ADDRESSES = 3;

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
