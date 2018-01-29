package mpc.utexas.edu.warble2.things;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.BridgeDb;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsBridge;

/**
 * Created by yosef on 11/12/2017.
 */

public class Bridge extends Thing implements BridgeInterface {
    public static String identifier = "Bridge";
    public static String TAG = "Bridge";
    protected String baseUrl;
    protected String UUID;


    // ======== [start Constructor methods] ========
    public Bridge(String name, String id, String baseUrl) {
        this.name = name;
        this.id = id;
        this.UUID = id;
        this.baseUrl = baseUrl;
    }
    // ========= [end Constructor methods] =========


    // ======== [start Getter Setter methods] ========
    public String getUUID(){
        return this.UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getBaseUrl(){
        return this.baseUrl;
    }
    // ========= [end Getter Setter methods] =========


    // ======== [start Static methods] ========
    public static List<Bridge> discover(){
        List<Bridge> bridges = new ArrayList<>();

        // TODO Add more bridge children, if necessary. Find idea how to do this better because it is too detailed.
        bridges.addAll(PhilipsBridge.discover());

        return bridges;
    }

    public static List<Bridge> getAllDb(Context context) {
        Log.d(TAG, "Getting All Bridges from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        BridgeDb[] dbBridgeDbs = appDatabase.bridgeDao().getAllBridges();

        List<Bridge> bridges = new ArrayList<>();

        for (BridgeDb dbBridgeDb : dbBridgeDbs) {
            if (dbBridgeDb.category.equals(PhilipsBridge.identifier)) {
                bridges.add(new PhilipsBridge(dbBridgeDb.name, dbBridgeDb.UUID, dbBridgeDb.baseUrl));
            } else {
                bridges.add(new Bridge(dbBridgeDb.name, dbBridgeDb.UUID, dbBridgeDb.baseUrl));
            }
        }

        return bridges;
    }

    public static void deleteAllDb(Context context) {
        Log.d(TAG, "Deleting All Bridges from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        appDatabase.bridgeDao().deleteAllBridges();
    }
    // ========= [end Static methods] =========


    // ======== [start DatabaseInterface implementation] ========
    @Override
    public void addDb(Context context) {
        Log.d(TAG, "Add bridge to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        appDatabase.bridgeDao().addBridge(new BridgeDb(this.UUID, this.name, this.getClass().getSimpleName(), this.baseUrl));
    }

    @Override
    public void updateDb(Context context) {
        Log.d(TAG, "Update bridge to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);

        BridgeDb existingBridgeDb = appDatabase.bridgeDao().getBridgeByUUID(this.UUID);

        if (existingBridgeDb == null) {
            this.addDb(context);
        } else {
            appDatabase.bridgeDao().updateBridge(new BridgeDb(this.UUID, this.name, this.getClass().getSimpleName(), this.baseUrl));
        }
    }

    @Override
    public void deleteDb(Context context) {
        Log.d(TAG, "Delete bridge to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        BridgeDb deleteBridgeDb = appDatabase.bridgeDao().getBridgeByUUID(this.UUID);
        appDatabase.bridgeDao().delete(deleteBridgeDb.dbid);
    }
    // ========= [end DatabaseInterface implementation] =========


    // ======== [start ThingInterface implementation] ========
    @Override
    public String getCapability() {
        return "Return Capabilities of Bridge";
    }
    // ========= [end ThingInterface implementation] =========


    // ======== [start BridgeInterface implementation] ========
    @Override
    public List<Thing> discoverThings(Context context) {
        Log.d(TAG, "Discover Things");
        return new ArrayList<>();
    }

    @Override
    public List<Light> discoverLights(Context context) {
        Log.d(TAG, "Discover Lights");
        return new ArrayList<>();
    }
    // ========= [end BridgeInterface implementation] =========


    // ======== [start Others implementation] ========
    public String toString() {
        String string = "";
        string += String.format("Name: %s\n", this.name);
        string += String.format("ID: %s\n", this.name);
        string += String.format("Base URL: %s\n", this.baseUrl);
        return string;
    }
    // ========= [end Others implementation] =========
}
