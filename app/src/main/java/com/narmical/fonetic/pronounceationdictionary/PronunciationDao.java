package com.narmical.fonetic.pronounceationdictionary;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PronunciationDao {

    @Delete
    void delete(Pronunciation pronunciation);

    @Query("SELECT * FROM pronunciation WHERE ipa = :ipa")
    List<Pronunciation> get(String ipa);

    @Query("SELECT * FROM pronunciation WHERE spelling = :spelling")
    List<Pronunciation> reverseLookup(String spelling);


    @Query("SELECT * FROM pronunciation")
    List<Pronunciation> getAll();

    @Query("SELECT * FROM pronunciation WHERE spelling = :spelling and ipa like :ipa")
    Pronunciation getBySpelling(String ipa, String spelling);

    @Query("SELECT * FROM pronunciation WHERE ipa LIKE :ipa")
    List<Pronunciation> getLikeIpa(String ipa);

    @Query("Update pronunciation set frequency = frequency + 1 where spelling = :spelling")
    void incrementFrequency(String spelling);

    @Insert
    void insert(Pronunciation pronunciation);

    @Insert
    void insertAll(List<Pronunciation> pronunciations);

    @Query("SELECT count(distinct ipa) from pronunciation")
    int numEntries();

    @Update
    void update(Pronunciation pronunciation);
}