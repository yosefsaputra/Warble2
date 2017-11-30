package mpc.utexas.edu.warble2.ui.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import mpc.utexas.edu.warble2.R;
import mpc.utexas.edu.warble2.things.Bridge;

/**
 * Created by yosef on 11/28/2017.
 */

public class InfoFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.listBridgesSwipeRefresh);
        final SwipeRefreshLayout.OnRefreshListener swipeRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new BridgeDiscovery().execute();
            }
        };
        swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);

        // Refreshing programmatically
        swipeRefreshLayout.post(new Runnable() {
            @Override public void run() {
                swipeRefreshLayout.setRefreshing(true);
                swipeRefreshListener.onRefresh();
            }
        });
    }

    public class BridgeDiscovery extends AsyncTask<Void, Void, List<Bridge>> {
        @Override
        protected List<Bridge> doInBackground(Void... params){
            return Bridge.discover();
        }

        @Override
        protected void onPostExecute(List<Bridge> bridges) {
            ListView bridgeListView = getView().findViewById(R.id.listBridgesView);
            ArrayAdapter<Bridge> adapter = new BridgeArrayAdapter(getContext(), bridges);
            bridgeListView.setAdapter(adapter);

            SwipeRefreshLayout swipeRefreshLayout = getView().findViewById(R.id.listBridgesSwipeRefresh);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public class BridgeArrayAdapter extends ArrayAdapter<Bridge> {
        private final Context context;
        private final List<Bridge> bridges;

        public BridgeArrayAdapter(Context context, List<Bridge> bridges) {
            super(context, -1, bridges);
            this.context = context;
            this.bridges = bridges;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.bridge_layout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.bridgeNameTextView);
            textView.setText(bridges.get(position).getName());
            return rowView;
        }
    }

}
