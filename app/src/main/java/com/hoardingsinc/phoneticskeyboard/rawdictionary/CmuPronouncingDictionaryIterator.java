package com.hoardingsinc.phoneticskeyboard.rawdictionary;

import android.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class CmuPronouncingDictionaryIterator extends RawDictionaryIterator {

    String thisLine;

    CmuPronouncingDictionaryIterator(BufferedReader reader, ArpabetToIpaConverter ipaConverter) {
        super(reader, ipaConverter);
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
        String[] entry = thisLine.split("  ");
        String word = formatWord(entry[0]);
        List<String> ipa = this.ipaConverter.convertToIpa(entry[1]);
        return new Pair<>(ipa.get(0), word);
    }
}
