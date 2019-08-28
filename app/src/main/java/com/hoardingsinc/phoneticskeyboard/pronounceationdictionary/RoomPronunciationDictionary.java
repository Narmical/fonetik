package com.hoardingsinc.phoneticskeyboard.pronounceationdictionary;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.room.Room;

import com.hoardingsinc.phoneticskeyboard.rawdictionary.RawDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class RoomPronunciationDictionary extends PronunciationDictionary {

    private PronunciationDao pronunciationDao;

    public RoomPronunciationDictionary(Context context, RawDictionary rawDictionary) {
        PronunciationDB db = Room.databaseBuilder(context, PronunciationDB.class, "pronunciation.db").build();
        this.pronunciationDao = db.pronunciationDao();
        if (this.pronunciationDao.numEntries() == 0) {
            List<RawDictionary> rawDictionaries = new ArrayList<>();
            rawDictionaries.add(rawDictionary);
            this.loadDictionary(rawDictionaries);
        }
    }

    public RoomPronunciationDictionary(Context context, List<RawDictionary> rawDictionaries) {
        PronunciationDB db = Room.databaseBuilder(context, PronunciationDB.class, "pronunciation.db").build();
        this.pronunciationDao = db.pronunciationDao();
        if (this.pronunciationDao.numEntries() == 0) {
            this.loadDictionary(rawDictionaries);
        }
    }

    public Set<String> exactMatch(String ipa) {
        Set<String> spellings = new TreeSet<>(new StringLengthComparator());
        for (Pronunciation pronunciation : this.pronunciationDao.get(ipa)) {
            spellings.add(pronunciation.getSpellings());
        }
        return spellings;
    }

    public SortedSet<String> lookAheadMatch(String ipa) {
        SortedSet<String> result = new TreeSet<>(new StringLengthComparator());
        for (Pronunciation pronunciation : this.pronunciationDao.getLikeIpa(ipa + "%")) {
            result.add(pronunciation.getSpellings());
        }
        return result;
    }

    public int numEntries() {
        return this.pronunciationDao.numEntries();
    }

    void loadDictionary(List<RawDictionary> rawDictionaries) {
        Log.d("RoomDb", "Database Load Start");
        List<Pronunciation> pronunciations = new ArrayList<>();
        for (RawDictionary rawDictionary : rawDictionaries) {
            for (Pair<String, String> entry : rawDictionary) {
                String ipa = entry.first;
                String word = entry.second;

                Log.d("RoomDb", word);

                if (word.equalsIgnoreCase("rude")) {
                    Log.d("RoomDb", "ahhhhhh");
                }

                Pronunciation pronunciation = new Pronunciation();
                pronunciation.setIpa(ipa);
                pronunciation.setSpellings(word);

                pronunciations.add(pronunciation);
            }
        }
        pronunciationDao.insertAll(pronunciations);
        Log.d("RoomDb", "Database Load Complete");
    }
}
