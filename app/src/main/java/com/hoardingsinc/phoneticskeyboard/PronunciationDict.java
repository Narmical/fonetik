package com.hoardingsinc.phoneticskeyboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PronunciationDict {

    public PronunciationDict(BufferedReader arpabetToIpaMap, BufferedReader pronunciationDictionary) throws IOException {
        if (arpabetToIpaMap != null)
            this.mArpabetToIpaMap = this.loadArpabetToIpaMap(arpabetToIpaMap);
        if (pronunciationDictionary != null)
            this.mDictionary = this.loadDictionary(pronunciationDictionary);
    }


    private Map<String, String> mArpabetToIpaMap;
    private Map<String, List<String>> mDictionary;

    Map<String, String> loadArpabetToIpaMap(BufferedReader reader) throws IOException {
        Map<String, String> arpabetToIpaMap = new HashMap<>();
        String thisLine;
        while ((thisLine = reader.readLine()) != null) {
            String[] tokens = thisLine.split("\t");
            arpabetToIpaMap.put(tokens[0], tokens[1]);
        }
        return arpabetToIpaMap;
    }

    Map<String, List<String>> loadDictionary(BufferedReader reader) throws IOException {
        Map<String, List<String>> dictionary = new HashMap<>();
        String thisLine;
        while ((thisLine = reader.readLine()) != null) {
            if (!thisLine.startsWith(";;;")) {
                String[] entry = thisLine.split("  ");
                String word = entry[0];
                String ipa = this.arpabetToIpa(entry[1]);
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

    String arpabetToIpa(String arpabet) {
        StringBuilder ipa = new StringBuilder();
        for (String arpa : arpabet.split(" ")) {
            if (this.mArpabetToIpaMap.containsKey(arpa)) {
                ipa.append(this.mArpabetToIpaMap.get(arpa));
            }
        }
        if (ipa.length() > 0)
            return ipa.toString();
        else
            return null;
    }

    public String exactMatch(String ipa) {
        return mArpabetToIpaMap.get(ipa);
    }
}
