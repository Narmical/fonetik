package com.hoardingsinc.phoneticskeyboard;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.hoardingsinc.phoneticskeyboard.pronounceationdict.Pronunciation;
import com.hoardingsinc.phoneticskeyboard.pronounceationdict.PronunciationDB;
import com.hoardingsinc.phoneticskeyboard.pronounceationdict.PronunciationDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

@RunWith(AndroidJUnit4.class)
public class TestDatabase {
    private PronunciationDao pronunciationDao;
    private PronunciationDB db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, PronunciationDB.class).build();
        pronunciationDao = db.pronunciationDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testWritePronunciation() throws Exception {
        Pronunciation pronunciation = new Pronunciation();
        pronunciation.setIpa("Hello");
        pronunciation.setSpellings("hello");
        List<Pronunciation> pronunciationList = new ArrayList<>();
        pronunciationList.add(pronunciation);
        pronunciationDao.insertAll(pronunciationList);
        List<Pronunciation> allPronunciations = pronunciationDao.getAll();
        assertThat(allPronunciations, containsInAnyOrder(pronunciationList.toArray()));
    }

    @Test
    public void testLikePronunciation() throws Exception {

        List<Pronunciation> pronunciationList = new ArrayList<>();
        List<Pronunciation> expected = new ArrayList<>();

        List<String> codas = new ArrayList<String>(Arrays.asList("r", "s", "t", "l", "n", "e"));

        for (String coda : codas) {
            Pronunciation pronunciation = new Pronunciation();
            pronunciation.setIpa("Hell" + coda);
            pronunciation.setSpellings("hell" + coda);
            pronunciationList.add(pronunciation);
            expected.add(pronunciation);
        }

        for (String coda : codas) {
            Pronunciation pronunciation = new Pronunciation();
            pronunciation.setIpa("Hall" + coda);
            pronunciation.setSpellings("hall" + coda);
            pronunciationList.add(pronunciation);
        }


        pronunciationDao.insertAll(pronunciationList);
        List<Pronunciation> actual = pronunciationDao.getLikeIpa("Hell%");
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }
}