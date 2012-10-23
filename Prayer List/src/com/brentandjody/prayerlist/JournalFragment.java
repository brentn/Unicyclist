package com.brentandjody.prayerlist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class JournalFragment extends Fragment {

    public static final String ARG_ITEM_ID = "current_request_id";

    PrayerRequest currentRequest;

    public JournalFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            PrayerRequests requests = new PrayerRequests(getActivity());
            currentRequest = requests.get(getArguments().getInt(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_journal, container, false);
        if (currentRequest != null) {
            ((TextView) rootView.findViewById(R.id.journal)).setText(currentRequest.toString());
        }
        return rootView;
    }
}
