package com.narmical.fonetic.rawdictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WordFrequency {

    private Map<String, Integer> wordFrequency;
    private int count = 0;

    public WordFrequency(BufferedReader wordFrequency) throws IOException {
        if (wordFrequency != null)
            this.wordFrequency = this.loadWordFrequency(wordFrequency);

    }

    public int getFrequencyRank(String spelling) {
        if (this.wordFrequency.containsKey(spelling.toLowerCase()))
            return this.count - this.wordFrequency.get(spelling.toLowerCase()) + 1;
        else
            return 0;
    }

    Map<String, Integer> loadWordFrequency(BufferedReader reader) throws IOException {
        Map<String, Integer> frequencyMap = new HashMap<>();
        String thisLine;
        while ((thisLine = reader.readLine()) != null) {
            String[] tokens = thisLine.split("\t");
            frequencyMap.put(tokens[1].toLowerCase(), Integer.parseInt(tokens[0]));
            this.count++;
        }
        return frequencyMap;
    }

}
