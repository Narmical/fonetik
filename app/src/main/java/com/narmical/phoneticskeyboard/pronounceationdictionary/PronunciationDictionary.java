package com.narmical.phoneticskeyboard.pronounceationdictionary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public abstract class PronunciationDictionary {

    public abstract Set<String> exactMatch(String ipa);

    public List<String> getSuggestions(String ipa, int maxSuggestions) {
        List<String> result = new ArrayList<>();
        Set<String> exact = exactMatch(ipa);
        if (exact != null) {
            result.addAll(exact);
        }

        SortedSet<String> lookahead = this.lookAheadMatch(ipa);
        lookahead.removeAll(result);
        result.addAll(lookahead);
        if (result.size() > maxSuggestions)
            return result.subList(0, maxSuggestions);
        else
            return result;
    }

    public abstract int numEntries();

    public abstract void recordSpellingSelected(String ipa, String spelling);

    protected abstract SortedSet<String> lookAheadMatch(String ipa);

    protected class FrequencyComparator implements Comparator<Pronunciation> {
        public int compare(Pronunciation o1, Pronunciation o2) {
            int result;
            result = o1.getSpelling().compareToIgnoreCase(o2.getSpelling());
            // don't add frequencies together for same spelling because db keeps all words with same
            // spelling at the same frequency count
            if (result != 0) {
                result = -1 * Integer.compare(o1.getFrequency(), o2.getFrequency());
                if (result == 0) {
                    result = Integer.compare(o1.getSpelling().length(), o2.getSpelling().length());
                    if (result == 0) {
                        result = o1.compareTo(o2);
                    }
                }
            }
            return result;
        }
    }

    protected class StringLengthComparator implements Comparator<String> {
        public int compare(String o1, String o2) {
            int result = Integer.compare(o1.length(), o2.length());
            if (result == 0) {
                return o1.compareTo(o2);
            } else {
                return result;
            }
        }
    }
}
