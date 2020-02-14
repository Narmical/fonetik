package com.narmical.fonetic.rawdictionary;

import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UnicodeEmojiDictionaryIterator extends RawDictionaryIterator {

    UnicodeEmojiDictionaryIterator(BufferedReader reader, IpaConverter ipaConverter) {
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
            } while (thisLine != null && (thisLine.equals("") || thisLine.startsWith("#")));
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
        String[] entry = thisLine.split("[;#]");

        String[] description = entry[2].split("E\\d+.\\d+ ");
        if (description.length < 2) {
            Log.i("Emoji", thisLine);
        }

        StringBuilder spelling = new StringBuilder();
        for (String codePoint : entry[0].trim().split(" ")) {
            spelling.appendCodePoint(Integer.valueOf(codePoint, 16));
        }
        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        for (String keyword : description[1].split(" ")) {
            for (String keyIpa : this.ipaConverter.convert(keyword
                    .replaceAll("\\W", ""))) {
                pairs.add(new Pair<>(":" + keyIpa, spelling.toString()));
            }
        }
        return pairs;
    }
}
