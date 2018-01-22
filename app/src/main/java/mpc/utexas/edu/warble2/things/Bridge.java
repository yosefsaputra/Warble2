package mpc.utexas.edu.warble2.things;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.User;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsBridge;

/**
 * Created by yosef on 11/12/2017.
 */

public class Bridge extends Thing {
    public static String identifier = "Bridge";
    public static String TAG = "Bridge";
    protected String name;
    protected String id;
    protected String base_url;
    protected String UUID;

    public static List<Bridge> discover(){
        List<Bridge> bridges = new ArrayList<>();

        // TODO Add more bridge children, if necessary. Find idea how to do this better because it is too detailed.
        bridges.addAll(PhilipsBridge.discover());

        return bridges;
    }

    public static void updateBridgesToDatabase(Context context, List<Bridge> bridges) {
        AppDatabase appDatabase = AppDatabase.getDatabase(context);

        for (Bridge bridge: bridges) {
            mpc.utexas.edu.warble2.database.Bridge foundDbBridge = appDatabase.bridgeDao().getBridgeByUUID(bridge.UUID);
            mpc.utexas.edu.warble2.database.Bridge dbBridge = new mpc.utexas.edu.warble2.database.Bridge(bridge.UUID, bridge.name, bridge.base_url);

            if (foundDbBridge == null) {
                appDatabase.bridgeDao().addBridge(dbBridge);
            } else {
                appDatabase.bridgeDao().updateBridge(dbBridge);
            }
        }
    }

    public static List<Bridge> getAllBridgesFromDatabase(Context context) {
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        mpc.utexas.edu.warble2.database.Bridge[] dbBridges = appDatabase.bridgeDao().getAllBridges();

        List<Bridge> bridges = new ArrayList<>();

        for (mpc.utexas.edu.warble2.database.Bridge dbBridge: dbBridges) {
            bridges.add(new Bridge(dbBridge.name, dbBridge.UUID, dbBridge.base_url));
        }

        return bridges;
    }

    public static void printBridgesFromDatabase(Context context) {
        Log.d(TAG, "Print Bridges from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        mpc.utexas.edu.warble2.database.Bridge[] bridges = appDatabase.bridgeDao().getAllBridges();
        for (mpc.utexas.edu.warble2.database.Bridge bridge : bridges) {
            Log.d(TAG, String.format("- (id %s, UUID %s, name %s, base_url %s)", bridge.id, bridge.UUID, bridge.name, bridge.base_url));
        }
        Log.d(TAG, "");
    }

    public Bridge(String name, String id, String base_url) {
        this.name = name;
        this.id = id;
        this.UUID = id;
        this.base_url = base_url;
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

    public void addUserToDatabase(Context context, String username, String userId) {
        AppDatabase appDatabase = AppDatabase.getDatabase(context);

        mpc.utexas.edu.warble2.database.Bridge dbBridge = appDatabase.bridgeDao().getBridgeByUUID(this.getUUID());
        System.out.println("This is the ID of dbBridge " + dbBridge.id);
        User user = new User(username, userId, dbBridge.id);
        appDatabase.userDao().addUser(user);
    }

    public List<User> getAllUsersFromDatabase(Context context){
        AppDatabase appDatabase = AppDatabase.getDatabase(context);

        mpc.utexas.edu.warble2.database.Bridge dbBridge = appDatabase.bridgeDao().getBridgeByUUID(this.getUUID());

        return appDatabase.userDao().getUsersForBridge(dbBridge.id);
    }

    public String toString() {
        String string = "";
        string += String.format("Name: %s\n", this.name);
        string += String.format("ID: %s\n", this.name);
        string += String.format("Base URL: %s\n", this.base_url);
        return string;
    }
}
