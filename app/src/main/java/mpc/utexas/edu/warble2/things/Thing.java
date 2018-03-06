package mpc.utexas.edu.warble2.things;

import android.content.Context;
import android.util.Log;

import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.DatabaseInterface;
import mpc.utexas.edu.warble2.database.ThingDb;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsBridge;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsLight;
import mpc.utexas.edu.warble2.things.Wink.WinkBridge;
import mpc.utexas.edu.warble2.things.Wink.WinkLight;

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

        if (thingDb != null) {
            if (thingDb.category.equals(PhilipsLight.identifier)) {
                return new PhilipsLight(thingDb.name, thingDb.id, thingDb.location, null, (PhilipsBridge) Bridge.getBridgeById(context, thingDb.bridgeDbid), dbid);
            } else if (thingDb.category.equals(WinkLight.identifier)) {
                return new WinkLight(thingDb.name, thingDb.id, thingDb.location, (WinkBridge) Bridge.getBridgeById(context, thingDb.bridgeDbid), dbid);
            }
        }

        return null;
    }

    public static void deleteAllDb(Context context) {
        Log.d(TAG, "Delete All Things from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        appDatabase.thingDao().deleteAllThings();
    }
    // ========= [end Static methods] =========

    // ======== [start Others methods] ========
    public String toString() {
        String string = "";
        string += String.format("Name: %s, ", this.name);
        string += String.format("Id: %s, ", this.id);
        string += String.format("Dbid: %s", this.dbid);
        return string;
    }
    // ========= [end Others methods] =========

}
