package com.dotcode.duoline.axdlockers.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dotcode.duoline.axdlockers.Models.RetroAddress;
import com.dotcode.duoline.axdlockers.Models.RetroBuilding;
import com.dotcode.duoline.axdlockers.Models.RetroFilteredResident;
import com.dotcode.duoline.axdlockers.Models.RetroLocker;
import com.dotcode.duoline.axdlockers.Models.RetroResident;
import com.google.gson.Gson;

public class SaveSharedPreferences {
    static SharedPreferences getSharedPreference(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    //########################### USER #######################
    public static int getUserId(Context ctx) {
        return getSharedPreference(ctx).getInt("ID", 0);
    }

    public static void setUserId(Context ctx, int userId) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putInt("ID", userId);
        editor.commit();
    }

    public static String getEncryptedPassword(Context ctx) {
        return getSharedPreference(ctx).getString("ENCRYPTED_PASSWORD", "");
    }

    public static void setEncryptedPassword(Context ctx, String password) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putString("ENCRYPTED_PASSWORD", password);
        editor.commit();
    }
    public static String getFirstName(Context ctx) {
        return getSharedPreference(ctx).getString("userFIRSTNAME", "");
    }

    public static void setFirstName(Context ctx, String userFIRSTNAME) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putString("userFIRSTNAME", userFIRSTNAME);
        editor.commit();
    }
    public static String getLasttName(Context ctx) {
        return getSharedPreference(ctx).getString("userLASTNAME", "");
    }

    public static void setLastName(Context ctx, String userLASTNAME) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putString("userLASTNAME", userLASTNAME);
        editor.commit();
    }

    public static String getEmail(Context ctx) {
        return getSharedPreference(ctx).getString("EMAIL", "");
    }

    public static void setEmail(Context ctx, String email) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putString("EMAIL", email);
        editor.commit();
    }

    public static String getAccesToken(Context ctx) {
        return getSharedPreference(ctx).getString("ACCES_TOKEN", "");
    }

    public static void setAccesToken(Context ctx, String accesToken) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putString("ACCES_TOKEN", accesToken);
        editor.commit();
    }

    public static boolean getIsAdmin(Context ctx) {
        return getSharedPreference(ctx).getBoolean("IS_ADMIN", false);
    }

    public static void setIsAdmin(Context ctx, boolean isAdmin) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putBoolean("IS_ADMIN", isAdmin);
        editor.commit();
    }

    public static long getTokenExpireAt(Context ctx) {
        return getSharedPreference(ctx).getLong("TOKEN_EXPIRE_AT", 0);
    }

    public static void setTokenExpireAt(Context ctx, long tokenExpireAt) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putLong("TOKEN_EXPIRE_AT", tokenExpireAt);
        editor.commit();
    }

    public static void logOutUser(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.remove("ID");
        editor.remove("TOKEN_EXPIRE_AT");
        editor.remove("IS_ADMIN");
        editor.remove("ACCES_TOKEN");
        //........
        editor.commit();
    }

    public static void setAddress(Context ctx, RetroAddress address){
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();

        Gson gson = new Gson();
        String json = gson.toJson(address);
        editor.putString("ADDRESS", json);
        editor.commit();
    }

    public static RetroAddress getAddress(Context ctx) {
        Gson gson = new Gson();
        String json = getSharedPreference(ctx).getString("ADDRESS", "");
        return gson.fromJson(json, RetroAddress.class);
    }

    public static void setAddressNull(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.remove("ADDRESS");
        editor.commit();
    }

    public static void setLocker(Context ctx, RetroLocker locker){
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        Gson gson = new Gson();
        String json = gson.toJson(locker);
        editor.putString("LOCKER", json);
        editor.commit();
    }

    public static RetroLocker getLocker(Context ctx) {
        Gson gson = new Gson();
        String json = getSharedPreference(ctx).getString("LOCKER", "");
        return gson.fromJson(json, RetroLocker.class);
    }

    public static RetroFilteredResident getResident(Context ctx) {
        Gson gson = new Gson();
        String json = getSharedPreference(ctx).getString("RESIDENT", "");
        return gson.fromJson(json, RetroFilteredResident.class);
    }

    public static void setResident(Context ctx, RetroFilteredResident resident){
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        Gson gson = new Gson();
        String json = gson.toJson(resident);
        editor.putString("RESIDENT", json);
        editor.commit();
    }



    public static void setBuildingNull(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.remove("BUILDING");
        editor.commit();
    }

    public static int getlastInsertedParcelID(Context ctx) {
        return getSharedPreference(ctx).getInt("PARCEL_ID", 0);
    }

    public static void setlastInsertedParcelID(Context ctx, int lastId) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putInt("PARCEL_ID", lastId);
        editor.commit();
    }

    public static void setParcelNull(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.remove("PARCEL_ID");
        editor.commit();
    }

    public static int getlastInsertedVirtualParcelID(Context ctx) {
        return getSharedPreference(ctx).getInt("VIRTUAL_PARCEL_ID", 0);
    }

    public static void setlastInsertedVirtualParcelID(Context ctx, int lastId) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putInt("VIRTUAL_PARCEL_ID", lastId);
        editor.commit();
    }

    public static void setVirtualParcelNull(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.remove("VIRTUAL_PARCEL_ID");
        editor.commit();
    }

    public static int getlastInsertedHistoryID(Context ctx) {
        return getSharedPreference(ctx).getInt("HISTORY_ID", 0);
    }

    public static void setlastInsertedHistoryID(Context ctx, int lastId) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putInt("HISTORY_ID", lastId);
        editor.commit();
    }
    public static void setHistoryNull(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.remove("HISTORY_ID");
        editor.commit();
    }

    public static boolean getAddLockerOnly(Context ctx){
        return getSharedPreference(ctx).getBoolean("lockerOnly", false);
    }
    public static void setAddLockerOnly(Context ctx, boolean lockerOnly){
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putBoolean("lockerOnly", lockerOnly);
        editor.commit();
    }
}
