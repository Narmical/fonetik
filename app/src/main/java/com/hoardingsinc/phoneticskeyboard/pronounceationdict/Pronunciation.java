package com.hoardingsinc.phoneticskeyboard.pronounceationdict;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Pronunciation {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "ipa")
    private String ipa;

    @ColumnInfo(name = "spellings")
    private String[] spellings;

    // getters and setters
}