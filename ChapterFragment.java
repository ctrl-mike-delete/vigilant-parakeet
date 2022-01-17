package com.hfad.bibleapp_5a;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;

public class ChapterFragment extends  Fragment {

    private ArrayList<String> Verses;
    private ArrayList<ArrayList<HashMap<String, String>>> InterlinearChapters;
    ListView readingListview;
    SimpleAdapter InterlinearAdapter;
    ArrayAdapter verse_adapter;
    ScrollView language_reader_grid_scroller ;
    GridView language_gridview;
    ArrayList<HashMap<String, String>> InterlinearVersesList = new ArrayList<>();
    String text="";

    ViewModel vm;
    private static boolean IsNewTestament;
    private static int index = -1;
    private int chapterNum;

    public ChapterFragment() {

    }

    @SuppressLint("ValidFragment")
    ChapterFragment(Object[] data, int position ) {
        vm = (ViewModel)data[0];
        IsNewTestament = (boolean)data[1];
        chapterNum=position;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /** The Fragment's UI is a simple text view shoving its instance number and proguard-rules.pro
     an associated list. **/
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_chapter,
                container, false);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.fragment_toolbar);
        toolbar.setTitle(vm.getBookName() + " " + (chapterNum+1));

        Verses=vm.getVerses(chapterNum);
        InterlinearChapters = vm.getInterlinearVersesList(chapterNum);

        ((FloatingActionButton) v.findViewById(R.id.fabButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(language_reader_grid_scroller.getVisibility() == View.VISIBLE){
                    language_reader_grid_scroller.setVisibility(View.GONE);
                }
                else {
                    if(((FrameLayout)v.findViewById(R.id.selectedVersesContainer)).getVisibility()
                            == View.VISIBLE){
                        ((FrameLayout)v.findViewById(R.id.selectedVersesContainer)).setVisibility(View.GONE);
                    }
                    else
                    {
                        v.findViewById(R.id.selectedVersesContainer).setVisibility(View.VISIBLE);
                    }

                   /* ((Button)v.findViewById(R.id.closeSelectedTextsViewer)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            v.findViewById(R.id.selectedVersesContainer).setVisibility(View.GONE);
                        }
                    });*/
                    ((Button)v.findViewById(R.id.showPaleoViewer)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            language_reader_grid_scroller.setVisibility(View.VISIBLE);
                            v.findViewById(R.id.selectedVersesContainer).setVisibility(View.GONE);
                        }
                    });
                    ((Button)v.findViewById(R.id.openNotes)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent notes = new Intent(getContext(), NotesActivity.class);
                            NotesActivity.SetVm(vm, text);
                            startActivity(notes);
                            v.findViewById(R.id.selectedVersesContainer).setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

        readingListview = v.findViewById(R.id.listview_reading);
        readingListview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        Context c = getContext();
        int selectedListItem=5;
        verse_adapter = new ArrayAdapter<String>(v.getContext(), R.layout.verse_card, Verses){
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                final View renderer = super.getView(position, convertView, parent);
                if (position == selectedListItem)
                {
                    //TODO: set the proper selection color here:
                    // renderer.setBackgroundResource(android.R.color.darker_gray);
                }
                return renderer;
            }

        };


        readingListview.setAdapter(verse_adapter);
        if (InterlinearChapters!=null && InterlinearChapters.size()>0 && !IsNewTestament){
            //enable fab
            ((FloatingActionButton)v.findViewById(R.id.fabButton)).setEnabled(true);
            populateInterlinearChaptersList(0);
        }
        else{
            //disable fab
            ((FloatingActionButton)v.findViewById(R.id.fabButton)).setEnabled(false);
           /* if(!IsNewTestament) {
                vm.ReloadInterlinearData();
                InterlinearChapters = vm.getInterlinearVersesList(chapterNum);
            }*/
        }


        final int[] fontType = new int[3];
        language_gridview =  v.findViewById(R.id.language_reader_grid);
        InterlinearAdapter = new SimpleAdapter( getContext(), InterlinearVersesList,
                R.layout.interlinear_gridview_card,
                new String[] {"one", "two", "three"},
                new int[] {R.id.first, R.id.second, R.id.third}){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View renderer = super.getView(position, convertView, parent);

                //set font for interlinear
                TextView t = (TextView)renderer.findViewById(R.id.first);
                Typeface typeface = ResourcesCompat.getFont(getContext(), vm.getFont());
                t.setTypeface(typeface);
                return renderer;
            }
        };
        language_gridview.setAdapter(InterlinearAdapter);

        language_reader_grid_scroller = v.findViewById(R.id.language_reader_grid_scroller);



        readingListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String item = vm.getBookName() + " " + (vm.getChapterNum()+1) + ":" + (position+1);
                if (!vm.selectedVerses.contains(item) &&
                        language_reader_grid_scroller.getVisibility() != View.VISIBLE) {
                    vm.selectedVerses.add(item);
                    for (int i = 0; i < vm.selectedVerses.size(); i++) {
                        text += vm.selectedVerses.get(i) + " ";
                    }
                    text = text.substring(0, text.length() - 1);
                    ((TextView) v.findViewById(R.id.selectedVersesText)).setText(text);
                }
                populateInterlinearChaptersList(position);
                InterlinearAdapter.notifyDataSetChanged();
                language_gridview.smoothScrollToPosition(0);
            }
        });

       /* ((Button)v.findViewById(R.id.closeSelectedTextsViewer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        return v;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((Toolbar)v.findViewById(R.id.fragment_toolbar)).inflateMenu(R.menu.menu_main);

        ((Toolbar)v.findViewById(R.id.fragment_toolbar)).setOnMenuItemClickListener(item -> {
            Intent settings = new Intent(getContext(), SettingsActivity.class);
            SettingsActivity.SetVm(vm);
            startActivity(settings);
            return true;
        });
    }

    private void populateInterlinearChaptersList(int verseNum) {
        InterlinearVersesList.clear();
        if (InterlinearChapters.size()==0) {
           /* Toast.makeText(getContext(),
                    "Error:\n Empty resource for Interlinear View!", Toast.LENGTH_LONG).show();*/
           return;
        }
        int listSize=InterlinearChapters.get(verseNum).size();
        int n = 1;
        for (int i = 0; i < listSize/2; i++){
            HashMap<String, String> ht = new HashMap<>();
            ht.put("one", InterlinearChapters.get(verseNum).get(2*n-1).get("heb"));
            ht.put("two", InterlinearChapters.get(verseNum).get(2*n-1).get("trans"));
            ht.put("three", InterlinearChapters.get(verseNum).get(2*n-1).get("eng"));
            InterlinearVersesList.add(ht);

            ht = new HashMap<>();
            ht.put("one", InterlinearChapters.get(verseNum).get(2*n-2).get("heb"));
            ht.put("two", InterlinearChapters.get(verseNum).get(2*n-2).get("trans"));
            ht.put("three", InterlinearChapters.get(verseNum).get(2*n-2).get("eng"));
            InterlinearVersesList.add(ht);
            n++;
        }

        if(listSize%2==1){
            HashMap<String, String> ht = new HashMap<>();
            ht.put("one", InterlinearChapters.get(verseNum).get(listSize-1).get("heb"));
            ht.put("two", InterlinearChapters.get(verseNum).get(listSize-1).get("trans"));
            ht.put("three", InterlinearChapters.get(verseNum).get(listSize-1).get("eng"));
            InterlinearVersesList.add(ht);
        }
    }
}