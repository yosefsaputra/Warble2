package mpc.utexas.edu.warble2.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import mpc.utexas.edu.warble2.R;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsBridge;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsLight;
import mpc.utexas.edu.warble2.ui.MainActivity;
import mpc.utexas.edu.warble2.users.PhilipsHue.PhilipsUser;

/**
 * Created by yosef on 11/28/2017.
 */

public class SwitchFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_switch, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final Switch lightSwitch = (Switch) getView().findViewById(R.id.lightSwitch);
        lightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhilipsUser user = new PhilipsUser(MainActivity.username, MainActivity.userid);
                PhilipsBridge bridge = new PhilipsBridge("hue", "hue", "http://192.168.1.74");
                PhilipsLight light = new PhilipsLight("10", "10", user, bridge);
                if (lightSwitch.isChecked()) {
                    light.setOn();
                }
                else {
                    light.setOff();
                }
            }
        });
    }
}
