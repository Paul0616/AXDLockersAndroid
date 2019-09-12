package com.dotcode.duoline.axdlockers.Utils;

import android.content.Context;
import android.text.TextUtils;

import com.dotcode.duoline.axdlockers.Models.RetroUser;
import com.dotcode.duoline.axdlockers.Models.RetroUserXRight;

import java.io.File;
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
//    public static final int REQUEST_LOCKER_HISTORIES = 7;
//    public static final int REQUEST_FILTERED_RESIDENTS = 8;
//    public static final int REQUEST_CHECK_BUILDING = 9;
    public static final int REQUEST_DELETE_PARCEL = 10;
    public static final int REQUEST_DELETE_LOCKER_HISTORY = 11;
//    public static final int REQUEST_LOCKER_BUILDING_RESIDENT = 12;
    public static final int REQUEST_INSERT_PARCEL = 13;
    public static final int REQUEST_INSERT_LOCKER_HISTORY = 14;
    public static final int REQUEST_INSERT_NOTIFICATION = 15;
//    public static final int REQUEST_GET_OWNED_BUILDINGS = 16;
    public static final int REQUEST_RESIDENTS_GET_BY_FULL_NAME_OR_UNIT = 17;
    public static final int REQUEST_INSERT_ORPHAN_PARCEL = 18;
    public static final int REQUEST_NEW_SECURITY_CODE = 19;
    public static final int REQUEST_INSERT_VIRTUAL_PARCEL = 20;
    public static final int REQUEST_INSERT_VIRTUAL_NOTIFICATION = 21;
    public static final int REQUEST_DELETE_VIRTUAL_PARCEL = 22;
    public static final int REQUEST_MANUAL_LOCKERS = 23;

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

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public static boolean userHaveRight(List<RetroUserXRight> userXRights, String code) {
        for(RetroUserXRight item : userXRights){
            if(item.getRight().getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    public static void setFullNameInPreferences(Context ctx, RetroUser userObj){
        SaveSharedPreferences.setFirstName(ctx, userObj.getFirstName());
        SaveSharedPreferences.setLastName(ctx, userObj.getLastName());
    }
}
