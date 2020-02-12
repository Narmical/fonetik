package com.narmical.fonetic;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.narmical.fonetic.pronounceationdictionary.PronunciationDB;
import com.narmical.fonetic.pronounceationdictionary.PronunciationDao;
import com.narmical.fonetic.pronounceationdictionary.RoomPronunciationDictionary;
import com.narmical.fonetic.rawdictionary.ArpabetToIpaConverter;
import com.narmical.fonetic.rawdictionary.CmuPronouncingDictionary;
import com.narmical.fonetic.rawdictionary.EmoticonDictionary;
import com.narmical.fonetic.rawdictionary.MobyPronunciator;
import com.narmical.fonetic.rawdictionary.MobyToIpaConverter;
import com.narmical.fonetic.rawdictionary.RawDictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Thread dictionaryBuilderThread;
    public ProgressBar progressBar;
    public TextView progressBarText;
    private boolean rebuild = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_open_settings:
                startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
                break;
            case R.id.button_open_keyboard_selection:
                InputMethodManager inputManager = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                inputManager.showInputMethodPicker();
                break;
            case R.id.build_room_dict:


                this.rebuild = true;
                dictionaryBuilderThread = new Thread(this::buildDictionary);
                dictionaryBuilderThread.start();

                break;
        }

    }

    private int preCountDictSize(int resource) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        this.getResources().openRawResource(resource),
                        StandardCharsets.UTF_8
                )
        );
        int lines = 0;
        while (reader.readLine() != null) lines++;
        reader.close();
        return lines;
    }


    private void buildDictionary() {
        PronunciationDB db = Room.databaseBuilder(this.getApplicationContext(), PronunciationDB.class,
                "pronunciation.db").build();
        PronunciationDao pronunciationDao = db.pronunciationDao();
        if (pronunciationDao.numEntries() == 0 || this.rebuild == true) {
            db.clearAllTables();
            runOnUiThread(() -> this.progressBar.setVisibility(View.VISIBLE));
            RoomPronunciationDictionary dictionary = new RoomPronunciationDictionary(this);
            try {

                int dictSize = this.preCountDictSize(R.raw.mpron_dict) +
                        this.preCountDictSize(R.raw.cmu_dict) +
                        this.preCountDictSize(R.raw.emoticon_to_emoji_dict);


                List<RawDictionary> rawDictionaries = new ArrayList<>();
                rawDictionaries.add(
                        new MobyPronunciator(
                                new BufferedReader(
                                        new InputStreamReader(
                                                this.getResources().openRawResource(R.raw.mpron_dict
                                                ),
                                                StandardCharsets.UTF_8
                                        )
                                ),
                                new MobyToIpaConverter(
                                        new BufferedReader(
                                                new InputStreamReader(
                                                        this.getResources().openRawResource(R.raw.mpront_to_ipa),
                                                        StandardCharsets.UTF_8
                                                )

                                        )
                                )
                        )
                );
                rawDictionaries.add(
                        new CmuPronouncingDictionary(
                                new BufferedReader(
                                        new InputStreamReader(
                                                this.getResources().openRawResource(R.raw.cmu_dict),
                                                StandardCharsets.UTF_8
                                        )
                                ),
                                new ArpabetToIpaConverter(
                                        new BufferedReader(
                                                new InputStreamReader(
                                                        this.getResources().openRawResource(R.raw.arpabet_to_ipa),
                                                        StandardCharsets.UTF_8
                                                )

                                        )
                                )
                        )
                );
                rawDictionaries.add(
                        new EmoticonDictionary(
                                new BufferedReader(
                                        new InputStreamReader(
                                                this.getResources().openRawResource(R.raw.emoticon_to_emoji_dict),
                                                StandardCharsets.UTF_8
                                        )
                                )
                        )
                );
                dictionary.loadDictionary(rawDictionaries, this, dictSize);
            } catch (
                    UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (
                    IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        this.progressBar = findViewById(R.id.progress_bar);
        this.progressBar.setMax(100);
        progressBar.setVisibility(View.INVISIBLE);
        this.progressBarText = findViewById(R.id.progress_bar_text);


        TextView presentation = findViewById(R.id.presentation);
        presentation.setText(Html.fromHtml(getString(R.string.presentation)));
        presentation.setMovementMethod(LinkMovementMethod.getInstance());

        Button b = findViewById(R.id.button_open_settings);
        b.setOnClickListener(this);
        b = findViewById(R.id.button_open_keyboard_selection);
        b.setOnClickListener(this);
        b = findViewById(R.id.build_room_dict);
        b.setOnClickListener(this);
        this.rebuild = false;
        dictionaryBuilderThread = new Thread(this::buildDictionary);
        dictionaryBuilderThread.start();
    }
}
