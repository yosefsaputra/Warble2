package mpc.utexas.edu.warble2.ui.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import mpc.utexas.edu.warble2.R;
import mpc.utexas.edu.warble2.database.LocationConverter;
import mpc.utexas.edu.warble2.features.Location;
import mpc.utexas.edu.warble2.things.Bridge;
import mpc.utexas.edu.warble2.things.Light;
import mpc.utexas.edu.warble2.ui.MainActivity;

/**
 * Created by yosef on 11/28/2017.
 */

public class SwitchFragment extends Fragment {
    private static String TAG = "SwitchFragment";

    private static Location currentLocation = new Location(0, 0);
    private static double distance = 3.0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_switch, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button switchRefreshButton = getView().findViewById(R.id.switchRefreshButton);
        switchRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().finish();
                startActivity(intent);
            }
        });

        // Set Light Switch
        callAsyncTask();

        // Set Current Location Views
        setCurrentLocationViews();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void callAsyncTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SetLightSwitch setLightSwitch = new SetLightSwitch();
                            setLightSwitch.execute();
                        } catch (Exception e) {
                            Log.d(TAG, "exception", e);
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 10000);
    }

    private class SetLightSwitch extends AsyncTask<Void, Void, List<Light>> {
        @Override
        protected List<Light> doInBackground(Void... params){
            List<Light> lights = new ArrayList<>();
            List<Bridge> bridges;

            bridges = Bridge.getAllDb(getContext());
            for (Bridge bridge : bridges) {
                Log.d(TAG, bridge.getName());
                List<Light> lightsInBridge = new ArrayList<>();
                try {
                    lightsInBridge = bridge.discoverLights(getContext());
                } catch (RuntimeException e) {
                    Log.d(TAG, "exception", e);
                }

                lights.addAll(lightsInBridge);
            }

            List<Light> filteredLights = new ArrayList<>();

            if (distance > 0.0) {
                for (Light light : lights) {
                    int xDiff = currentLocation.getxCoordinate() - light.getLocation().getxCoordinate();
                    int yDiff = currentLocation.getyCoordinate() - light.getLocation().getyCoordinate();
                    double lightDistance = Math.sqrt(xDiff * xDiff + yDiff * yDiff);

                    Log.d(TAG, "TAG: lightDistance = " + lightDistance);
                    if (lightDistance <= distance) {
                        filteredLights.add(light);
                    }
                }
            } else {
                filteredLights.addAll(lights);
            }

            return filteredLights;
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

    private void setCurrentLocationViews() {
        TextView currentLocationTextView = (TextView) getView().findViewById(R.id.currentLocationTextView);
        currentLocationTextView.setText((CharSequence) LocationConverter.toString(currentLocation));

        Button newCurrentLocationSubmitButton = (Button) getView().findViewById(R.id.newCurrentLocationSubmitButton);
        newCurrentLocationSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText newCurrentLocationEditText = (EditText) getView().findViewById(R.id.newCurrentLocationEditText);
                currentLocation = LocationConverter.toLocation(String.format("(%s)", newCurrentLocationEditText.getText()));

                TextView currentLocationTextView = (TextView) getView().findViewById(R.id.currentLocationTextView);
                currentLocationTextView.setText((CharSequence) LocationConverter.toString(currentLocation));

                newCurrentLocationEditText.setText("");

                new SetLightSwitch().execute();
            }
        });
    }
}
