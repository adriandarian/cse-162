package com.example.voiceinterface;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    ArrayList<String> mylist;
    ArrayList<LatLng> response;
    boolean command_found;
    LatLng res;
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        // Enables Always-on
        setAmbientEnabled();

        mylist = new ArrayList<>();
        response = new ArrayList<>();
        mylist.add("Toronto");
        LatLng TORONTO = new LatLng(43.6532, -79.3832);
        response.add(TORONTO);
        mylist.add("Sydney");
        LatLng SYDNEY = new LatLng(-33.85704, 151.21522);
        response.add(SYDNEY);

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });
    }

    public void start(View view) {
        displaySpeechRecognizer();
    }

    private static final int SPEECH_REQUEST_CODE = 0;

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // This starts the activity and populates the intent with the speech text.
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText.
            Log.d("TAG", spokenText);
            command_found = false;

            for (String curVal : mylist) {
                if (curVal.contains(spokenText)) {
                    command_found = true;
                    Log.d("TAG", "found");
                    break;
                }
            }

            if (command_found) {
                Log.d("TAG", "enter");
                int idx = mylist.indexOf(spokenText);
                res = response.get(idx);
                mTextView.setText(spokenText);
                t1.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null);

                Intent intent = new Intent(this, MapActivity.class);
                Bundle extras = new Bundle();

                extras.putDouble("long", res.longitude);
                extras.putDouble("lat", res.latitude);
                intent.putExtras(extras);
                startActivity(intent);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
