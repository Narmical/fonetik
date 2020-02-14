package com.narmical.fonetic.rawdictionary;

import android.util.Pair;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

public class UnicodeEmojiDictionary extends RawDictionary {

    public UnicodeEmojiDictionary(BufferedReader reader) {
        super(reader, null);
    }

    @NonNull
    @Override
    public Iterator<Pair<String, String>> iterator() {
        try {
            return new UnicodeEmojiDictionaryIterator(reader, new MapConverter(this.pronuciationMap));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
