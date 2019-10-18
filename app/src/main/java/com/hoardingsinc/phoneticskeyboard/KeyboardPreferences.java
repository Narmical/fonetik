package com.hoardingsinc.phoneticskeyboard;

import android.content.Context;
import android.content.SharedPreferences;

public class KeyboardPreferences {
    public static final int LAYOUT_DIPHTHONGS = 1;
    public static final int LAYOUT_LEGACY = 4;
    public static final int LAYOUT_NORMAL = 0;
    public static final int LAYOUT_NUM_SYMB = 2;
    public static final int LAYOUT_SHAVIAN = 3;
    private static final String PREFIX_LAYOUT = "layout";
    private static final String PREFS_NAME = "Preferences";
    private Context mContext;

    public KeyboardPreferences(Context context) {
        mContext = context;
    }

    public int getLayout() {
        return getSharedPreferences().getInt(PREFIX_LAYOUT, LAYOUT_NORMAL);
    }

    public void rotateLayout() {
        saveLayout((getLayout() + 1) % 3);
    }

    public void saveLayout(int layout) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(PREFIX_LAYOUT, layout);
        editor.apply();
    }

    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(PREFS_NAME, 0);
    }

}
