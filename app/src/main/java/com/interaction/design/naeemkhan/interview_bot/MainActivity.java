package com.interaction.design.naeemkhan.interview_bot;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextToSpeech TTS;
    EditText textbox;
    Button gobutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the components of the app and assign them here for use
        textbox = findViewById(R.id.text_box);
        gobutton = findViewById(R.id.go_button);

        // Initialise Text-To-Speech
        TTS = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // If successfully initialized, set the language to English
                if(status != TextToSpeech.ERROR) {
                    TTS.setLanguage(Locale.ENGLISH);
                }
            }
        });

        // ActionListener for the gobutton
        gobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textToSpeek = textbox.getText().toString();
                /* Have the TTS play whatever is written in the text. Queue_flush is being used
                *  to play the next text and cancel the ongoing speech if button is pressed
                */
                TTS.speak(textToSpeek, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

    }

    // Force shutdown the TTS when app is closed
    @Override
    protected void onDestroy() {
        if(TTS != null) {
            TTS.stop();
            TTS.shutdown();
        }
        super.onDestroy();
    }
}
