package com.hfad.bibleapp_5a;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;


public class TodayActivity extends AppCompatActivity{
    private static MainActivity mainActivity;
    public static void SetActivity(MainActivity _mainActivity) {
        mainActivity= _mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("BibleApp: Today");
        setSupportActionBar(toolbar);/**/
    }

    public void onClickShowLAst(View view) {

        int chapterNum = mainActivity.vm.getChapterNum();
        int bookNum = mainActivity.vm.getBookNum();

        mainActivity.vm.SetAndGetTextVerses(bookNum, chapterNum);

        //ReadingActivity.setTitle(Name + " " + (chapterNum+1));
        ReadingActivity.setData(mainActivity, mainActivity.vm,//.GetChapterVerses()
                    //.GetChapters(Name, String.valueOf(bookNum+1))
                bookNum>=39);
        Intent readerIntent = new Intent(this, ReadingActivity.class);
        readerIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(readerIntent, 0);
    }

    public void onClickShowBooks(View view) {
        BooksActivity.SetVm(mainActivity);
        Intent BooksIntent = new Intent(this, BooksActivity.class);
        startActivity(BooksIntent);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the app bar.
          getMenuInflater().inflate(R.menu.menu_main, menu);
         return super.onCreateOptionsMenu(menu);
    }*/
    public void onClickShowAnnotations(View view) {
        // BooksActivity.SetVm(mainActivity);
        Intent notes = new Intent(this, NotesActivity.class);
        NotesActivity.SetVm(mainActivity.vm, "");
        notes.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(notes, 0);
        //findViewById(R.id.selectedVersesContainer).setVisibility(View.GONE);
    }

}