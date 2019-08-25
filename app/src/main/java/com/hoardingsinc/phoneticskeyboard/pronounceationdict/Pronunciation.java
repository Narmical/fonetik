package com.hoardingsinc.phoneticskeyboard.pronounceationdict;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    @NonNull
    @Override
    public String toString() {
        return this.ipa + " : [" + this.spellings + "]";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Pronunciation) {
            String otherIpa = ((Pronunciation) obj).getIpa();
            return this.ipa.equals(otherIpa);
        }
        return false;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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

    public void addSpelling(String spelling) {
        if (spellings == null || spellings == "")
            spellings = spelling;
        else
            spellings = spellings + "," + spelling;
    }

    // getters and setters
}