package com.brentandjody.prayerlist;


import android.support.v4.app.Fragment;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class NewRequestBarFragment extends Fragment {
	
	private Callbacks mCallbacks = prayerRequestCallbacks;

	public interface Callbacks {
		public void newPrayerRequest(String text);
	}
	
	private static Callbacks prayerRequestCallbacks = new Callbacks() {
		@Override
		public void newPrayerRequest(String text) {
		}
	};
	
	private EditText requestText;

	public NewRequestBarFragment() {
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_request_bar, container, false);
        requestText = (EditText) rootView.findViewById(R.id.newRequest);
        Button addButton = (Button) rootView.findViewById(R.id.addButton);
        addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String title = requestText.getText().toString();
				if (! title.equals(""))
				{
					mCallbacks.newPrayerRequest(title);
					requestText.setText("");
				}
			}
        });
        return rootView;
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
        mCallbacks = prayerRequestCallbacks;
    }
}
