package com.wayfoo.wayfoo.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

/**
 * Created by Ravi on 08/07/15.
 */
public class PrefManager {

    SharedPreferences pref;
    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "wayfooPref";

    // All Shared Preferences Keys
    private static final String KEY_IS_WAITING_FOR_SMS = "IsWaitingForSms";
    private static final String KEY_MOBILE_NUMBER = "mobile_number";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_LOC = "loc";
    private static final String KEY_FIRST = "first";
    private static final String KEY_REGID = "regid";
    private static final String KEY_HOTEL = "hotel";
    private static final String KEY_TITLE = "title";
    private static final String KEY_PRICE_SUM = "priceSum";
    private static final String KEY_TABLE = "table";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setIsWaitingForSms(boolean isWaiting) {
        editor.putBoolean(KEY_IS_WAITING_FOR_SMS, isWaiting);
        editor.commit();
    }

    public boolean isWaitingForSms() {
        return pref.getBoolean(KEY_IS_WAITING_FOR_SMS, false);
    }

    public void setMobileNumber(String mobileNumber) {
        editor.putString(KEY_MOBILE_NUMBER, mobileNumber);
        editor.commit();
    }

    public String getMobileNumber() {
        return pref.getString(KEY_MOBILE_NUMBER, null);
    }

    public void createLogin(String name, String email, String mobile) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_MOBILE, mobile);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    public void createUnverifiedLogin(String name, String email) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> profile = new HashMap<>();
        profile.put("name", pref.getString(KEY_NAME, null));
        profile.put("email", pref.getString(KEY_EMAIL, null));
        profile.put("mobile", pref.getString(KEY_MOBILE, null));
        return profile;
    }

    public Boolean loggedInAndVerfified() {
        if (pref.contains(KEY_EMAIL) && isLoggedIn()) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean loggedInNotVerfified() {
        if (!isLoggedIn() && pref.contains(KEY_EMAIL)) {
            return true;
        } else {
            return false;
        }
    }

    public void putProfImage(String image) {
        editor.putString(KEY_IMAGE, image);
        editor.commit();
    }

    public void putLocation(String loc) {
        editor.putString(KEY_LOC, loc);
        editor.commit();
    }

    public Boolean isFirstTime() {
        return pref.getBoolean(KEY_FIRST, true);
    }

    public void setFirstTime() {
        editor.putBoolean(KEY_FIRST, false);
        editor.commit();
    }

    public String getName() {
        return pref.getString(KEY_NAME, null);
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public String getLocation() {
        return pref.getString(KEY_LOC, null);
    }

    public String getRegId() {
        return pref.getString(KEY_REGID, null);
    }

    public String getHotelName() {
        return pref.getString(KEY_HOTEL, null);
    }

    public String getTitle() {
        return pref.getString(KEY_TITLE, null);
    }

    public int getPriceSum() {
        return pref.getInt(KEY_PRICE_SUM, 0);
    }

    public String getTable() {
        return pref.getString(KEY_TABLE, null);
    }

    public String getImage() {
        return pref.getString(KEY_IMAGE, null);
    }

    public void setPriceSum(int priceSum) {
        editor.putInt(KEY_PRICE_SUM, priceSum);
        editor.commit();
    }

    public void setLocation(String loc) {
        editor.putString(KEY_LOC, loc);
        editor.commit();
    }

    public void setTitle(String title) {
        editor.putString(KEY_TITLE, title);
        editor.commit();
    }

    public void setTable(String table) {
        editor.putString(KEY_TABLE, table);
        editor.commit();
    }

    public void setHotelName(String hotelname) {
        editor.putString(KEY_HOTEL, hotelname);
        editor.commit();
    }

    public void setRegid(String regid) {
        editor.putString(KEY_REGID, regid);
        editor.commit();
    }
}