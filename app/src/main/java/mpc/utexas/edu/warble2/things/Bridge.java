package mpc.utexas.edu.warble2.things;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.BridgeDb;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsBridge;
import mpc.utexas.edu.warble2.things.Wink.WinkBridge;
import mpc.utexas.edu.warble2.users.PhilipsHue.PhilipsUser;
import mpc.utexas.edu.warble2.users.User;
import mpc.utexas.edu.warble2.users.Wink.WinkUser;

/**
 * Created by yosef on 11/12/2017.
 */

public abstract class Bridge extends Thing implements BridgeInterface {
    public static String identifier = "Bridge";
    public static String TAG = "Bridge";
    public static String bridgeToUser = "manytoone";
    protected String baseUrl;
    protected String UUID;
    protected String category;
    protected User user;


    // ======== [start Constructor methods] ========
    public Bridge(String name, String id, String baseUrl, String category, User user) {
        this.name = name;
        this.id = id;
        this.UUID = id;
        this.baseUrl = baseUrl;
        if (category == null) {
            this.category = identifier;
        } else {
            this.category = category;
        }
        this.user = user;
    }

    public Bridge(String name, String id, String baseUrl, String category, User user, long dbid) {
        this.name = name;
        this.id = id;
        this.UUID = id;
        this.baseUrl = baseUrl;
        if (category == null) {
            this.category = identifier;
        } else {
            this.category = category;
        }
        this.user = user;

        this.dbid = dbid;
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

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    // ========= [end Getter Setter methods] =========


    // ======== [start Static methods] ========
    public static List<Bridge> discover(Context context){
        List<Bridge> bridges = new ArrayList<>();

        // TODO Add more bridge children, if necessary. Find idea how to do this better because it is too detailed.
        bridges.addAll(PhilipsBridge.discover(context));
        bridges.addAll(WinkBridge.discover(context));

        return bridges;
    }

    public static List<Bridge> getAllDb(Context context) {
        Log.d(TAG, "Get All Bridges from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        BridgeDb[] dbBridgeDbs = appDatabase.bridgeDao().getAllBridges();

        List<Bridge> bridges = new ArrayList<>();

        for (BridgeDb dbBridgeDb : dbBridgeDbs) {
            if (dbBridgeDb.category.equals(PhilipsBridge.identifier)) {
                bridges.add(new PhilipsBridge(dbBridgeDb.name, dbBridgeDb.UUID, dbBridgeDb.baseUrl, PhilipsUser.getUserByDbid(context, dbBridgeDb.userDbid), dbBridgeDb.dbid));
            } else if (dbBridgeDb.category.equals(WinkBridge.identifier)) {
                bridges.add(new WinkBridge(dbBridgeDb.name, dbBridgeDb.UUID, WinkUser.getUserByDbid(context, dbBridgeDb.userDbid), dbBridgeDb.dbid));
            }
        }

        return bridges;
    }

    public static void deleteAllDb(Context context) {
        Log.d(TAG, "Deleting All Bridges from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        appDatabase.bridgeDao().deleteAllBridges();
    }

    public static Bridge getBridgeById(Context context, long dbid) {
        Log.d(TAG, "Getting Bridge by Id from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        BridgeDb dbBridgeDb = appDatabase.bridgeDao().getBridge(dbid);

        Bridge bridge = null;

        if (dbBridgeDb.category.equals(PhilipsBridge.identifier)) {
            bridge = new PhilipsBridge(dbBridgeDb.name, dbBridgeDb.UUID, dbBridgeDb.baseUrl, PhilipsUser.getUserByDbid(context, dbBridgeDb.userDbid), dbBridgeDb.dbid);
        } else if (dbBridgeDb.category.equals(WinkBridge.identifier)) {
            bridge = new WinkBridge(dbBridgeDb.name, dbBridgeDb.UUID, WinkUser.getUserByDbid(context, dbBridgeDb.userDbid), dbBridgeDb.dbid);
        }

        return bridge;
    }
    // ========= [end Static methods] =========


    // ======== [start DatabaseInterface implementation] ========
    @Override
    public void addDb(Context context) {
        Log.d(TAG, "Add bridge to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        if (this.user == null) {
            this.dbid = appDatabase.bridgeDao().addBridge(new BridgeDb(this.UUID, this.name, this.getClass().getSimpleName(), this.baseUrl, 0));
        } else {
            this.dbid = appDatabase.bridgeDao().addBridge(new BridgeDb(this.UUID, this.name, this.getClass().getSimpleName(), this.baseUrl, this.user.getDbid()));
        }
    }

    @Override
    public void updateDb(Context context) {
        Log.d(TAG, "Update bridge to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);

        BridgeDb existingBridgeDb = appDatabase.bridgeDao().getBridgeByUUID(this.UUID);

        if (existingBridgeDb == null) {
            this.addDb(context);
        } else {
            if (this.user == null) {
                BridgeDb bridgeDb = new BridgeDb(this.UUID, this.name, this.getClass().getSimpleName(), this.baseUrl, 0);
                bridgeDb.dbid = this.dbid;
                appDatabase.bridgeDao().updateBridge(bridgeDb);
            } else {
                BridgeDb bridgeDb = new BridgeDb(this.UUID, this.name, this.getClass().getSimpleName(), this.baseUrl, this.user.getDbid());
                bridgeDb.dbid = this.dbid;
                appDatabase.bridgeDao().updateBridge(bridgeDb);
            }
        }
    }

    @Override
    public void deleteDb(Context context) {
        Log.d(TAG, "Delete bridge from Database");
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
    public abstract List<Thing> discoverThings(Context context);
    public abstract List<Light> discoverLights(Context context);
    // ========= [end BridgeInterface implementation] =========


    // ======== [start Others methods] ========
    public String toString() {
        String string = "";
        string += String.format("Name: %s, ", this.name);
        string += String.format("ID: %s, ", this.UUID);
        string += String.format("Base URL: %s, ", this.baseUrl);
        string += String.format("User: %s", this.user);
        return string;
    }
    // ========= [end Others methods] =========
}
