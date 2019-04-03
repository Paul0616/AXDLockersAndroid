package com.dotcode.duoline.axdlockers.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

    public static boolean getTokenExpireAt(Context ctx) {
        return getSharedPreference(ctx).getLong("TOKEN_EXPIRE_AT", 0);
    }

    public static void setTokenExpireAt(Context ctx, long tokenExpireAt) {
        SharedPreferences.Editor editor = getSharedPreference(ctx).edit();
        editor.putLong("TOKEN_EXPIRE_AT", tokenExpireAt);
        editor.commit();
    }
}
