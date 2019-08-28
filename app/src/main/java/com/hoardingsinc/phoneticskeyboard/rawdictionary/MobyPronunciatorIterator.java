package com.hoardingsinc.phoneticskeyboard.rawdictionary;

import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MobyPronunciatorIterator extends RawDictionaryIterator {

    String thisLine;
    private List<Pair<String, String>> flowOver;

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
    public Pair<String, String> next() {
        if (flowOver != null && flowOver.size() > 0) {
            Pair<String, String> pair = flowOver.get(0);
            flowOver.remove(0);
            return pair;
        }
        //Log.d("DictionaryReadLine", thisLine);
        String[] entry = thisLine.split(" ");
        String spelling = formatWord(entry[0]).replaceAll("/\\w+", "");
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        for (String ipa : this.ipaConverter.convertToIpa(entry[1])) {
            pairs.add(new Pair<>(ipa, spelling));
        }
        if (pairs.size() > 1)
            flowOver = pairs.subList(1, pairs.size());
        return pairs.get(0);
    }
}
