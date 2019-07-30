package fes.ghm.books;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class SpUtil {

    private SpUtil() {
    }

    public static final String PREF_NAME = "BooksPreferences";
    public static final String POSITION = "positon";
    public static final String QUERY = "query";

    public static SharedPreferences getPref (Context context){
        // MODE_PRIVATE constant on Context means that only the application have access
        // to this sharedPreferences
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static String getPreferenceString (Context context, String key){
        // the second parameter is the default value in case they don't find the result
        return getPref(context).getString(key, "");
    }

    public static int getPreferenceInt (Context context, String key){
        // the second parameter is the default value in case they don't find the result
        return getPref(context).getInt(key, 0);
    }

    public static void setPreferenceString (Context context, String key, String value){
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void setPreferenceInt (Context context, String key, int value){
        SharedPreferences.Editor editor = getPref(context).edit();
        editor.putInt(key, value);
        editor.apply();
    }


    /**
     * we will use this method to use the last 5 search as a method to create menu dynamically
     * @param contect
     * @return
     */
    public static ArrayList<String> getQueryList (Context contect){
        ArrayList<String> queryList = new ArrayList<>();
        for (int i = 1; i <=5 ; i++) {
            String query = getPref(contect).getString(QUERY + String.valueOf(i), "");
            if( ! query.isEmpty()){
                query = query.replace(",", " ");
                queryList.add(query.trim());
            }
        }
        return queryList;
    }


}
