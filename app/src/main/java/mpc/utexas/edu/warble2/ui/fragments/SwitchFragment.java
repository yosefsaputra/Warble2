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
import mpc.utexas.edu.warble2.things.Light;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsLight;
import mpc.utexas.edu.warble2.things.Thing;

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

    private class SetLightSwitch extends AsyncTask<Void, Void, List<Light>> {
        @Override
        protected List<Light> doInBackground(Void... params){
            List<Light> lights = new ArrayList<>();

            bridges = Bridge.getAllDb(getContext());
            for (Bridge bridge : bridges) {
                List<Light> lightsInBridge = new ArrayList<>();
                try {
                    lightsInBridge = bridge.discoverLights(getContext());
                } catch (RuntimeException e) {
                    Log.d(TAG, "exception", e);
                }

                lights.addAll(lightsInBridge);
            }

            return lights;
        }

        @Override
        protected void onPostExecute(List<Light> lights) {
            final Switch lightSwitch = (Switch) getView().findViewById(R.id.lightSwitch);
            final List<Light> lights2 = lights;

            lightSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (lightSwitch.isChecked()) {
                        try {
                            for (Light light : lights2) {
                                light.setOn();
                            }
                        } catch (NullPointerException ex) {}

                    }
                    else {
                        try {
                            for (Light light : lights2) {
                                light.setOff();
                            }
                        } catch (NullPointerException ex) {}
                    }
                }
            });
        }
    }
}
