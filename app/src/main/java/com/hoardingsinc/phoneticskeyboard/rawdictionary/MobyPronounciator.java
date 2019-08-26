package com.hoardingsinc.phoneticskeyboard.rawdictionary;

import android.util.Pair;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.util.Iterator;

public class MobyPronounciator extends RawDictionary {

    public MobyPronounciator(BufferedReader reader, ArpabetToIpaConverter ipaConverter) {
        super(reader, ipaConverter);
    }

    @NonNull
    @Override
    public Iterator<Pair<String, String>> iterator() {
        return new CmuPronouncingDictionaryIterator(reader, ipaConverter);
    }
}
