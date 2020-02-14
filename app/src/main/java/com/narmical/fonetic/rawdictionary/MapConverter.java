package com.narmical.fonetic.rawdictionary;

import com.narmical.fonetic.pronounceationdictionary.Pronunciation;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapConverter implements IpaConverter {

    private final Map<String, List<Pronunciation>> map;

    public MapConverter(Map<String, List<Pronunciation>> map) throws IOException {
        this.map = map;

    }

    @Override
    public List<String> convert(String spelling) {
        if (this.map.containsKey(spelling))
            return this.map.get(spelling).stream().map(Pronunciation::getIpa).collect(Collectors.toList());
        else
            return Arrays.asList(spelling);
    }

}
