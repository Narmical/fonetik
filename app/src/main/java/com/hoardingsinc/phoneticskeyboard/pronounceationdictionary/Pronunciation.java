package com.hoardingsinc.phoneticskeyboard.pronounceationdictionary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Pronunciation implements Comparable<Pronunciation> {

    @ColumnInfo(name = "frequency")
    private int frequency;
    @ColumnInfo(name = "ipa")
    private String ipa;
    @ColumnInfo(name = "spelling")
    private String spelling;
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @Override
    public int compareTo(Pronunciation o) {
        int result = Integer.compare(this.getFrequency(), o.getFrequency());
        if (result == 0) {
            return this.getSpelling().compareTo(o.getSpelling());
        } else {
            return result;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Pronunciation) {
            String otherIpa = ((Pronunciation) obj).getIpa();
            String otherSpelling = ((Pronunciation) obj).getSpelling();
            return this.ipa.equalsIgnoreCase(otherIpa) &&
                    this.spelling.equalsIgnoreCase(otherSpelling);
        }
        return false;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getIpa() {
        return ipa;
    }

    public void setIpa(String ipa) {
        this.ipa = ipa;
    }

    public String getSpelling() {
        return spelling;
    }

    public void setSpelling(String spelling) {
        this.spelling = spelling;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void incrementFrequency() {
        this.frequency++;
    }

    @NonNull
    @Override
    public String toString() {
        return this.ipa + " : [" + this.spelling + "]";
    }

    // getters and setters
}