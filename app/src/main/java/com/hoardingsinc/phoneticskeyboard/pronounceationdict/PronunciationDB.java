package com.hoardingsinc.phoneticskeyboard.pronounceationdict;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Pronunciation.class}, version = 1, exportSchema=false)
public abstract class PronunciationDB extends RoomDatabase {
    public abstract PronunciationDao pronunciationDao();
}

