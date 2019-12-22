package com.narmical.fonetic.rawdictionary;

import android.util.Pair;

import java.io.BufferedReader;
import java.util.Spliterator;
import java.util.function.Consumer;

public abstract class RawDictionary implements Iterable<Pair<String, String>> {

    BufferedReader reader;
    IpaConverter ipaConverter;

    public RawDictionary(BufferedReader reader, IpaConverter ipaConverter) {
        this.reader = reader;
        this.ipaConverter = ipaConverter;
    }


    @Override
    public void forEach(Consumer<? super Pair<String, String>> action) {

    }

    @Override
    public Spliterator<Pair<String, String>> spliterator() {
        return null;
    }

}
