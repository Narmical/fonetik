package com.narmical.fonetic.pronounceationdictionary;

import android.content.res.Resources;

import androidx.test.core.app.ApplicationProvider;

import com.narmical.fonetic.R;
import com.narmical.fonetic.rawdictionary.ArpabetToIpaConverter;
import com.narmical.fonetic.rawdictionary.CmuPronouncingDictionary;
import com.narmical.fonetic.rawdictionary.MobyPronunciator;
import com.narmical.fonetic.rawdictionary.MobyToIpaConverter;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeSet;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

public class BuildDictFromFilesTest {
    Resources res = getInstrumentation().getTargetContext().getResources();

    @Test
    public void processCmuDictInMemoryTest() throws IOException {
        Set<String> expected = new TreeSet<>();
        BufferedReader rawFile = new BufferedReader(
                new InputStreamReader(
                        this.res.openRawResource(R.raw.cmudict)));

        String thisLine;
        while ((thisLine = rawFile.readLine()) != null) {
            if (!thisLine.startsWith(";;;")) {
                String[] tokens = thisLine.split("  ");
                expected.add(tokens[1].replaceAll("\\d", "").replaceAll("\\W", ""));
            }
        }

        InMemoryPronunciationDictionary dict = new InMemoryPronunciationDictionary(
                ApplicationProvider.getApplicationContext(),
                new CmuPronouncingDictionary(
                        new BufferedReader(
                                new InputStreamReader(
                                        this.res.openRawResource(R.raw.cmudict)
                                )
                        ),
                        new ArpabetToIpaConverter(
                                new BufferedReader(
                                        new InputStreamReader(
                                                this.res.openRawResource(R.raw.arpabet_to_ipa)
                                        )
                                )
                        )
                )
        );

        // assert that dict object has name number of unique pronunciations as the input file
        assertThat(dict.numEntries(), equalTo(expected.size()));
    }

    @Test
    public void processMobyPronunciatorInMemoryTest() throws IOException {
        Set<String> expected = new TreeSet<>();
        BufferedReader rawFile = new BufferedReader(
                new InputStreamReader(
                        this.res.openRawResource(R.raw.mpron)));

        String thisLine;
        while ((thisLine = rawFile.readLine()) != null) {
            if (!thisLine.startsWith(";;;")) {
                String[] tokens = thisLine.split(" ");
                expected.add(tokens[1].replaceAll("\\d", "").replaceAll("\\W", ""));
            }
        }

        InMemoryPronunciationDictionary dict = new InMemoryPronunciationDictionary(
                ApplicationProvider.getApplicationContext(),
                new MobyPronunciator(
                        new BufferedReader(
                                new InputStreamReader(
                                        this.res.openRawResource(R.raw.mpron)
                                )
                        ),
                        new MobyToIpaConverter(
                                new BufferedReader(
                                        new InputStreamReader(
                                                this.res.openRawResource(R.raw.mpront_to_ipa)
                                        )
                                )
                        )
                )
        );

        // assert that dict object has name number of unique pronunciations as the input file
        assertThat(dict.numEntries(), greaterThan(expected.size()));
    }

    @Test
    public void processCmuDictRoomTest() throws IOException {
        Set<String> expected = new TreeSet<>();
        BufferedReader rawFile = new BufferedReader(
                new InputStreamReader(
                        this.res.openRawResource(R.raw.cmudict)));

        String thisLine;
        while ((thisLine = rawFile.readLine()) != null) {
            if (!thisLine.startsWith(";;;")) {
                String[] tokens = thisLine.split("  ");
                expected.add(tokens[1].replaceAll("\\d", "").replaceAll("\\W", ""));
            }
        }

        RoomPronunciationDictionary dict = new RoomPronunciationDictionary(
                ApplicationProvider.getApplicationContext(),
                new CmuPronouncingDictionary(
                        new BufferedReader(
                                new InputStreamReader(
                                        this.res.openRawResource(R.raw.cmudict)
                                )
                        ),
                        new ArpabetToIpaConverter(
                                new BufferedReader(
                                        new InputStreamReader(
                                                this.res.openRawResource(R.raw.arpabet_to_ipa)
                                        )
                                )
                        )
                )
        );

        // assert that dict object has name number of unique pronunciations as the input file
        assertThat(dict.numEntries(), equalTo(expected.size()));
    }

    @Test
    public void processMobyPronunciatorRoomTest() throws IOException {
        Set<String> expected = new TreeSet<>();
        BufferedReader rawFile = new BufferedReader(
                new InputStreamReader(
                        this.res.openRawResource(R.raw.mpron)));

        String thisLine;
        while ((thisLine = rawFile.readLine()) != null) {
            if (!thisLine.startsWith(";;;")) {
                String[] tokens = thisLine.split(" ");
                expected.add(tokens[1].replaceAll("\\d", "").replaceAll("\\W", ""));
            }
        }

        RoomPronunciationDictionary dict = new RoomPronunciationDictionary(
                ApplicationProvider.getApplicationContext(),
                new MobyPronunciator(
                        new BufferedReader(
                                new InputStreamReader(
                                        this.res.openRawResource(R.raw.mpron)
                                )
                        ),
                        new MobyToIpaConverter(
                                new BufferedReader(
                                        new InputStreamReader(
                                                this.res.openRawResource(R.raw.mpront_to_ipa)
                                        )
                                )
                        )
                )
        );

        // assert that dict object has name number of unique pronunciations as the input file
        assertThat(dict.numEntries(), greaterThan(expected.size()));
    }
}
