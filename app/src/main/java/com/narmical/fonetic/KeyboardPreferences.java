package com.narmical.fonetic;

import android.content.Context;
import android.content.SharedPreferences;

class KeyboardPreferences {
    static final int LAYOUT_DIPHTHONGS = 1;
    static final int LAYOUT_LEGACY = 4;
    static final int LAYOUT_NORMAL = 0;
    static final int LAYOUT_NUM_SYMB = 2;
    static final int LAYOUT_SHAVIAN = 3;
    static final int LAYOUT_QWERTY = 5;
    private static final String PREFIX_LAYOUT = "layout";
    private static final String PREFS_NAME = "Preferences";
    private Context mContext;
    static final int MAX_SUGGESTIONS = 25;

    KeyboardPreferences(Context context) {
        mContext = context;
    }

    int getLayout() {
        return getSharedPreferences().getInt(PREFIX_LAYOUT, LAYOUT_NORMAL);
    }

    void rotateLayout() {
        saveLayout((getLayout() + 1) % 3);
    }

    void saveLayout(int layout) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(PREFIX_LAYOUT, layout);
        editor.apply();
    }

    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(PREFS_NAME, 0);
    }

}
