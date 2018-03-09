package mpc.utexas.edu.warble2.ui.fragments;

import android.content.Context;
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
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.annotations.Expose;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.R;
import mpc.utexas.edu.warble2.database.LocationConverter;
import mpc.utexas.edu.warble2.features.Location;
import mpc.utexas.edu.warble2.services.Demo.DemoService;
import mpc.utexas.edu.warble2.things.Light;
import mpc.utexas.edu.warble2.ui.MainActivity;
import mpc.utexas.edu.warble2.ui.views.CanvasView;
import mpc.utexas.edu.warble2.ui.views.Point;
import mpc.utexas.edu.warble2.utils.DemoUtil;
import mpc.utexas.edu.warble2.utils.ViewUtil;
import okhttp3.ResponseBody;

/**
 * Created by yosef on 11/28/2017.
 */

public class SwitchFragment extends Fragment {
    private static String TAG = "SwitchFragment";

    private static Location currentLocation = new Location(0, 0);
    private static float scopeSwitchDistance = 100;
    private static long intervalLightSwitch = 100;

    private static long demoIntervalLocationUpdate = 100;
    private static int demoXLimit;
    private static int demoYLimit;
    private static List<String> demoTrajectoryArray = new ArrayList<>();
    private static List<String> demoLightLocationArray = new ArrayList<>();
    private static int demoTrajectoryArrayCounter;
    private static boolean demoTrajectoryArrayDraw = false;
    private static boolean demoTrajectoryArrayDrawn = false;

