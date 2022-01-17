package com.hfad.bibleapp_5a;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.hfad.bibleapp_5a.DatabaseHelpers.InterlinearDatabaseHelper;


public class ReadingActivity extends FragmentActivity {
    static ViewModel vm;
    static InterlinearDatabaseHelper db;
    private static boolean IsNewTestament;
    private static int currentChapterNum;
    MyAdapter mAdapter;
    ViewPager mPager;
    static MainActivity act;
    static  String bName;

    public static void setData(MainActivity a, ViewModel vmd , boolean IsNew) {
        vm = vmd;
        currentChapterNum = vmd.getChapterNum() ;
        bName =  vmd.getBookName();
        IsNewTestament = IsNew;
        act = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        //ActionBar actionBar = getActionBar().;
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        mAdapter = new MyAdapter(getSupportFragmentManager(),
                new Object[]{vm, IsNewTestament}, this);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(vm.getChapterNum());
      // mAdapter.modify();
    }


    /**MyAdapter Class**/
    public static class MyAdapter extends FragmentStatePagerAdapter {
        private Object[] data;
        boolean updated = false;
        private Context context;


        public MyAdapter(FragmentManager fragmentManager, Object[] data, Context context ) {
            super(fragmentManager);
            boolean IsNew;
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            int x=vm.getTotalChapters(vm.getBookNum());
            return x;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            if (updated){
                updated = false;
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }

        @Override
        public Fragment getItem(int position) {
            return new ChapterFragment(data, position);
        }

        public void modify(){
            updated = true;
            notifyDataSetChanged();
        }
    }
}
