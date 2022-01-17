package com.hfad.bibleapp_5a;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hfad.bibleapp_5a.DatabaseHelpers.InterlinearDatabaseHelper;

import org.w3c.dom.Text;

public class NotesActivity extends AppCompatActivity {
    private static ViewModel vmd;
    private static String verses;
    Toolbar toolbar;
    View v;
    public static void SetVm(ViewModel vm, String text) {
        vmd = vm;
        verses = text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        toolbar = (Toolbar) findViewById(R.id.notestoolbar);
        toolbar.setTitle("Notes");
        setSupportActionBar(toolbar);/**/

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (vmd.selectedVerses!=null && vmd.selectedVerses.size()>0){
            //String v = vmd.selectedVerses.get(0);
            TextView t = (TextView)findViewById(R.id.tVerses);
            t.setText(verses);
        }

        setupFavoritesListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes, menu);
        return super.onCreateOptionsMenu(menu);
    }



    boolean isEditing = true;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                // Navigate to settings screen
                isEditing = !isEditing;
                MenuItem saveItem = toolbar.getMenu().findItem(R.id.action_done);
                saveItem.setVisible(isEditing);
                getValues();
                vmd.AddNote(t, d, vs, c, dt);
                ((LinearLayout)findViewById(R.id.Viewer)).setVisibility(View.GONE);
                return true;
            case R.id.action_close:
                // Save profile changes
                return true;
            default:
                return false;
        }
    }

    String t, d, vs, c, dt;
    private void getValues() {
        t = ((TextView)findViewById(R.id.tTitle)).getText().toString();
        d = ((TextView)findViewById(R.id.tDescription)).getText().toString();
        vs = ((TextView)findViewById(R.id.tVerses)).getText().toString();
        c = ((TextView)findViewById(R.id.tContent)).getText().toString();
        dt = ((TextView)findViewById(R.id.tDate)).getText().toString();
    }

    private void setupFavoritesListView() {
        //Populate the list_favorites ListView from a cursor
        ListView listFavorites = (ListView) findViewById(R.id.list_notes);
        SQLiteDatabase db;
        Cursor favoritesCursor;
        try{
         SQLiteOpenHelper notesDatabaseHelper =
                 new InterlinearDatabaseHelper(this, "notes");
         db = notesDatabaseHelper.getReadableDatabase();
         favoritesCursor = db.query("NOTES",
                    new String[] { "_id", "TITLE"},
                    null,
                    null, null, null, null);

            SimpleCursorAdapter favoriteAdapter =
                    new SimpleCursorAdapter(NotesActivity.this,
                            android.R.layout.simple_list_item_1,
                            favoritesCursor,
                            new String[]{"TITLE"},
                            new int[]{android.R.id.text1}, 0);
            listFavorites.setAdapter(favoriteAdapter);
        } catch(SQLiteException e) {
            Toast toast = Toast.makeText(this,
                    "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        //Navigate to DrinkActivity if a drink is clicked
        listFavorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View v, int position, long id) {
               // Intent intent = new Intent(TopLevelActivity.this, DrinkActivity.class);
              //  intent.putExtra(DrinkActivity.EXTRA_DRINKID, (int)id);
               // startActivity(intent);
            }
        });
    }
}