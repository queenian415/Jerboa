package com.jebora.jebora.Utils;

/**
 * Created by jack on 15/7/19.
 */
public class UserMainCheck {
    private static boolean isKidNumberUpdated = false;
    private static String KidSelected;
    private static boolean FilterItemSlected = false;
    private static int UserMainEnter = 0;
    private static String whichFragment;

    public static void setKidNumberUpdated (boolean KidNumberStatus){isKidNumberUpdated = KidNumberStatus;}
    public static boolean getKidNumberStatus (){return isKidNumberUpdated;}

    public static void SetKidSelected (String KidID) {KidSelected = KidID;}
    public static String GetKidSelected (){return KidSelected;}
    public static void ResetKidSelected (){KidSelected = null;}

    public static void FilterItemStatus (boolean status){FilterItemSlected = status;}
    public static boolean isFilterItemSlected (){return FilterItemSlected;}

    public static void UserMainEnters(){UserMainEnter++;}
    public static void ResetUserMainEnter(){UserMainEnter=0;}
    public static int getUserMainEnter(){return UserMainEnter;}

    public static String whoCalledUpdOptMenu(){return whichFragment;}
    public static void reqUpdOptMenu(String tag){whichFragment = tag;}
}
