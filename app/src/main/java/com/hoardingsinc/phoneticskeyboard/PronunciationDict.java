package com.hoardingsinc.phoneticskeyboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PronunciationDict {

    private Map<String, String> mArpabetToIpaMap;
    private Map<String, List<String>> mDictionary;
    private Pattern arpabetPattern = Pattern.compile("([A-Z]+)");

    public PronunciationDict(BufferedReader arpabetToIpaMap, BufferedReader pronunciationDictionary) throws IOException {
        if (arpabetToIpaMap != null)
            this.mArpabetToIpaMap = this.loadArpabetToIpaMap(arpabetToIpaMap);
        if (pronunciationDictionary != null)
            this.mDictionary = this.loadDictionary(pronunciationDictionary);
    }

    Map<String, String> loadArpabetToIpaMap(BufferedReader reader) throws IOException {
        Map<String, String> arpabetToIpaMap = new HashMap<>();
        String thisLine;
        while ((thisLine = reader.readLine()) != null) {
            String[] tokens = thisLine.split("\t");
            arpabetToIpaMap.put(tokens[0], tokens[1]);
        }
        return arpabetToIpaMap;
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

            Matcher matcher = this.arpabetPattern.matcher(arpa);
            if (matcher.find()) {
                String key = matcher.group(1);
                if (this.mArpabetToIpaMap.containsKey(key)) {
                    ipa.append(this.mArpabetToIpaMap.get(key));
                } else {
                    throw new NoSuchElementException("cannot convert " + key + " to ipa");
                }
            } else {
                throw new NoSuchElementException("cannot convert " + arpa + " to ipa");
            }
        }
        if (ipa.length() > 0)
            return ipa.toString();
        else
            throw new NoSuchElementException("cannot encode " + arpabet + " in ipa");
    }

    public List<String> exactMatch(String ipa) {
        return this.mDictionary.get(ipa);
    }

    public List<String> lookaheadMatch(String ipa) {
        ArrayList<String> result = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : this.mDictionary.entrySet()) {
            if (entry.getKey().startsWith(ipa)) {
                result.addAll(entry.getValue());
            }
        }
        return result;
    }

    public int numEntries() {
        return this.mDictionary.size();
    }

}
