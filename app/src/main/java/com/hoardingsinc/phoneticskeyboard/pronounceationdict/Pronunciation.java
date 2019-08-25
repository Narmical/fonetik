package com.hoardingsinc.phoneticskeyboard.pronounceationdict;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Pronunciation {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "ipa")
    private String ipa;

    @ColumnInfo(name = "spellings")
    private String spellings;

    public int getUid() {
        return uid;
    }

    public String getIpa() {
        return ipa;
    }

    public void setIpa(String ipa) {
        this.ipa = ipa;
    }

    public String getSpellings() {
        return spellings;
    }

    public void setSpellings(String spellings) {
        this.spellings = spellings;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    // getters and setters
}