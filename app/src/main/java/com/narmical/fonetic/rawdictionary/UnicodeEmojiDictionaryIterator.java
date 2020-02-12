package com.narmical.fonetic.rawdictionary;

import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnicodeEmojiDictionaryIterator extends RawDictionaryIterator {

    UnicodeEmojiDictionaryIterator(BufferedReader reader) {
        super(reader, null);
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
            pairs.add(new Pair<>(keyword, spelling.toString()));
        }
        return pairs;
    }
}
