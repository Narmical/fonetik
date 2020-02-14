package com.narmical.fonetic.rawdictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArpabetToIpaConverter implements IpaConverter {

    private Map<String, String> mArpabetToIpaMap;
    private Pattern arpabetPattern = Pattern.compile("([A-Z]+)");

    public ArpabetToIpaConverter(BufferedReader arpabetToIpaMap) throws IOException {
        if (arpabetToIpaMap != null)
            this.mArpabetToIpaMap = this.loadMobyToIpaMap(arpabetToIpaMap);

    }

    @Override
    public List<String> convert(String arpabet) {
        StringBuilder ipa = new StringBuilder();
        for (String arpa : arpabet.split(" ")) {

            Matcher matcher = this.arpabetPattern.matcher(arpa);
            if (matcher.find()) {
                String key = matcher.group(1);
                if (this.mArpabetToIpaMap.containsKey(key)) {
                    ipa.append(this.mArpabetToIpaMap.get(key));
                } else {
                    throw new NoSuchElementException("cannot convert " + key + " to ipa");
                }
            } else {
                throw new NoSuchElementException("cannot convert " + arpa + " to ipa");
            }
        }
        if (ipa.length() > 0)
            return Arrays.asList(ipa.toString());
        else
            throw new NoSuchElementException("cannot encode " + arpabet + " in ipa");
    }

    Map<String, String> loadMobyToIpaMap(BufferedReader reader) throws IOException {
        Map<String, String> arpabetToIpaMap = new HashMap<>();
        String thisLine;
        while ((thisLine = reader.readLine()) != null) {
            String[] tokens = thisLine.split("\t");
            arpabetToIpaMap.put(tokens[0], tokens[1]);
        }
        return arpabetToIpaMap;
    }

}
