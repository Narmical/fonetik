package com.narmical.fonetic.rawdictionary;

import com.narmical.fonetic.pronounceationdictionary.PronunciationDictionary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReverseRoomDBConverter implements IpaConverter {

    private final PronunciationDictionary dictionary;

    public ReverseRoomDBConverter(PronunciationDictionary dictionary) throws IOException {
        this.dictionary = dictionary;

    }

    @Override
    public List<String> convert(String spelling) {
        return new ArrayList<>(this.dictionary.reverseLookup(spelling));
    }

}
