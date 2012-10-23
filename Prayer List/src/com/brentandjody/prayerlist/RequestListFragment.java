package com.brentandjody.prayerlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;

public class RequestListFragment extends ListFragment 
		implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private static final int LOADER_ID = 0;
    private static final String[] PROJECTION = {Database.KEY_SUBLIST_ID,Database.KEY_REQUEST_DESCRIPTION,Database.KEY_REQUEST_CHECKED};

    private Callbacks mCallbacks = sCallbacks;
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private RequestListAdapter requestListAdapter;
    private PrayerRequests prayerRequests;
    private int currentItemId = -1;

    public interface Callbacks {
        public void onItemSelected(int id);
    }

    private static Callbacks sCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(int id) {
        }
    };

    // Constructor
    public RequestListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prayerRequests = new PrayerRequests(getActivity());
        getLoaderManager().initLoader(LOADER_ID,null,this);
        requestListAdapter = new RequestListAdapter(getActivity(),null);
        setListAdapter(requestListAdapter);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        setCurrentItem((int) id);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    public void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }
        mActivatedPosition = position;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,ContextMenuInfo menuInfo) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
    	int position = info.position;
    	getListView().setItemChecked(position, true);  //ensure the correct request is selected
    	if (getActivity().findViewById(R.id.request_detail_container) != null) { //display the correct journal (if tablet)
    		int id = (int) info.id;
    		setCurrentItem(id);
    	}
    	MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = item.getItemId();
        switch (id){
        case R.id.Edit: // Edit Prayer Request
    		if (currentItemId != -1) {
    			final PrayerRequest request = prayerRequests.get(currentItemId);
    			final EditText requestTitle = new EditText(getActivity());
    			requestTitle.setText(request.getDescription());
    			AlertDialog.Builder editor = new AlertDialog.Builder(getActivity());
    			editor.setTitle(R.string.edit_request);
    			editor.setView(requestTitle);
    			editor.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					request.setDescription(requestTitle.getText().toString());
    					prayerRequests.update(request);
    					setCurrentItem(request.getId());
    				}
    			});
    			editor.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int which) {
    					// Do nothing but close the dialog
    				}
    			});
    			AlertDialog editorDialog = editor.create();
    			editorDialog.show();
    		}
        	break;
        case R.id.Organize: //Edit Tags
//        	Intent intent = new Intent(getActivity(), TagPickerActivity.class);
//        	intent.putExtra("currentRequestId", (((PrayerJournalActivity) getActivity()).currentRequest.getId()));
//    		getActivity().startActivityForResult(intent,PrayerJournalActivity.SELECT_TAGS);
        	break;
        case R.id.Delete: // Delete Prayer Request
        	new AlertDialog.Builder(getActivity())
        	.setTitle(R.string.areyousure)
        	.setMessage(R.string.clickyestodelete)
        	.setPositiveButton(R.string.yes, new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					prayerRequests.delete(currentItemId);
					setCurrentItem(-1);
				}
        	})
        	.setNegativeButton(R.string.no,null)
        	.show();
        	break;
        case R.id.Answered: // Mark as Answered
        	break;
        }
        return true;
    }
    
    public void setCurrentItem(int id) {
    	currentItemId = id;
    	mCallbacks.onItemSelected(id);
    }
    
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
      return new CursorLoader(getActivity(), PrayerRequestProvider.CONTENT_URI,PROJECTION, null, null, null);
    }
    
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
      requestListAdapter.swapCursor(cursor);
    }
   
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
      requestListAdapter.swapCursor(null);
    } 
    
    public void newPrayerRequest(String title) {
    	PrayerRequest request = new PrayerRequest(title);
    	int id = prayerRequests.add(request);
//TODO:FIX
    	// Select the new item
    	setCurrentItem(id);
//    	mActivatedPosition = getListView().getCount();
//    	getListView().setItemChecked(mActivatedPosition, true);
    }
    
}
