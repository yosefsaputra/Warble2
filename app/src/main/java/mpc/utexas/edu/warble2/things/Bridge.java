package mpc.utexas.edu.warble2.things;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import mpc.utexas.edu.warble2.database.*;
import mpc.utexas.edu.warble2.database.Light;
import mpc.utexas.edu.warble2.services.PhilipsHue.PhilipsHueService;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsBridge;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsLight;
import mpc.utexas.edu.warble2.users.PhilipsHue.PhilipsUser;
import mpc.utexas.edu.warble2.utils.PhilipsHueUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    // TODO make it more general. Is this necessary?
    private PhilipsHueService service;

    public static List<Bridge> discover(){
        List<Bridge> bridges = new ArrayList<>();

        // TODO Add more bridge children, if necessary. Find idea how to do this better because it is too detailed.
        bridges.addAll(PhilipsBridge.discover());

        return bridges;
    }

    public static void updateBridgesToDatabase(Context context, List<Bridge> bridges) {
        Log.d(TAG, "Updating Bridges to Database");
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
        Log.d(TAG, "Getting All Bridges from Database");
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

    // TODO to make the philips light to be general. Is this function located correctly?
    public List<PhilipsLight> getAllPhilipsLights(Context context) {
        Log.d(TAG, "Getting All Philips Lights");
        final List<User> users = this.getAllUsersFromDatabase(context);
        final Bridge bridge = this;
        final List<PhilipsLight> lights = new ArrayList<>();

        ResponseBody responseBody;
        try {
            responseBody = service.getLights(users.get(0).userId).execute().body();
        } catch (IOException ex) {
            ex.printStackTrace();
            responseBody = null;
        }

        JSONObject jsonObject;
        try {
            JSONParser parser = new JSONParser();
            jsonObject = (JSONObject) parser.parse(responseBody.string());
        } catch (ParseException | IOException ex) {
            ex.printStackTrace();
            jsonObject = new JSONObject();
        }

        List<String> lightNames = new ArrayList<>();
        for (Object o : jsonObject.keySet()) {
            String lightName = o.toString();
            lightNames.add(lightName);
        }

        for (String lightName : lightNames) {
            PhilipsBridge philipsBridge = new PhilipsBridge(bridge.name, bridge.UUID, bridge.base_url);
            PhilipsLight philipsLight = new PhilipsLight(lightName, lightName, PhilipsUser.getUserFromUserDb(users.get(0)), philipsBridge);
            lights.add(philipsLight);
        }

        return lights;
    }

    public static void addLightsToDatabase(Context context, String lightName, long bridgeId) {
        Log.d(TAG, "Adding lights to database");

        AppDatabase appDatabase = AppDatabase.getDatabase(context);

        mpc.utexas.edu.warble2.database.Light light = new Light(lightName, bridgeId);
        appDatabase.lightDao().addLight(light);
    }

    public static List<Light> getLightsFromDatabase(Context context) {
        Log.d(TAG, "Getting Light from Database");

        AppDatabase appDatabase = AppDatabase.getDatabase(context);

        return appDatabase.lightDao().getAllLights();
    }

    public String toString() {
        String string = "";
        string += String.format("Name: %s\n", this.name);
        string += String.format("ID: %s\n", this.name);
        string += String.format("Base URL: %s\n", this.base_url);
        return string;
    }
}
