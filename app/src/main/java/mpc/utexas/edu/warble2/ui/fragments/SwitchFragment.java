package mpc.utexas.edu.warble2.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.R;
import mpc.utexas.edu.warble2.things.Bridge;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsLight;

/**
 * Created by yosef on 11/28/2017.
 */

public class SwitchFragment extends Fragment {
    private static String TAG = "SwitchFragment";

    List<PhilipsLight> lights = new ArrayList<>();
    List<Bridge> bridges;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_switch, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        new SetLightSwitch().execute();
    }

    private class SetLightSwitch extends AsyncTask<Void, Void, List<PhilipsLight>> {
        @Override
        protected List<PhilipsLight> doInBackground(Void... params){
            List<PhilipsLight> lights = new ArrayList<>();

            bridges = Bridge.getAllBridgesFromDatabase(getContext());
            for (Bridge bridge : bridges) {
                List<PhilipsLight> lightsInBridge = new ArrayList<>();
                try {
                    lightsInBridge = bridge.getAllPhilipsLights(getContext());
                } catch (RuntimeException e) {
                    Log.d(TAG, e.getMessage());
                }

                lights.addAll(lightsInBridge);
            }

            return lights;
        }

        @Override
        protected void onPostExecute(List<PhilipsLight> lights) {
            final Switch lightSwitch = (Switch) getView().findViewById(R.id.lightSwitch);
            final List<PhilipsLight> lights2 = lights;

            lightSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (lightSwitch.isChecked()) {
                        try {
                            for (PhilipsLight light : lights2) {
                                light.setOn();
                            }
                        } catch (NullPointerException ex) {}

                    }
                    else {
                        try {
                            for (PhilipsLight light : lights2) {
                                light.setOff();
                            }
                        } catch (NullPointerException ex) {}
                    }
                }
            });
        }
    }
}
