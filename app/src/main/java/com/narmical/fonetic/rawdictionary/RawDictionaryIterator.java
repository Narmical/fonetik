package com.narmical.fonetic.rawdictionary;

import android.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract class RawDictionaryIterator implements Iterator<Pair<String, String>> {
    BufferedReader reader;
    IpaConverter ipaConverter;
    boolean hasNext = true;
    List<Pair<String, String>> flowOver;
    String thisLine;

    RawDictionaryIterator(BufferedReader reader, IpaConverter ipaConverter) {
        this.reader = reader;
        this.ipaConverter = ipaConverter;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public Pair<String, String> next() {
        if (flowOver != null && flowOver.size() > 0) {
            Pair<String, String> pair = flowOver.get(0);
            flowOver.remove(0);
            return pair;
        }

        List<Pair<String, String>> pairs = this._next(thisLine);

        if (pairs.size() > 1)
            flowOver = pairs.subList(1, pairs.size());
        return pairs.get(0);
    }

    abstract protected List<Pair<String, String>> _next(String thisLine);

    String formatWord(String word) {
        return word.replaceAll("\\(\\d+\\)", "");
    }
}
