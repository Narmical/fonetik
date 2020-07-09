package com.narmical.fonetic.pronounceationdictionary;

import android.content.Context;
import android.util.Pair;

import androidx.room.Room;

import com.narmical.fonetic.MainActivity;
import com.narmical.fonetic.rawdictionary.RawDictionary;
import com.narmical.fonetic.rawdictionary.WordFrequency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class RoomPronunciationDictionary extends PronunciationDictionary {

    private PronunciationDao pronunciationDao;

    public RoomPronunciationDictionary(Context context) {
        PronunciationDB db = Room.databaseBuilder(context.getApplicationContext(), PronunciationDB.class, "pronunciation.db").build();
        this.pronunciationDao = db.pronunciationDao();
    }

    public RoomPronunciationDictionary(Context context, RawDictionary rawDictionary) {
        PronunciationDB db = Room.databaseBuilder(context.getApplicationContext(), PronunciationDB.class, "pronunciation.db").build();
        this.pronunciationDao = db.pronunciationDao();
        if (this.pronunciationDao.numEntries() == 0) {
            List<RawDictionary> rawDictionaries = new ArrayList<>();
            rawDictionaries.add(rawDictionary);
            this.loadDictionary(rawDictionaries);
        }
    }

    public RoomPronunciationDictionary(Context context, List<RawDictionary> rawDictionaries) {
        PronunciationDB db = Room.databaseBuilder(context.getApplicationContext(), PronunciationDB.class, "pronunciation.db").build();
        this.pronunciationDao = db.pronunciationDao();
        if (this.pronunciationDao.numEntries() == 0) {
            this.loadDictionary(rawDictionaries);
        }
    }

    @Override
    public Set<String> exactMatch(String ipa) {
        SortedSet<String> spellings = new TreeSet<>(new StringLengthComparator());
        spellings.addAll(this._exactMatch(ipa).stream().map(Pronunciation::getSpelling).collect(Collectors.toSet()));
        return spellings;
    }

    @Override
    public List<String> getSuggestions(String ipa, int maxSuggestions) {
        List<String> result = new ArrayList<>();
        SortedSet<Pronunciation> exact = this._exactMatch(ipa);
        SortedSet<Pronunciation> lookahead = this._lookAheadMatch(ipa);
        if (exact != null) {
            result.addAll(exact.stream().map(Pronunciation::getSpelling).collect(Collectors.toList()));
            lookahead.removeAll(exact);
        }

        result.addAll(lookahead.stream().map(Pronunciation::getSpelling).collect(Collectors.toList()));

        if (result.size() > maxSuggestions)
            return result.subList(0, maxSuggestions);
        else
            return result;
    }

    public int numEntries() {
        return this.pronunciationDao.numEntries();
    }

    @Override
    public void recordSpellingSelected(String ipa, String spelling) {

        this.pronunciationDao.incrementFrequency(spelling);

    }

    @Override
    protected SortedSet<String> lookAheadMatch(String ipa) {
        SortedSet<String> spellings = new TreeSet<>(new StringLengthComparator());
        spellings.addAll(this._lookAheadMatch(ipa).stream().map(Pronunciation::getSpelling).collect(Collectors.toSet()));
        return spellings;
    }

    @Override
    public Set<String> reverseLookup(String spelling) {
        SortedSet<Pronunciation> ipas = new TreeSet<>(new FrequencyComparator());
        ipas.addAll(this.pronunciationDao.reverseLookup(spelling));
        return ipas.stream().map(Pronunciation::getSpelling).collect(Collectors.toSet());
    }

    private SortedSet<Pronunciation> _exactMatch(String ipa) {
        SortedSet<Pronunciation> spellings = new TreeSet<>(new FrequencyComparator());
        spellings.addAll(this.pronunciationDao.get(ipa));
        return spellings;
    }

    private SortedSet<Pronunciation> _lookAheadMatch(String ipa) {
        SortedSet<Pronunciation> spellings = new TreeSet<>(new FrequencyComparator());
        spellings.addAll(this.pronunciationDao.getLikeIpa(ipa + "%"));
        return spellings;
        //return spellings.stream().map(Pronunciation::getSpelling).collect(Collectors.toList());
    }

    void loadDictionary(List<RawDictionary> rawDictionaries) {
        loadDictionary(rawDictionaries, null, 0, null);
    }

    public void loadDictionary(List<RawDictionary> rawDictionaries, MainActivity activity, int dictSize, WordFrequency wordFrequency) {
        int progress = 0;
        Map<String, List<Pronunciation>> pronunciations = new HashMap<>();
        for (RawDictionary rawDictionary : rawDictionaries) {
            rawDictionary.setPronoucations(pronunciations);
            for (Pair<String, String> entry : rawDictionary) {
                progress = progress + 1;
                String ipa = entry.first;
                String word = entry.second;


                Pronunciation pronunciation = new Pronunciation();
                pronunciation.setIpa(ipa);
                pronunciation.setSpelling(word);
                if (wordFrequency == null) {
                    pronunciation.setFrequency(0);
                } else {
                    pronunciation.setFrequency(wordFrequency.getFrequencyRank(word));
                }

                if (!pronunciations.containsKey(word))
                    pronunciations.put(word, new ArrayList<>());
                pronunciations.get(word).add(pronunciation);

                if (activity != null) {
                    final int progressPercent = progress * 100 / dictSize;
                    activity.runOnUiThread(() -> {
                        activity.progressBarText.setText("loading " + word + "[" + ipa + "]");
                        activity.progressBar.setProgress(progressPercent);
                    });
                }
            }

        }

        if (activity != null) {
            activity.runOnUiThread(() -> {
                activity.progressBarText.setText("Indexing pronunciation database");
            });
        }
        List pronunciationList = new ArrayList<>();
        pronunciations.entrySet().forEach(list -> pronunciationList.addAll(list.getValue()));

        pronunciationDao.insertAll(pronunciationList);

        activity.runOnUiThread(() -> {
            activity.progressBarText.setText("Dictionary Build Complete!");
            activity.progressBar.setProgress(100);
        });

    }
}
