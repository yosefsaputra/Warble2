package mpc.utexas.edu.warble2.things;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.BridgeDb;
import mpc.utexas.edu.warble2.database.DatabaseInterface;
import mpc.utexas.edu.warble2.database.ThingDb;
import mpc.utexas.edu.warble2.database.UserDb;
import mpc.utexas.edu.warble2.features.Location;
import mpc.utexas.edu.warble2.services.Service;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsBridge;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsLight;
import mpc.utexas.edu.warble2.things.Wink.WinkBridge;
import mpc.utexas.edu.warble2.users.PhilipsHue.PhilipsUser;
import mpc.utexas.edu.warble2.users.User;

/**
 * Created by yosef on 11/7/2017.
 */

public abstract class Thing implements DatabaseInterface {
    public static String TAG = "Thing";
    public static String identifier = "Thing";
    protected String name;
    protected String id;
    protected long dbid;

    // ======== [start Getter Setter implementation] ========
    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public long getDbid() {
        return this.dbid;
    }
    // ========= [end Getter Setter implementation] =========


    // ======== [start Static methods] ========
    public static Thing getThingByDbid(Context context, long dbid) {
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        ThingDb thingDb = appDatabase.thingDao().getThing(dbid);

        if (thingDb.category.equals(PhilipsLight.identifier)) {
            PhilipsUser user = (PhilipsUser) User.getUserByDbid(context, thingDb.userDbid);
            Log.d(TAG, user.getName());
            return new PhilipsLight(thingDb.name, thingDb.location, user, (PhilipsBridge) Bridge.getBridgeById(context, thingDb.bridgeDbid), dbid);
        }
        return null;
    }

    public static void deleteAllDb(Context context) {
        Log.d(TAG, "Deleting All Things from Database  ...");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        appDatabase.thingDao().deleteAllThings();
    }
    // ========= [end Static methods] =========

    // ======== [start Others methods] ========
    public String toString() {
        String string = "";
        string += String.format("Name: %s\n", this.name);
        return string;
    }
    // ========= [end Others methods] =========

}
