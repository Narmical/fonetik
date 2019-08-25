package com.hoardingsinc.phoneticskeyboard.pronounceationdict;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ArpabetToIpaConverterTest {

    ArpabetToIpaConverter arpabetToIpaConverter = new ArpabetToIpaConverter(new BufferedReader(new StringReader("AA\tɑ\n" +
            "AE\tæ\n" +
            "AH\tʌ")));

    public ArpabetToIpaConverterTest() throws IOException {
    }

    @Test
    public void arpabetToIpaSingle() {
        assertThat(arpabetToIpaConverter.arpabetToIpa("AA"), equalTo("ɑ"));
    }

    @Test
    public void arpabetToIpaDouble() {
        assertThat(arpabetToIpaConverter.arpabetToIpa("AA AA"), equalTo("ɑɑ"));
    }

    @Test
    public void arpabetToIpaIgnoreAuxSymbols() {
        String[] auxSymbols = new String[]{"0", "1", "2", "3", "-", "!", "+", "/", "#", ":", ":1", ":2", "3", ".", "?", "."};
        for (String symbol : auxSymbols) {
            assertThat(arpabetToIpaConverter.arpabetToIpa("AA" + symbol + " AA"), equalTo("ɑɑ"));
        }
    }

    @Test
    public void arpabetToIpaTwo() {
        assertThat(arpabetToIpaConverter.arpabetToIpa("AA AE"), equalTo("ɑæ"));
    }

    @Test(expected = NoSuchElementException.class)
    public void arpabetToIpaMissing() {
        arpabetToIpaConverter.arpabetToIpa("AAAE");
    }
}
