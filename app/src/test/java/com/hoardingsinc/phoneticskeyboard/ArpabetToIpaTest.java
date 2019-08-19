package com.hoardingsinc.phoneticskeyboard;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ArpabetToIpaTest {

    PronunciationDict pronunciationDict = new PronunciationDict(new BufferedReader(new StringReader("AA\tɑ\n" +
            "AE\tæ\n" +
            "AH\tʌ")), null);

    public ArpabetToIpaTest() throws IOException {
    }

    @Test
    public void arpabetToIpaSingle() {
        assertThat(pronunciationDict.arpabetToIpa("AA"), equalTo("ɑ"));
    }

    @Test
    public void arpabetToIpaDouble() {
        assertThat(pronunciationDict.arpabetToIpa("AA AA"), equalTo("ɑɑ"));
    }

    @Test
    public void arpabetToIpaIgnoreAuxSymbols() {
        String[] auxSymbols = new String[]{"0", "1", "2", "3", "-", "!", "+", "/", "#", ":", ":1", ":2", "3", ".", "?", "."};
        for (String symbol : auxSymbols) {
            assertThat(pronunciationDict.arpabetToIpa("AA" + symbol + " AA"), equalTo("ɑɑ"));
        }
    }

    @Test
    public void arpabetToIpaTwo() {
        assertThat(pronunciationDict.arpabetToIpa("AA AE"), equalTo("ɑæ"));
    }

    @Test(expected = NoSuchElementException.class)
    public void arpabetToIpaMissing() {
        pronunciationDict.arpabetToIpa("AAAE");
    }
}
