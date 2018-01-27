package mpc.utexas.edu.warble2.things;

import android.content.Context;
import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.BridgeDb;
import mpc.utexas.edu.warble2.database.UserDb;
import mpc.utexas.edu.warble2.services.PhilipsHue.PhilipsHueService;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsBridge;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsLight;
import mpc.utexas.edu.warble2.users.PhilipsHue.PhilipsUser;
import mpc.utexas.edu.warble2.utils.PhilipsHueUtil;
import okhttp3.ResponseBody;

/**
 * Created by yosef on 11/12/2017.
 */

public class Bridge extends mpc.utexas.edu.warble2.things.Thing {
    public static String identifier = "Bridge";
    public static String TAG = "Bridge";
    protected String name;
    protected String id;
    protected String base_url;
    protected String UUID;
    // TODO make it more general. Is this necessary?
    private PhilipsHueService service;

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
            bridges.add(new Bridge(dbBridgeDb.name, dbBridgeDb.UUID, dbBridgeDb.base_url));
        }

        return bridges;
    }

    public static void deleteAllDb(Context context) {
        Log.d(TAG, "Deleting All Bridges from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        appDatabase.bridgeDao().deleteAllBridges();
    }

    public void addDb(Context context) {
        Log.d(TAG, "Add bridge to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        appDatabase.bridgeDao().addBridge(new BridgeDb(this.UUID, this.name, identifier, this.base_url));
    }

    public void updateDb(Context context) {
        Log.d(TAG, "Update bridge to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);

        BridgeDb existingBridgeDb = appDatabase.bridgeDao().getBridgeByUUID(this.UUID);

        if (existingBridgeDb == null) {
            this.addDb(context);
        } else {
            appDatabase.bridgeDao().updateBridge(new BridgeDb(this.UUID, this.name, identifier, this.base_url));
        }
    }

    public void deleteDb(Context context) {
        Log.d(TAG, "Delete bridge to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        BridgeDb deleteBridgeDb = appDatabase.bridgeDao().getBridgeByUUID(this.UUID);
        appDatabase.bridgeDao().delete(deleteBridgeDb.dbid);
    }

//    public static void updateBridgesToDatabase(Context context, List<BridgeDb> bridges) {
//        Log.d(TAG, "Updating Bridges to Database");
//        AppDatabase appDatabase = AppDatabase.getDatabase(context);
//
//        for (BridgeDb bridge: bridges) {
//            mpc.utexas.edu.warble2.database.BridgeDb foundDbBridge = appDatabase.bridgeDao().getBridgeByUUID(bridge.UUID);
//            mpc.utexas.edu.warble2.database.BridgeDb dbBridge = new mpc.utexas.edu.warble2.database.BridgeDb(bridge.UUID, bridge.name, bridge.base_url);
//
//            if (foundDbBridge == null) {
//                appDatabase.bridgeDao().addBridge(dbBridge);
//            } else {
//                appDatabase.bridgeDao().updateBridge(dbBridge);
//            }
//        }
//    }

//    public static void printBridgesFromDatabase(Context context) {
//        Log.d(TAG, "Print Bridges from Database");
//        AppDatabase appDatabase = AppDatabase.getDatabase(context);
//        mpc.utexas.edu.warble2.database.BridgeDb[] bridges = appDatabase.bridgeDao().getAllBridges();
//        for (mpc.utexas.edu.warble2.database.BridgeDb bridge : bridges) {
//            Log.d(TAG, String.format("- (id %s, UUID %s, name %s, base_url %s)", bridge.dbid, bridge.UUID, bridge.name, bridge.base_url));
//        }
//        Log.d(TAG, "");
//    }

    public Bridge(String name, String id, String base_url) {
        this.name = name;
        this.id = id;
        this.UUID = id;
        this.base_url = base_url;

        this.service = PhilipsHueUtil.getService(base_url);
    }

    public String getUUID(){
        return this.UUID;
    }

    public void setUUID(String UUID){
        this.UUID = UUID;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getBaseUrl(){
        return this.base_url;
    }

    public void setBaseUrl(String base_url){
        this.base_url = base_url;
    }

//    public void addUserToDatabase(Context context, String username, String userId) {
//        AppDatabase appDatabase = AppDatabase.getDatabase(context);
//
//        mpc.utexas.edu.warble2.database.BridgeDb dbBridge = appDatabase.bridgeDao().getBridgeByUUID(this.getUUID());
//        System.out.println("This is the ID of dbBridge " + dbBridge.dbid);
//        UserDb user = new UserDb(username, userId, dbBridge.dbid);
//        appDatabase.userDao().addUser(user);
//    }

//    public List<UserDb> getAllUsersFromDatabase(Context context){
//        AppDatabase appDatabase = AppDatabase.getDatabase(context);
//
//        mpc.utexas.edu.warble2.database.BridgeDb dbBridge = appDatabase.bridgeDao().getBridgeByUUID(this.getUUID());
//
//        return appDatabase.userDao().getUsersForBridge(dbBridge.dbid);
//    }

    // TODO to make the philips light to be general. Is this function located correctly?
    public List<Thing> discoverThings(Context context) {
        Log.d(TAG, "Discover Things");

        List<Thing> things = new ArrayList<>();

        things.addAll(this.discoverLights(context));

        return things;
    }

    public List<Light> discoverLights(Context context) {
        Log.d(TAG, "Discover Lights");

        List<Light> lights = new ArrayList<>();

        PhilipsBridge philipsBridge = (PhilipsBridge) this;
        lights.addAll(philipsBridge.discoverPhilipsLights(context));

        return lights;
    }

//    public static void addLightsToDatabase(Context context, String lightName, long bridgeId) {
//        Log.d(TAG, "Adding lights to database");
//
//        AppDatabase appDatabase = AppDatabase.getDatabase(context);
//
//        mpc.utexas.edu.warble2.database.ThingDb thing = new mpc.utexas.edu.warble2.database.ThingDb(lightName, bridgeId);
//        appDatabase.lightDao().addLight(thing);
//    }

//    public static List<ThingDb> getLightsFromDatabase(Context context) {
//        Log.d(TAG, "Getting ThingDb from Database");
//
//        AppDatabase appDatabase = AppDatabase.getDatabase(context);
//
//        return appDatabase.lightDao().getAllLights();
//    }

    public String toString() {
        String string = "";
        string += String.format("Name: %s\n", this.name);
        string += String.format("ID: %s\n", this.name);
        string += String.format("Base URL: %s\n", this.base_url);
        return string;
    }
}