    private static Handler demoSetCurrentLocationHandler = new Handler();
    private static Runnable demoSetCurrentLocationRunnable = new Runnable() {
        @Override
        public void run() {
            if (!(demoTrajectoryArray.size() == 0)) try {
                currentLocation = LocationConverter.toLocation(demoTrajectoryArray.get(demoTrajectoryArrayCounter));
                demoTrajectoryArrayCounter++;
            } catch (IndexOutOfBoundsException e) {
                demoTrajectoryArrayCounter = 0;
            }
            demoSetCurrentLocationHandler.postDelayed(this, demoIntervalLocationUpdate);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_switch, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final CanvasView demoLocationCanvasView = (CanvasView) getView().findViewById(R.id.canvasView);
        demoLocationCanvasView.init(null);

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
        setLightSwitch();

        // Set Demo Switch
        setDemoSwitch();

        // Set Device Speed Seek Bar
        setSpeedSeekBar();

        // Set Device Scope Seek Bar
        setScopeSeekBar();

        // Set New Current Location Form
        setNewCurrentLocationForm();

        // Set Current Location View
        final Handler setCurrentLocationViewHandler = new Handler();
        Runnable setCurrentLocationViewRunnable = new Runnable() {
            @Override
            public void run() {
                setCurrentLocationView();

                Point currentLocationPoint = new Point(
                        (int) ViewUtil.pxToDp(getContext(), (float) currentLocation.getxCoordinate() * demoLocationCanvasView.getWidth() / demoXLimit),
                        (int) ViewUtil.pxToDp(getContext(), (float) currentLocation.getyCoordinate() * demoLocationCanvasView.getHeight() / demoYLimit));
                demoLocationCanvasView.setDevicePoint(currentLocationPoint);

                setCurrentLocationViewHandler.postDelayed(this, demoIntervalLocationUpdate);
            }
        };
        setCurrentLocationViewHandler.post(setCurrentLocationViewRunnable);

        // Draw Canvas
        final Handler drawCanvasHandler = new Handler();
        Runnable drawCanvasRunnable = new Runnable() {
            @Override
            public void run() {
                if (demoTrajectoryArrayDraw && !demoTrajectoryArrayDrawn && (demoTrajectoryArray.size() != 0)) {
                    List<Point> demoTrajectoryLocationArray = new ArrayList<>();
                    for (String locationString: demoTrajectoryArray) {
                        Point point = Point.toPoint(locationString);
                        point.x = (int) ViewUtil.pxToDp(getContext(), point.x) * demoLocationCanvasView.getWidth() / demoXLimit;
                        point.y = (int) ViewUtil.pxToDp(getContext(), point.y) * demoLocationCanvasView.getHeight() / demoYLimit;
                        demoTrajectoryLocationArray.add(point);
                    }
                    demoLocationCanvasView.setPathPoints(demoTrajectoryLocationArray);

                    List<Point> demoTrajectoryLightArray = new ArrayList<>();
                    for (String locationString: demoLightLocationArray) {
                        Point point = Point.toPoint(locationString);
                        point.x = (int) ViewUtil.pxToDp(getContext(), point.x) * demoLocationCanvasView.getWidth() / demoXLimit;
                        point.y = (int) ViewUtil.pxToDp(getContext(), point.y) * demoLocationCanvasView.getHeight() / demoYLimit;
                        demoTrajectoryLightArray.add(point);
                    }
                    demoLocationCanvasView.setLightPoints(demoTrajectoryLightArray);

                    demoTrajectoryArrayDrawn = true;
                }

                demoLocationCanvasView.setDeviceScope((int) ViewUtil.pxToDp(getContext(), (float) scopeSwitchDistance * demoLocationCanvasView.getWidth() / demoXLimit));

                drawCanvasHandler.postDelayed(this, demoIntervalLocationUpdate);
            }
        };
        drawCanvasHandler.post(drawCanvasRunnable);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void setDemoSwitch() {
        final Switch demoSwitch = (Switch) getView().findViewById(R.id.demoSwitch);
        demoSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (demoSwitch.isChecked()) {
                    // Set demoTrajectoryArray and demoLightLocationArray
                    new DemoLoadTrajectoryAsyncTask().execute(getContext());

                    // Set TimerTask to update current location with a specific interval
                    demoSetCurrentLocationHandler.post(demoSetCurrentLocationRunnable);
                    
                    demoTrajectoryArrayDraw = true;
                    demoTrajectoryArrayDrawn = false;

                } else if (!demoSwitch.isChecked()) {
                    // Stop the TimerTask
                    demoSetCurrentLocationHandler.removeCallbacks(demoSetCurrentLocationRunnable);

                    // Clear demoTrajectoryArray
                    demoTrajectoryArray.clear();

                    // Clear demoLightLocationArray
                    demoLightLocationArray.clear();

                    demoTrajectoryArrayDraw = false;
                    demoTrajectoryArrayDrawn = false;
                }
            }
        });
    }

    private static class DemoLoadTrajectoryAsyncTask extends AsyncTask<Context, Void, Void> {
        @Override
        protected Void doInBackground(Context... params) {
            DemoService demoService = DemoUtil.getService();
            ResponseBody responseBody;
            try {
                responseBody = demoService.loadTrajectory().execute().body();
                JSONObject jsonObject = new JSONObject(responseBody.string().replace(" ", ""));
                JSONArray jsonArray = jsonObject.getJSONArray("trajectory");
                for (int i = 0; i < jsonArray.length(); i++) {
                    demoTrajectoryArray.add((String) jsonArray.get(i));
                }
                jsonArray = jsonObject.getJSONArray("lights");
                for (int i = 0; i < jsonArray.length(); i++) {
                    demoLightLocationArray.add((String) jsonArray.get(i));
                }
                demoXLimit = Integer.parseInt(jsonObject.getString("width"));
                demoYLimit = Integer.parseInt(jsonObject.getString("height"));
            } catch (IOException | JSONException e) {
                Log.d(TAG, "exception", e);
            }

            // Set lights
            List<Light> lights = new ArrayList<>();
            lights.addAll(Light.getAllDb(params[0]));
            for (int i = 0; i < Math.min(demoLightLocationArray.size(), lights.size()); i++) {
                Log.d(TAG, demoLightLocationArray.get(i));
                lights.get(i).setLocation(LocationConverter.toLocation(demoLightLocationArray.get(i)));
                lights.get(i).updateDb(params[0]);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void params) {}
    }

    private void setLightSwitch() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Light> lights = new ArrayList<>();
                final List<Light> selectedLights = new ArrayList<>();
                int xDiff, yDiff, xCurr, yCurr;

                lights.addAll(Light.getAllDb(getContext()));
                if (scopeSwitchDistance > 0.0) {
                    xCurr = currentLocation.getxCoordinate();
                    yCurr = currentLocation.getyCoordinate();
                    for (Light light : lights) {
                        xDiff = xCurr - light.getLocation().getxCoordinate();
                        yDiff = yCurr - light.getLocation().getyCoordinate();
                        double lightDistance = Math.sqrt(xDiff * xDiff + yDiff * yDiff);

                        if (lightDistance <= scopeSwitchDistance) {
                            selectedLights.add(light);
                        }
                    }
                } else {
                    selectedLights.addAll(lights);
                }

                final Switch lightSwitch = (Switch) getView().findViewById(R.id.lightSwitch);
                lightSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (lightSwitch.isChecked()) {
                            for (Light light : selectedLights) {
                                light.setOn();
                            }
                        } else if (!lightSwitch.isChecked()) {
                            for (Light light : selectedLights) {
                                light.setOff();
                            }
                        }
                    }
                });

                handler.postDelayed(this, intervalLightSwitch);
            }
        };
        handler.post(runnable);
    }

    private void setSpeedSeekBar() {
        SeekBar speedSeekBar = (SeekBar) getView().findViewById(R.id.speedSeekBar);
        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                demoIntervalLocationUpdate = (long) (10 + Math.pow(2, (100 - i) / 10));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setScopeSeekBar() {
        SeekBar scopeSeekBar = (SeekBar) getView().findViewById(R.id.scopeSeekBar);
        scopeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                scopeSwitchDistance = i * 4;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setCurrentLocationView() {
        TextView currentLocationTextView = (TextView) getView().findViewById(R.id.currentLocationTextView);
        currentLocationTextView.setText((CharSequence) LocationConverter.toString(currentLocation));
    }

    private void setNewCurrentLocationForm() {
        Button newCurrentLocationSubmitButton = (Button) getView().findViewById(R.id.newCurrentLocationSubmitButton);
        newCurrentLocationSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText newCurrentLocationEditText = (EditText) getView().findViewById(R.id.newCurrentLocationEditText);
                currentLocation = LocationConverter.toLocation(String.format("(%s)", newCurrentLocationEditText.getText()));

                TextView currentLocationTextView = (TextView) getView().findViewById(R.id.currentLocationTextView);
                currentLocationTextView.setText((CharSequence) LocationConverter.toString(currentLocation));

                newCurrentLocationEditText.setText("");

                // new SetLightSwitch().execute();
            }
        });
    }
}
