package com.bignerdranch.android.travelbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

public class TranslateActivity extends AppCompatActivity {
    private Spinner fromSpinner, toSpinner;
    private int fromSpinnerIndex;
    private int toSpinnerIndex;
    private TextInputEditText sourceText;
    private ImageView micIV;
    private MaterialButton translateBtn;
    private TextView translateTV;
    String[] fromLanguage = {"From", "English", "Spanish", "French", "German", "Japanese", "Afrikaans", "Arabic", "Belarusian", "Bulgarian", "Bengali", "Catalan", "Czech", "Welsh", "Hindi", "Urdu"};
    String[] toLanguage = {"To", "English", "Spanish", "French", "German", "Japanese", "Afrikaans", "Arabic", "Belarusian", "Bulgarian", "Bengali", "Catalan", "Czech", "Welsh", "Hindi", "Urdu"};
    private static final int REQUEST_PERMISSION_CODE = 1;
    int languageCode, fromLanguageCode, toLanguageCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        fromSpinner = findViewById(R.id.idFromSpinner);
        toSpinner = findViewById(R.id.idToSpinner);
        sourceText = findViewById(R.id.idEditSource);
        micIV = findViewById(R.id.idIVMic);
        translateBtn = findViewById(R.id.idBtnTranslation);
        translateTV = findViewById(R.id.idTranslatedTV);

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fromSpinnerIndex = i;
                fromLanguageCode = getLanguageCode(fromLanguage[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter fromAdapter = new ArrayAdapter(this, R.layout.spinner_item, fromLanguage);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toSpinnerIndex = i;
                toLanguageCode = getLanguageCode(toLanguage[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter toAdapter = new ArrayAdapter(this, R.layout.spinner_item, toLanguage);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);
        micIV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something to translate");
                try{
                    startActivityForResult(intent,REQUEST_PERMISSION_CODE);
                }catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(TranslateActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        });
        translateBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                translateTV.setVisibility(View.VISIBLE);
                translateTV.setText("");
                if (sourceText.getText().toString().isEmpty()) {
                    Toast.makeText(TranslateActivity.this, "Please enter to translate", Toast.LENGTH_SHORT).show();
                } else if (fromLanguageCode == 0) {
                    Toast.makeText(TranslateActivity.this, "Please select source Language", Toast.LENGTH_SHORT).show();
                } else if(toLanguageCode==0){
                    Toast.makeText(TranslateActivity.this, "Please select the language to make translation", Toast.LENGTH_SHORT).show();
                }else{
                    translateText(fromLanguageCode,toLanguageCode,sourceText.getText().toString());
                }
            }



        });
    }

    public void onSwitchLanguageButtonClick(View view) {
        if(fromLanguageCode != 0 && toLanguageCode != 0) {
            int temp = fromSpinnerIndex;
            fromSpinnerIndex = toSpinnerIndex;
            toSpinnerIndex = temp;

            fromSpinner.setSelection(fromSpinnerIndex);
            toSpinner.setSelection(toSpinnerIndex);

            fromLanguageCode = getLanguageCode(fromLanguage[fromSpinnerIndex]);
            toLanguageCode = getLanguageCode(toLanguage[toSpinnerIndex]);
        }
        else {
            Toast.makeText(TranslateActivity.this, "Please select two languages to switch", Toast.LENGTH_SHORT).show();
        }
    }

    private void translateText(int fromLanguageCode, int toLanguageCode, String source) {
        translateTV.setText("Downloading model, please wait...");
        FirebaseTranslatorOptions options= new FirebaseTranslatorOptions.Builder().setSourceLanguage(fromLanguageCode).setTargetLanguage(toLanguageCode).build();
        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translateTV.setText("Translation..");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        translateTV.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TranslateActivity.this,"Faied to translate!! try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TranslateActivity.this,"Faied to download model!! Check your internew connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==REQUEST_PERMISSION_CODE){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            sourceText.setText(result.get(0));
        }
    }

    //    String[] fromLanguage = {"From","English","Afrikaans","Arabic","Belarusian","Bulgarian","Bengali","Catalan"
    //    ,"Czech","Welsh","Hindi","Urdu"};
    private int getLanguageCode(String language) {
        int languageCode = 0;
        switch (language) {
            case"English":
                languageCode= FirebaseTranslateLanguage.EN;
                break;
            case"Spanish":
                languageCode= FirebaseTranslateLanguage.ES;
                break;
            case"French":
                languageCode= FirebaseTranslateLanguage.FR;
                break;
            case"German":
                languageCode= FirebaseTranslateLanguage.DE;
                break;
            case"Japanese":
                languageCode= FirebaseTranslateLanguage.JA;
                break;
            case"Afrikaans":
                languageCode= FirebaseTranslateLanguage.AF;
                break;
            case"Arabic":
                languageCode= FirebaseTranslateLanguage.AR;
                break;
            case"Belarusian":
                languageCode= FirebaseTranslateLanguage.BE;
                break;
            case"Bulgarian":
                languageCode= FirebaseTranslateLanguage.BG;
                break;
            case"Bengali":
                languageCode= FirebaseTranslateLanguage.BN;
                break;
            case"Catalan":
                languageCode= FirebaseTranslateLanguage.CA;
                break;
            case"Czech":
                languageCode= FirebaseTranslateLanguage.CS;
                break;
            case"Welsh":
                languageCode= FirebaseTranslateLanguage.CY;
                break;
            case"Hindi":
                languageCode= FirebaseTranslateLanguage.HI;
                break;
            case"Urdu":
                languageCode= FirebaseTranslateLanguage.UR;
                break;
            default:
                languageCode=0;
        }
        return languageCode;
    }
}
