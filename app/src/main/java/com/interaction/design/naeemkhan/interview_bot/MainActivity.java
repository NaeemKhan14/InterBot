package com.interaction.design.naeemkhan.interview_bot;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextToSpeech TTS;
    private TextView textview;
    private Button gobutton;
    private Spinner spinnerList;
    private ArrayAdapter<String> adapter;
    private final ArrayList<String> results = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the components of the app and assign them here for use
        textview = findViewById(R.id.text_view);
        gobutton = findViewById(R.id.go_button);
        // First value for TTS to play
        results.add("Select a question from the list below");

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
                getWebsiteData();
            }
        });

    }

    private void getWebsiteData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Connect to the link and get the results from specified tag
                    String username = "";
                    String password = "";
                    String login = username + ":" + password;
                    String base64login = new String(android.util.Base64.encode(login.getBytes(), android.util.Base64.DEFAULT));
                    Document dataSource = Jsoup.connect("http://shootboys.net/interbot/showData.php").header("Authorization", "Basic " + base64login).get();
                    // Get all the data inside <body> tag.
                    Elements data = dataSource.select("body");
                    // Split the results at ### so we know where a new line is
                    String[] dataList = data.text().split("###");

                    // Append each question into the ArrayList
                    for(String value : dataList) {
                        results.add(value);
                    }

                } catch (IOException e) {
                    e.getMessage();
                }

                // Once the results' list is populated, push the result into spinner
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Initialize the spinner and enable it
                        spinnerList = findViewById(R.id.spinner);
                        spinnerList.setEnabled(true);
                        spinnerList.setSelection(0, false);
                        // ArrayAdapter will handle the values that goes into spinner
                        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, results);
                        spinnerList.setAdapter(adapter);

                        /*
                         * Whenever an item from spinner is selected, this listener will record that
                         * selection and display it in the spinner, as well as playing it
                         */
                        spinnerList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                textview.setText(results.get(position));
                                TTS.speak(results.get(position), TextToSpeech.QUEUE_FLUSH, null);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });
            }
        }).start();
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
