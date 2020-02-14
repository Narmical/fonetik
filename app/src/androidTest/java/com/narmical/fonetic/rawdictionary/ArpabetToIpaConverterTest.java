package com.narmical.fonetic.rawdictionary;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ArpabetToIpaConverterTest {

    ArpabetToIpaConverter arpabetToIpaConverter = new ArpabetToIpaConverter(new BufferedReader(new StringReader("AA\tɑ\n" +
            "AE\tæ\n" +
            "AH\tʌ")));

    public ArpabetToIpaConverterTest() throws IOException {
    }

    @Test
    public void arpabetToIpaSingle() {
        assertThat(arpabetToIpaConverter.convert("AA").get(0), equalTo("ɑ"));
    }

    @Test
    public void arpabetToIpaDouble() {
        assertThat(arpabetToIpaConverter.convert("AA AA").get(0), equalTo("ɑɑ"));
    }

    @Test
    public void arpabetToIpaIgnoreAuxSymbols() {
        String[] auxSymbols = new String[]{"0", "1", "2", "3", "-", "!", "+", "/", "#", ":", ":1", ":2", "3", ".", "?", "."};
        for (String symbol : auxSymbols) {
            assertThat(arpabetToIpaConverter.convert("AA" + symbol + " AA").get(0), equalTo("ɑɑ"));
        }
    }

    @Test
    public void arpabetToIpaTwo() {
        assertThat(arpabetToIpaConverter.convert("AA AE").get(0), equalTo("ɑæ"));
    }

    @Test(expected = NoSuchElementException.class)
    public void arpabetToIpaMissing() {
        arpabetToIpaConverter.convert("AAAE");
    }
}
