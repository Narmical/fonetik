package com.hoardingsinc.phoneticskeyboard.rawdictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class MobyToIpaConverter implements IpaConverter {

    private Map<String, List<String>> mobyToIpaMap;
    private Pattern arpabetPattern = Pattern.compile("([A-Z])");

    public MobyToIpaConverter(BufferedReader mobyToIpaMap) throws IOException {
        if (mobyToIpaMap != null)
            this.mobyToIpaMap = this.loadMobyToIpaMap(mobyToIpaMap);

    }

    @Override
    public List<String> convertToIpa(String moby) {
        List<StringBuilder> pronounciations = new ArrayList<>();
        pronounciations.add(new StringBuilder());
        for (String mob : moby.replace("'", "")
                .replace(",", "").split("/")) {

            String key = mob;
            if (this.mobyToIpaMap.containsKey(key)) {
                List<String> ipas = this.mobyToIpaMap.get(key);
                List<StringBuilder> adders = new ArrayList<>();
                for (StringBuilder pronounciation : pronounciations) {
                    if (ipas.size() == 2) {
                        StringBuilder added = new StringBuilder(pronounciation);
                        adders.add(added.append(ipas.get(1)));
                    }
                    pronounciation.append(ipas.get(0));
                }
                pronounciations.addAll(adders);
            } else if (key.equals("")) {
                continue;
            } else {
                throw new NoSuchElementException("cannot convert " + key + " to ipa");

            }
        }
        if (pronounciations.size() > 0)
            return pronounciations.stream().map(StringBuilder::toString).collect(toList());
        else
            throw new NoSuchElementException("cannot encode " + moby + " in ipa");
    }

    public Map<String, List<String>> loadMobyToIpaMap(BufferedReader reader) throws
            IOException {
        Map<String, List<String>> arpabetToIpaMap = new HashMap<>();
        String thisLine;
        while ((thisLine = reader.readLine()) != null) {
            String[] tokens = thisLine.split("\t");
            arpabetToIpaMap.put(tokens[0].replace("/", ""), Arrays.asList(tokens[1].split(", ")));
        }
        return arpabetToIpaMap;
    }

}
