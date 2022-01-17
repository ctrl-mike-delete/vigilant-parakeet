package com.hfad.bibleapp_5a;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private static ViewModel vmd;
    public static void SetVm (ViewModel vm){
       vmd=vm;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

       InitializePaleoSpinner();
    }

    private void InitializePaleoSpinner() {
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("modern hebrew");
        categories.add("paleo hebrew one");
        categories.add("paleo hebrew two");
        categories.add("paleo hebrew three");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ((Spinner)findViewById(R.id.paleoFontsSpinner)).setAdapter(dataAdapter);
        ((Spinner)findViewById(R.id.paleoFontsSpinner)).setSelection(vmd.PaleoFontIndex);
        ((Spinner)findViewById(R.id.paleoFontsSpinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                vmd.setAndSavePaleoFontIndex(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}