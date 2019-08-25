package com.hoardingsinc.phoneticskeyboard.pronounceationdict;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryPronunciationDictionary implements PronunciationDictionary {


    private Map<String, List<String>> mDictionary;
    private PronunciationDB mDatabase;
    private ArpabetToIpaConverter mArpabetToIpaConverter;

    public InMemoryPronunciationDictionary(Context context, ArpabetToIpaConverter arpabetToIpaConverter, BufferedReader pronunciationDictionary) throws IOException {
        this.mArpabetToIpaConverter = arpabetToIpaConverter;
        //this.mDatabase = Room.databaseBuilder(context.getApplicationContext(), PronunciationDB.class, "PronunciationDb").build();
        if (pronunciationDictionary != null)
            this.mDictionary = this.loadDictionary(pronunciationDictionary);

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
                String ipa = this.mArpabetToIpaConverter.arpabetToIpa(entry[1]);
                if (dictionary.containsKey(ipa)) {
                    List<String> wordList = dictionary.get(ipa);
                    wordList.add(word);
                } else {
                    ArrayList<String> wordList = new ArrayList<>();
                    wordList.add(word);
                    dictionary.put(ipa, wordList);
                }
            }
        }
        return dictionary;
    }

    public List<String> exactMatch(String ipa) {
        return this.mDictionary.get(ipa);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<String> lookaheadMatch(String ipa, int maxSuggestions) {
        List<String> result = new ArrayList<>();
        List<String> exact = exactMatch(ipa);
        if (exact != null) {
            result.addAll(exact);
            result.sort(new StringLengthComparator());
        }

        List<String> lookahead = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : this.mDictionary.entrySet()) {
            if (entry.getKey().startsWith(ipa)) {
                lookahead.addAll(entry.getValue());
            }
        }
        lookahead.sort(new StringLengthComparator());
        result.addAll(lookahead);
        if (result.size() > maxSuggestions)
            return result.subList(0, maxSuggestions);
        else
            return result;
    }

    public int numEntries() {
        return this.mDictionary.size();
    }

    class StringLengthComparator implements Comparator<String> {
        public int compare(String o1, String o2) {
            return Integer.compare(o1.length(), o2.length());
        }
    }

}
