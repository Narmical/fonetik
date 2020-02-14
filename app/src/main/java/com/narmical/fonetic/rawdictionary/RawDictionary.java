package com.narmical.fonetic.rawdictionary;

import android.util.Pair;

import com.narmical.fonetic.pronounceationdictionary.Pronunciation;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;

public abstract class RawDictionary implements Iterable<Pair<String, String>> {

    BufferedReader reader;
    IpaConverter ipaConverter;
    protected Map<String, List<Pronunciation>> pronuciationMap;

    public RawDictionary(BufferedReader reader, IpaConverter ipaConverter) {
        this.reader = reader;
        this.ipaConverter = ipaConverter;
    }

    public void setPronoucations (Map<String, List<Pronunciation>> map)
    {
        this.pronuciationMap = map;
    }


    @Override
    public void forEach(Consumer<? super Pair<String, String>> action) {

    }

    @Override
    public Spliterator<Pair<String, String>> spliterator() {
        return null;
    }

}
