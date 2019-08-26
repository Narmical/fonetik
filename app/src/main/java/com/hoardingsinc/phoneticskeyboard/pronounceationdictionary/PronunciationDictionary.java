package com.hoardingsinc.phoneticskeyboard.pronounceationdictionary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;

abstract class PronunciationDictionary {

    public abstract List<String> exactMatch(String ipa);

    public List<String> getSuggestions(String ipa, int maxSuggestions) {
        List<String> result = new ArrayList<>();
        List<String> exact = exactMatch(ipa);
        if (exact != null) {
            result.addAll(exact);
            result.sort(new StringLengthComparator());
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

    protected abstract SortedSet<String> lookAheadMatch(String ipa);

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
