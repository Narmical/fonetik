package com.narmical.fonetic.rawdictionary;

import android.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MobyPronunciatorIterator extends RawDictionaryIterator {

    MobyPronunciatorIterator(BufferedReader reader, IpaConverter ipaConverter) {
        super(reader, ipaConverter);
    }

    @Override
    public boolean hasNext() {
        if (!this.hasNext)
            return false;
        if (flowOver != null && flowOver.size() > 0)
            return true;
        try {
            do {
                thisLine = reader.readLine();
            } while (thisLine != null && thisLine.contains("_"));
        } catch (IOException e) {
            this.hasNext = false;
            e.printStackTrace();
        } finally {
            this.hasNext = thisLine != null;
        }
        return this.hasNext;
    }

    @Override
    public List<Pair<String, String>> _next(String thisLine) {
        String[] entry = thisLine.split(" ");
        String spelling = formatWord(entry[0]).replaceAll("/\\w+", "");
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        for (String ipa : this.ipaConverter.convert(entry[1])) {
            pairs.add(new Pair<>(ipa, spelling));
        }
        return pairs;
    }
}
