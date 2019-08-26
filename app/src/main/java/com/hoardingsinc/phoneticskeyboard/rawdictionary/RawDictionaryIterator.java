package com.hoardingsinc.phoneticskeyboard.rawdictionary;

import android.util.Pair;

import java.io.BufferedReader;
import java.util.Iterator;

abstract class RawDictionaryIterator implements Iterator<Pair<String, String>> {
    BufferedReader reader;
    ArpabetToIpaConverter ipaConverter;
    boolean hasNext = true;

    RawDictionaryIterator(BufferedReader reader, ArpabetToIpaConverter ipaConverter) {
        this.reader = reader;
        this.ipaConverter = ipaConverter;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    String formatWord(String word) {
        return word.replaceAll("\\(\\d+\\)", "").toLowerCase();
    }
}
