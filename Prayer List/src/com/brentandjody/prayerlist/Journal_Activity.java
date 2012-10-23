package com.brentandjody.prayerlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class Journal_Activity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

//        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(JournalFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(JournalFragment.ARG_ITEM_ID));
            JournalFragment fragment = new JournalFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.journal_container, fragment)
                    .commit();
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            NavUtils.navigateUpTo(this, new Intent(this, Main_Activity.class));
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
