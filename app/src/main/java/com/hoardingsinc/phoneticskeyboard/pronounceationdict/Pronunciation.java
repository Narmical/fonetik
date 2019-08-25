package com.hoardingsinc.phoneticskeyboard.pronounceationdict;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Pronunciation {

    @NonNull
    @Override
    public String toString() {
        return this.ipa + " : [" + this.spellings + "]";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Pronunciation) {
            String otherIpa = ((Pronunciation)obj).getIpa();
            return this.ipa == otherIpa;
        }
        return false;
    }

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