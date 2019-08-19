package com.hoardingsinc.phoneticskeyboard.pronounceationdict;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Pronunciation.class}, version = 1)
public abstract class PronunciationDB extends RoomDatabase {
    public abstract PronunciationDao pronunciationDao();
}

