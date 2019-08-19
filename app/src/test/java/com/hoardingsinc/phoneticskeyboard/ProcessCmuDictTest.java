package com.hoardingsinc.phoneticskeyboard;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ProcessCmuDictTest {

    @Test
    public void processCmuDictTest() throws IOException {
        Set<String> expected = new TreeSet<>();
        BufferedReader rawFile = new BufferedReader(
                new FileReader("src/main/res/raw/cmudict.txt")
        );

        String thisLine;
        while ((thisLine = rawFile.readLine()) != null) {
            if (!thisLine.startsWith(";;;")) {
                String[] tokens = thisLine.split("  ");
                expected.add(tokens[1].replaceAll("\\d", "").replaceAll("\\W", ""));
            }
        }

        PronunciationDict dict = new PronunciationDict(
                new BufferedReader(
                        new FileReader("src/main/res/raw/arpabet_to_ipa.txt")),
                new BufferedReader(
                        new FileReader("src/main/res/raw/cmudict.txt")
                )
        );

        // assert that dict object has name number of unique pronunciations as the input file
        assertThat(dict.numEntries(), equalTo(expected.size()));
    }
}
