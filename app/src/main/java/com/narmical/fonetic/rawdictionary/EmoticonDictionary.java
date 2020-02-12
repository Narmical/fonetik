package com.narmical.fonetic.rawdictionary;

import android.util.Pair;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.util.Iterator;

public class EmoticonDictionary extends RawDictionary {

    public EmoticonDictionary(BufferedReader reader) {
        super(reader, null);
    }

    @NonNull
    @Override
    public Iterator<Pair<String, String>> iterator() {
        return new EmoticonDictionaryIterator(reader);
    }
}
