package com.hoardingsinc.phoneticskeyboard.pronounceationdictionary;

import android.content.Context;
import android.util.Pair;

import com.hoardingsinc.phoneticskeyboard.rawdictionary.RawDictionary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class InMemoryPronunciationDictionary extends PronunciationDictionary {


    private Map<String, Set<String>> dictionary;

    public InMemoryPronunciationDictionary(Context context, RawDictionary rawDictionary) throws IOException {
        if (rawDictionary != null) {
            List<RawDictionary> rawDictionaries = new ArrayList<>();
            rawDictionaries.add(rawDictionary);
            this.dictionary = this.loadDictionary(rawDictionaries);
        }

    }


    public InMemoryPronunciationDictionary(Context context, List<RawDictionary> rawDictionaries) throws IOException {
        if (rawDictionaries != null)
            this.dictionary = this.loadDictionary(rawDictionaries);

    }

    public SortedSet<String> exactMatch(String ipa) {
        SortedSet sortedSet = new TreeSet<>(new StringLengthComparator());
        Set<String> list = this.dictionary.get(ipa);
        if (list != null)
            sortedSet.addAll(list);
        return sortedSet;
    }

    public SortedSet<String> lookAheadMatch(String ipa) {
        SortedSet<String> lookahead = new TreeSet<>(new StringLengthComparator());
        for (Map.Entry<String, Set<String>> entry : this.dictionary.entrySet()) {
            if (entry.getKey().startsWith(ipa)) {
                lookahead.addAll(entry.getValue());
            }
        }
        return lookahead;
    }

    public int numEntries() {
        return this.dictionary.size();


    }

    @Override
    public void recordSpellingSelected(String ips, String spelling) {

    }

    Map<String, Set<String>> loadDictionary(List<RawDictionary> rawDictionaries) throws IOException {
        Map<String, Set<String>> dictionary = new HashMap<>();
        for (RawDictionary rawDictionary : rawDictionaries) {
            for (Pair<String, String> entry : rawDictionary) {
                String ipa = entry.first;
                String word = entry.second;
                if (dictionary.containsKey(ipa)) {
                    Set<String> wordList = dictionary.get(ipa);
                    wordList.add(word);
                } else {
                    TreeSet<String> wordList = new TreeSet<>(new StringLengthComparator());
                    wordList.add(word);
                    dictionary.put(ipa, wordList);
                }
            }
        }
        return dictionary;
    }
}
