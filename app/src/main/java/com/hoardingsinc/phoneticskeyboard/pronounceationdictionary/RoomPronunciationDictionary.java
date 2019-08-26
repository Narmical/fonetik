package com.hoardingsinc.phoneticskeyboard.pronounceationdictionary;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.room.Room;

import com.hoardingsinc.phoneticskeyboard.rawdictionary.ArpabetToIpaConverter;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.CmuPronouncingDictionary;
import com.hoardingsinc.phoneticskeyboard.rawdictionary.RawDictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class RoomPronunciationDictionary extends PronunciationDictionary {

    private PronunciationDao pronunciationDao;

    public RoomPronunciationDictionary(Context context, RawDictionary rawDictionary) throws IOException {
        PronunciationDB db = Room.inMemoryDatabaseBuilder(context, PronunciationDB.class).build();
        this.pronunciationDao = db.pronunciationDao();
        this.loadDictionary(rawDictionary);
    }

    public List<String> exactMatch(String ipa) {
        List<String> spellings = new ArrayList<>();
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

    void loadDictionary(RawDictionary rawDictionary) {
        for (Pair<String, String> entry : rawDictionary) {
            String ipa = entry.first;
            String word = entry.second;

            Pronunciation pronunciation = new Pronunciation();
            pronunciation.setIpa(ipa);
            pronunciation.setSpellings(word);

            Log.i("roomdb", pronunciation.getSpellings());
            pronunciationDao.insert(pronunciation);
        }
    }
}
