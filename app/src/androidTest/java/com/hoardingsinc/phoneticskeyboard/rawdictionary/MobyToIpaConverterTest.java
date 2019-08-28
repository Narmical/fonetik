package com.hoardingsinc.phoneticskeyboard.rawdictionary;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

public class MobyToIpaConverterTest {

    MobyToIpaConverter mobyToIpaConverter = new MobyToIpaConverter(new BufferedReader(new StringReader("/&/\tæ\n" +
            "/-/\tə\n" +
            "/@/\tʌ, ə\n" +
            "/[@]/\tɜː, ə\n" +
            "/A/\tɑː\n" +
            "b\tb\n" +
            "r\tɹ\n" +
            "t\tt\n" +
            "w\tw\n" +
            "k\tk\n" +
            "w\tw\n")));

    public MobyToIpaConverterTest() throws IOException {
    }

    @Test
    public void mobyToIpaSingle() {
        List<String> expected = new ArrayList<>();
        expected.add("ɑː");
        List<String> actual = mobyToIpaConverter.convertToIpa("/A/");
        assertThat(actual.toArray(), arrayContainingInAnyOrder(expected.toArray()));
    }

    @Test
    public void mobyToIpaDouble() {
        ArrayList<String> expected = new ArrayList<>();
        expected.add("əʌ");
        expected.add("əə");
        expected.add("ʌə");
        expected.add("ʌʌ");
        assertThat(mobyToIpaConverter.convertToIpa("/@//@/").toArray(), arrayContainingInAnyOrder(expected.toArray()));
    }

    @Test
    public void mobyToIpaIgnoreAuxSymbols() {
        String[] auxSymbols = new String[]{"'", ","};

        for (String symbol : auxSymbols) {
            assertThat(mobyToIpaConverter.convertToIpa("/&/" + symbol + "/&/").toArray(), arrayContainingInAnyOrder(new String[]{"ææ"}));
        }
    }

    @Test
    public void mobyToIpaTwo() {
        assertThat(mobyToIpaConverter.convertToIpa("/&/b").toArray(), arrayContainingInAnyOrder(new String[]{"æb"}));
    }

    @Test
    public void mobyToIpaFullWord() {
        List<String> expected = new ArrayList<>();
        expected.add("ɑːɹtwɜːɹk");
        expected.add("ɑːɹtwəɹk");
        assertThat(mobyToIpaConverter.convertToIpa("'/A/rt,w/[@]/rk").toArray(), arrayContainingInAnyOrder(expected.toArray()));
    }
}
