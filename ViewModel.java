package com.hfad.bibleapp_5a;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.Toast;

import com.hfad.bibleapp_5a.DatabaseHelpers.InterlinearDatabaseHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ViewModel {

    private ArrayList<ArrayList<ArrayList<String>>> verses;
    private String Maintext;
    ArrayList<String> selectedVerses = new ArrayList<>();
    private InterlinearDatabaseHelper db;
    private static final String TAG = "mikesLog";
    private int bookIndex, chapterIndex;
    Activity _activity;
    int PaleoFontIndex;

    public ViewModel(Activity act, int bookNumber, int chapterNumber) throws IOException {
        _activity=act;
        bookIndex = bookNumber;
        chapterIndex = chapterNumber;

        ApplySettings();

        db = new InterlinearDatabaseHelper(_activity, "books");
       // db.InitializeDb();/*  */

        ReadTextFile(act);
        beginExtraction();
    }

    SharedPreferences sharedPref;
    private void ApplySettings() {
        sharedPref = _activity.getPreferences(Context.MODE_PRIVATE);
        PaleoFontIndex  = Integer.valueOf(sharedPref.getString("paleoFontIndex", "1"));
    }


    private void ReadTextFile(Activity act) throws IOException {
        String string = "";

        StringBuilder stringBuilder = new StringBuilder();
        InputStream is = act.getResources().openRawResource(R.raw.kjv);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        while (true) {
            try {
                if ((string = reader.readLine()) == null) break;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            stringBuilder.append(string).append("\n");
        }
        is.close();
        //  Log.i(TAG, stringBuilder.toString().substring(0,202));
        Maintext = stringBuilder.toString();
    }

    private void beginExtraction()
    {
        ArrayList<String> bookTexts;
        ArrayList<String> chapterTexts = new ArrayList<String>();
        verses = new ArrayList<ArrayList<ArrayList<String>>>();
        String bn;

        bookTexts = extractBooks(Maintext);
        //Log.i(TAG, "length: " + String.valueOf(bookTexts.size()));

        for (int i = 0; i <= bookTexts.size() - 1; i++)
        {
            verses.add(new ArrayList<ArrayList<String>>());
            bn = ("0" + (i + 1)).substring(("0" + (i + 1))
                    .length() - 2);
            //pattern=Pattern.compile("Book " + bn + " " + getBooksListBasedOnVersionLanguage()[i] + "(\r\n)+");
            bn = replaceGroup("Book " + bn + " " + getBooksListBasedOnVersionLanguage()[i] + "(\r\n)+",
                    bookTexts.get(i), 1, "");
            bn = replaceGroup("(\r\n)+",bn,  1,"\n");
            chapterTexts = extractChapters(bn);
            for (int j = 0; j <= chapterTexts.size() - 1; j++){
                verses.get(i).add(extractVerses(chapterTexts.get(j), j + 1));
            }
        }
    }
    public static String replaceGroup(String regex, String source, int groupToReplace, String replacement) {
        return replaceGroup(regex, source, groupToReplace, 1, replacement);
    }
    public static String replaceGroup(String regex, String source, int groupToReplace, int groupOccurrence, String replacement) {
        Matcher m = Pattern.compile(regex).matcher(source);
        for (int i = 0; i < groupOccurrence; i++)
            if (!m.find()) return source; // pattern not met, may also throw an exception here
        return new StringBuilder(source).replace(m.start(groupToReplace), m.end(groupToReplace), replacement).toString();
    }
    private ArrayList<String> extractVerses(String chaptertext, int chapterNum)
    {
        ArrayList<String> verses = new ArrayList<>();
        Matcher matches;
        int i=0, start = 0;
        String chNum, vNum;
        String newtext;

        try {
            Pattern regex = Pattern.compile("\\d{3}:\\d{3}");
            matches = regex.matcher(chaptertext);
            matches.find();
            while (matches.find()) {
                chNum = ("00" + chapterNum).substring(("00" + chapterNum).length() - 3);
                vNum = ("00" + (i+1)).substring(("00" + (i+1)).length() - 3);
                newtext = chaptertext.substring(start, matches.start(0)).replace(chNum + ":" + vNum, "");
                verses.add(String.valueOf(i+1) + ". " + newtext.replace(chNum + ":" + vNum, "").replace("\n", ""));
                start = matches.start(0);
                i++;
            }
        } catch (PatternSyntaxException ex) {
            // Syntax error in the regular expression
        }

        return verses;
    }
    private ArrayList<String> extractChapters(String chaptertext)
    {
        ArrayList<String> chapters = new ArrayList<String>();
        Matcher matches;
        int start = 0;

        String newtext="";
        try {
            Pattern regex = Pattern.compile("\\d{3}:001");
            matches = regex.matcher(chaptertext);
            matches.find();
            while (matches.find()) {
                newtext = chaptertext.substring(start, matches.start(0));
                chapters.add(newtext);
                start = matches.start(0);
            }
            newtext = chaptertext.substring(start);
            chapters.add(newtext);

        } catch (PatternSyntaxException ex) {
            // Syntax error in the regular expression
        }
        return chapters;
    }
    private ArrayList<String> extractBooks(String maintext)
    {
        ArrayList<String> books = new ArrayList<String>();
        Matcher matches, bmatches;
        Pattern regex, bregex;
        int start = 0;

        String newtext="";
        try {
            regex = Pattern.compile("Book \\d+ ");
            matches = regex.matcher(maintext);
            matches.find();
            while (matches.find()) {
                newtext = maintext.substring(start , matches.start(0));
                newtext = replaceGroup("(Book \\d+ (?:\\d+ )?(?:\\w+ ?)+(?:\\n|\\r\\n|\\s)+?)",newtext, 1,"");
                books.add(newtext);
                start = matches.start(0);
            }
            newtext = maintext.substring(start ,maintext.length());
            books.add(newtext);
        } catch (PatternSyntaxException ex) {
            // Syntax error in the regular expression
        }
        return books;
    }
    public String[] getBooksListBasedOnVersionLanguage()
    {
        String[] booksList = new String[]  {
                "Genesis", "Exodus", "Leviticus", "Numbers", "Deuteronomy", "Joshua", "Judges", "Ruth", "1 Samuel", "2 Samuel", "1 Kings", "2 Kings", "1 Chronicles", "2 Chronicles", "Ezra", "Nehemiah", "Esther", "Job", "Psalms", "Proverbs", "Ecclesiastes", "Song of Songs", "Isaiah", "Jeremiah", "Lamentations", "Ezekiel", "Daniel", "Hosea", "Joel", "Amos", "Obadiah", "Jonah", "Micah", "Nahum", "Habakkuk", "Zephaniah", "Haggai", "Zechariah", "Malachi",
                "Matthew", "Mark", "Luke", "John", "Acts", "Romans", "1 Corinthians", "2 Corinthians", "Galatians", "Ephesians", "Philippians", "Colossians", "1 Thessalonians", "2 Thessalonians", "1 Timothy", "2 Timothy", "Titus", "Philemon", "Hebrews", "James", "1 Peter", "2 Peter", "1 John", "2 John", "3 John", "Jude", "Revelation" };

        String[] ybooksList = new String[] { "Gẹnẹsisi", "Eksodu", "Lefitiku", "Numeri", "Deuteronomi", "Joṣua", "Onidajọ", "Rutu", "1 Samuẹli",
                "2 Samuẹli", "1 Ọba", "2 Ọba", "1 Kronika", " 2 Kronika", "Esra", "Nehemiah", "Esteri", "Jobu", "Saamu", "Òwe", "Oniwaasu",
                "Orin Solomoni", "Isaiah", "Jeremiah", "Ẹkun Jeremiah", "Esekiẹli", "Daniẹli", "Hosea", "Joẹli", "Amosi", "Ọbadiah", "Jona",
                "Mika", "Nahumu", "Habakuku", "Sefaniah", "Hagai", "Sekariah", "Malaki", "Matiu", "Marku", "Luku", "Johanu", "Ìṣe àwọn Aposteli",
                "Romu", "1 Kọrinti", "2 Kọrinti", "Galatia", "Efesu", "Filipi", "Kolose", "1 Tẹsalonika", "2 Tẹsalonika", "1 Timotiu", "2 Timotiu",
                "Titu", "Filemoni", "Heberu", "Jakọbu", "1 Peteru", "2 Peteru", "1 Johanu", "2 Johanu",
                "3 Johanu", "Juda", "Ìfihàn" };


        Hashtable<String, String[]> booksLists = new Hashtable<String, String[]>();
        String[] versionList = new String[] { "KJV", "NKJV", "ERV", "NASB", "ASV", "ESV", "NLT", "NIV" };
        for(int i = 0; i < versionList.length; i++)
        {
            booksLists.put(versionList[i], booksList);
        }
        booksLists.put("bmt", ybooksList);

        String textVersion = "KJV"; //remove this later, place in settings model
        return (booksLists.get(textVersion));
    }


    public void SaveText(MainActivity act, int bookNum, int chapterNum) {
        SharedPreferences sharedPref = act.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("bookNum", Integer.toString(bookNum));
        editor.putString("chapterNum", Integer.toString(chapterNum));
        editor.apply();
    }

    public ArrayList<String> getVerses(int chapterNum) {
return  verses.get(bookIndex).get(chapterNum);
    }

    public int getSelectedBookIndex(String item){
        List<String> numList
                = new ArrayList<String>(
                Arrays.asList(getBooksListBasedOnVersionLanguage()));
        return numList.indexOf(item);
    }
    public int getTotalChapters(int position) {
        return verses.get(position).size();
    }

    public ArrayList<String> SetAndGetTextVerses(int bookIndex, int chapter) {
        this.bookIndex  = bookIndex;
        this.chapterIndex = chapter;
        return verses.get(bookIndex).get(chapter);
    }

    public int getBookNum(){
        return bookIndex;
    }

    public int getChapterNum(){
        return chapterIndex;
    }
    public ArrayList<ArrayList<String>> GetChapterVerses() {
        if (verses.get(bookIndex).size()<chapterIndex){
            Toast.makeText(_activity, "chapter unavailable!", Toast.LENGTH_SHORT).show();
        chapterIndex=0;}
        return verses.get(bookIndex);
    }

    public String getBookName(int bookNum) {
        return getBooksListBasedOnVersionLanguage()[bookNum];
    }
    public String getBookName() {
        return getBooksListBasedOnVersionLanguage()[bookIndex];
    }

    /*public void ReloadInterlinearData() {
        db.LoadFromSource();
    }*/

    public ArrayList<ArrayList<HashMap<String, String>>> getInterlinearVersesList(int chapterNum) {
        return db.GetChapters(getBookName(bookIndex), bookIndex, chapterNum);
    }

    public void SetBookNameAndChapterNum(int bookNum, int i) {
        this.bookIndex=bookNum;
        this.chapterIndex = i;
    }


    public int getFont() {
        switch(PaleoFontIndex){
            case 1:
                return R.font.paleo_hebrew1;
            case 2:
                return R.font.paleo_hebrew2;
            case 3:
                return R.font.paleo_hebrew3;
            default:
                return R.font.titillium;
        }
    }

public void AddNote(String title, String description, String verses, String content, String date ) {
    db.insertNote(title, description, verses, content, date);
}

    public void setAndSavePaleoFontIndex(int i) {
        PaleoFontIndex = i;
        SharedPreferences sharedPref = _activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("paleoFontIndex", Integer.toString(i));
        editor.apply();
    }


    public Cursor getNotesCursor() {
        Cursor c  = db.GetNotes();
        return c;
    }
}
