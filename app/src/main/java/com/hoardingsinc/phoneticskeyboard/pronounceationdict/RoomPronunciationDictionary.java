package com.hoardingsinc.phoneticskeyboard.pronounceationdict;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomPronunciationDictionary implements PronunciationDictionary {

    private ArpabetToIpaConverter arpabetToIpaConverter;
    private PronunciationDB pronunciationDB;
    private PronunciationDao pronunciationDao;

    public RoomPronunciationDictionary(Context context, ArpabetToIpaConverter arpabetToIpaConverter, BufferedReader pronunciationDictionary) throws IOException {
        this.arpabetToIpaConverter = arpabetToIpaConverter;
        PronunciationDB db = Room.inMemoryDatabaseBuilder(context, PronunciationDB.class).build();
        this.pronunciationDao = db.pronunciationDao();
        this.loadDictionary(pronunciationDictionary);
    }

    static String formatWord(String word) {
        return word.replaceAll("\\(\\d+\\)", "").toLowerCase();
    }

    Map<String, List<String>> loadDictionary(BufferedReader reader) throws IOException {
        Map<String, List<String>> dictionary = new HashMap<>();
        String thisLine;
        while ((thisLine = reader.readLine()) != null) {
            if (!thisLine.startsWith(";;;")) {
                String[] entry = thisLine.split("  ");
                String word = formatWord(entry[0]);
                String ipa = this.arpabetToIpaConverter.arpabetToIpa(entry[1]);
                Pronunciation pronunciation = new Pronunciation();
                pronunciation.setIpa(ipa);
                pronunciation.setSpellings(word);
                Log.i("roomdb", pronunciation.getSpellings());
                pronunciationDao.insert(pronunciation);
            }
        }
        return dictionary;
    }


    public List<String> exactMatch(String ipa) {
        List<String> spellings = new ArrayList<>();
        for (Pronunciation pronunciation : this.pronunciationDao.get(ipa)) {
            spellings.add(pronunciation.getSpellings());
        }
        return spellings;
    }

    public List<String> lookaheadMatch(String ipa, int maxSuggestions) {
        List<String> result = new ArrayList<>();
        for (Pronunciation pronunciation : this.pronunciationDao.getLikeIpa(ipa + "%")) {
            result.add(pronunciation.getSpellings());
        }
        if (result.size() > maxSuggestions)
            return result.subList(0, maxSuggestions);
        else
            return result;
    }

    public int numEntries() {
        return this.pronunciationDao.numEntries();
    }

}
