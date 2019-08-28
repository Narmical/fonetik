package com.hoardingsinc.phoneticskeyboard.rawdictionary;

import android.util.Pair;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.util.Iterator;

public class MobyPronunciator extends RawDictionary {

    public MobyPronunciator(BufferedReader reader, MobyToIpaConverter ipaConverter) {
        super(reader, ipaConverter);
    }

    @NonNull
    @Override
    public Iterator<Pair<String, String>> iterator() {
        return new MobyPronunciatorIterator(reader, ipaConverter);
    }
}
