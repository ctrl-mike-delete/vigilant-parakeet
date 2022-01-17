package com.hfad.bibleapp_5a;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;



public class BooksActivity extends AppCompatActivity {
    String Name;
    String[] books;
    private static MainActivity mainActivity;
    Integer selectedIndex;
    ArrayAdapter<String> chapter_adapter;
    Intent readerIntent;
    GridView chapterNumbers_GridView;
    ArrayList<String> chapterNums;
   // NumbersAdapter chapter_adapter;
    int i = 1, bookNum;

    GridView  books_gridview, chapters_gridview;
    ArrayAdapter booksAdapter, chaptersAdapter;

    public static void SetVm(MainActivity a){
        mainActivity = a;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Books");
        setSupportActionBar(toolbar);/**/

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        books= mainActivity.vm.getBooksListBasedOnVersionLanguage();
        chapterNums =new ArrayList<String>();
        chapterNumbers_GridView = findViewById(R.id.chapterNumbers_GridView);

        books_gridview =  findViewById(R.id.books_reader_grid);
        booksAdapter = new ArrayAdapter<String>(this,
                R.layout.books_gridview_card, books);

        books_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Object item = booksAdapter.getItem(position);
                bookNum=position;
                ((ScrollView)findViewById(R.id.chapter_reader_grid_scroller)).setVisibility(View.VISIBLE);
                if (item != null) {
                    int count = mainActivity.vm.getTotalChapters(position);
                    selectedIndex = position;
                    chapterNums.clear();
                    for (int i = 1; i <= count; i++) {
                        chapterNums.add(String.valueOf(i));
                    }
                    chapter_adapter.notifyDataSetChanged();
                }
            }
        });
        books_gridview.setAdapter(booksAdapter);
        chapter_adapter = new ArrayAdapter(this, R.layout.books_chapters_card, chapterNums);
        chapterNumbers_GridView.setAdapter(chapter_adapter);
        books_gridview.setSelection(mainActivity.vm.getBookNum());
        chapterNumbers_GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((ScrollView)findViewById(R.id.chapter_reader_grid_scroller)).setVisibility(View.VISIBLE);
            }
        });

        ((Button)findViewById(R.id.btnLetters)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent l = new Intent(BooksActivity.this, LettersActivity.class);
                startActivity(l);
            }
        });

    }

    public void onChapterClick(View view) {
        try {
            SavePrefs(bookNum, Integer.valueOf((String)((TextView)view).getText())-1);
    
            mainActivity.vm.SetAndGetTextVerses(bookNum, Integer.valueOf((String)((TextView)view).getText())-1);
        Name = mainActivity.vm.getBookName(bookNum);
            ReadingActivity.setData(mainActivity, mainActivity.vm,//.GetChapterVerses()
                    //.GetChapters(Name, String.valueOf((bookNum+1)))
                    bookNum>=39);

            readerIntent = new Intent(this, ReadingActivity.class);
            readerIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(readerIntent, 0);
        }
        catch (Exception ex){
            Toast.makeText(getBaseContext(),"Load Text exception", Toast.LENGTH_SHORT).show();
            startActivityIfNeeded(readerIntent, 0);
        }


    }

    private void SavePrefs(int bookNum, int chapterNum) {
        SharedPreferences sharedPref = mainActivity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("bookNum", Integer.toString(bookNum));
        editor.putString("chapterNum", Integer.toString(chapterNum));
        editor.apply();
    }

}
