package mpc.utexas.edu.warble2.things;

import android.content.Context;
import android.util.Log;

import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.BridgeDb;
import mpc.utexas.edu.warble2.database.ThingDb;
import mpc.utexas.edu.warble2.features.Location;
import mpc.utexas.edu.warble2.features.LocationInterface;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsBridge;

/**
 * Created by yosef on 11/7/2017.
 */

public abstract class Light extends Thing implements LightInterface, LocationInterface {
    public static String identifier = "Light";
    public static String TAG = "Light";

    protected Location location;
}
