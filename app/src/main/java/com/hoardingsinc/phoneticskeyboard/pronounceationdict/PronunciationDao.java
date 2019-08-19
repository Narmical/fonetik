package com.hoardingsinc.phoneticskeyboard.pronounceationdict;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface PronunciationDao {

    @Query("SELECT * FROM pronunciation")
    List<Pronunciation> getAll();

    @Query("SELECT * FROM pronunciation WHERE ipa LIKE :ipa LIMIT 1")
    Pronunciation findByIpa(String ipa);

    @Insert
    void insertAll(List<Pronunciation> products);

    @Update
    void update(Pronunciation product);

    @Delete
    void delete(Pronunciation product);
}