package com.narmical.fonetic.rawdictionary;

import android.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmoticonDictionaryIterator extends RawDictionaryIterator {

    String thisLine;

    EmoticonDictionaryIterator(BufferedReader reader) {
        super(reader, null);
    }

    @Override
    public boolean hasNext() {
        if (!this.hasNext)
            return false;
        try {
            do {
                thisLine = reader.readLine();
            } while (thisLine != null && thisLine.startsWith(";;;"));
        } catch (IOException e) {
            this.hasNext = false;
            e.printStackTrace();
        } finally {
            this.hasNext = thisLine != null;
        }
        return this.hasNext;
    }

    @Override
    public Pair<String, String> next() {
        String[] entry = thisLine.split(" ");
        String spelling = new StringBuilder().
                appendCodePoint(Integer.valueOf(entry[1], 16)).toString();
        return new Pair<>(entry[0], spelling);
    }
}
