package com.hobarb.locatadora.utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CONSTANTS {


    public static class SHARED_PREF_KEYS
    {
        public static String APP_PREFERENCES = "APP_PREFS";
        public static String IDENTIFIER = "Identifier";
        public static String EMERGENCY_NAME_KEY = "EMERGENCY_NAME";
        public static String EMERGENCY_NUMBER_KEY = "EMERGENCY_NUMBER";
    }



    public static class MAPKEYS {
        //Alarm
        public static String DATETIME = "DATETIME";
        public static String LOCATION = "LOCATION";
        public static String LATLNG = "LAT,LNG";

        //Reminders
        public static String EVENT_DATE = "EVENT_DATE";
        public static String EVENT_TIME = "EVENT_TIME";
        public static String EVENT_NAME = "EVENT_NAME";
        public static String EVENT_LOCATION = "EVENT_LOCATION";


        public static String CONTACT_NAME = "CONTACT_NAME";
        public static String CONTACT_NUMBER = "CONTACT_NUMBER";
    }

    public static class FIRESTORESTUFF {

        public static String MAINTABLE = "USER_DATA";
        public static String USERID = "";
        public static String HISTORY = "HISTORY";
        public static String REMINDERS = "REMINDERS";
        public static String CONTACTS = "CONTACTS";
    }

    public static class ALARM_STUFF{
        public static String DESTINATION = "";
        public static int NOTIFY_INTERVAL = 0;
        public static int CHECK_LOCATION_INTERVAL = 5000;
        public static Set<String> MY_CONTACTS = new HashSet<>();



    }

    public static class BG_STUFF{

        public static double CURRENT_USER_LATITUDE = 0.0;
        public static double CURRENT_USER_LONGITUDE = 0.0;
        public static double DESTINATION_LATITUDE = 0.0;
        public static double DESTINATION_LONGITUDE = 0.0;
        public static String DESTINATION = "";
        public static double CURRENT_DISTANCE_REMAINING = 1000;

        public static String DESTINATION_LAT_LNG = "";

        public static String INTENT_EXTRA_LATITUDE= "LATITUDE";
        public static String INTENT_EXTRA_LONGITUDE = "LONGITUDE";
        public static String INTENT_ACTION = "TRACK_USER_ACTIVITY";
        public static  String INTENT_EXTRA_REACHED = "IS_DESTINATION_REACHED";



    }


}
