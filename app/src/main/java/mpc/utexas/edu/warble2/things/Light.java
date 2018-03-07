package mpc.utexas.edu.warble2.things;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.ThingDb;
import mpc.utexas.edu.warble2.features.Location;
import mpc.utexas.edu.warble2.features.LocationInterface;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsLight;
import mpc.utexas.edu.warble2.things.Wink.WinkLight;

/**
 * Created by yosef on 11/7/2017.
 */

public abstract class Light extends Thing implements LightInterface, LocationInterface {
    public static String identifier = "Light";
    public static String TAG = "Light";

    protected Location location;

    // ======== [start Static methods] ========
    public static List<? extends Light> getAllDb(Context context) {
        List<Light> lights = new ArrayList<>();

        lights.addAll(PhilipsLight.getAllDb(context));
        lights.addAll(WinkLight.getAllDb(context));

        return lights;
    }
    // ======== [start Static methods] ========

    public void updateDb(Context context) {
        Log.d(TAG, "Update Light to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);

        ThingDb existingThingDb = appDatabase.thingDao().getThingById(this.id);

        if (existingThingDb == null) {
            this.addDb(context);
        } else {
            ThingDb thingDb = new ThingDb(this.name, this.id, this.getClass().getSimpleName(), this.location, 0);
            appDatabase.thingDao().updateThing(thingDb);
        }
    }
}
