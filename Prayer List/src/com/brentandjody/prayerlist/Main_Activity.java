package com.brentandjody.prayerlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class Main_Activity extends FragmentActivity
        implements RequestListFragment.Callbacks, NewRequestBarFragment.Callbacks {

    private boolean mTwoPane;
    private RequestListFragment requestList;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);
        requestList = ((RequestListFragment) getSupportFragmentManager().findFragmentById(R.id.request_list));

        if (findViewById(R.id.request_detail_container) != null) {
            mTwoPane = true;
            requestList.setActivateOnItemClick(true);
        }
    }

    @Override
    public void onItemSelected(int id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putInt(JournalFragment.ARG_ITEM_ID, id);
            JournalFragment fragment = new JournalFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.request_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, Journal_Activity.class);
            detailIntent.putExtra(JournalFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
    
	@Override
	public void newPrayerRequest(String title) {
		requestList.newPrayerRequest(title);
	}


}
