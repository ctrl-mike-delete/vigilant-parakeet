package com.hfad.bibleapp_5a;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ViewModel vm;
    private Cursor cursor;

    SharedPreferences sharedPref;
    String Name, id;
    Typeface typeface;
    Fragment fragment = null;
    Intent readerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        Initialize();
        int bookNum=0, chapterNum =0;

        try{
            sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            bookNum  = Integer.valueOf(sharedPref.getString("bookNum", "0"));
            chapterNum = Integer.valueOf(sharedPref.getString("chapterNum", "0"));
        }
        catch (Exception ex){
            bookNum=0;
            chapterNum  = 0;
        }

        try
        {
            vm = new ViewModel(this, bookNum, chapterNum);
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception ex){
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void Initialize() {
        TodayActivity.SetActivity(this);
        Intent today = new Intent(this, TodayActivity.class);
        startActivity(today);
    }

}
