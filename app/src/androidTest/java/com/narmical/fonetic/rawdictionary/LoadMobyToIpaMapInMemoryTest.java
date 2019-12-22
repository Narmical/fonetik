package com.narmical.fonetic.rawdictionary;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;


public class LoadMobyToIpaMapInMemoryTest {

    @Test
    public void loadMobyToIpaMapTest() throws IOException {
        MobyToIpaConverter mobyToIpaConverter = new MobyToIpaConverter(null);

        Map<String, ArrayList<String>> expected = new HashMap<String, ArrayList<String>>() {
            {
                put("/-/", new ArrayList<>(Arrays.asList("ə")));
                put("/&/", new ArrayList<>(Arrays.asList("æ")));
                put("/@/", new ArrayList<>(Arrays.asList("ʌ", "ə")));
                put("/[@]/", new ArrayList<>(Arrays.asList("ɜ", "ə")));
                put("/A/", new ArrayList<>(Arrays.asList("ɑ", "ɑː")));
            }
        };

        Map<String, List<String>> actual = mobyToIpaConverter.loadMobyToIpaMap(new BufferedReader(new StringReader("/&/\tæ\n" +
                "/-/\tə\n" +
                "/@/\tʌ, ə\n" +
                "/[@]/\tɜ, ə\n" +
                "/A/\tɑ, ɑː\n")));

        assertThat(actual.entrySet().toArray(), arrayContainingInAnyOrder(expected.entrySet().toArray()));
    }

}