package com.hoardingsinc.phoneticskeyboard.rawdictionary;

import android.util.Pair;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.util.Iterator;

public class MobyPronunciator extends RawDictionary {

    public MobyPronunciator(BufferedReader reader, IpaConverter ipaConverter) {
        super(reader, ipaConverter);
    }

    @NonNull
    @Override
    public Iterator<Pair<String, String>> iterator() {
        return new MobyPronounciatorIterator(reader, ipaConverter);
    }
}
