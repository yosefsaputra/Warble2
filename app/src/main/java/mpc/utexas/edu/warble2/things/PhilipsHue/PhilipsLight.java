package mpc.utexas.edu.warble2.things.PhilipsHue;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.BridgeDb;
import mpc.utexas.edu.warble2.database.ThingDb;
import mpc.utexas.edu.warble2.database.UserDb;
import mpc.utexas.edu.warble2.features.Location;
import mpc.utexas.edu.warble2.services.PhilipsHue.PhilipsHueService;
import mpc.utexas.edu.warble2.things.Light;
import mpc.utexas.edu.warble2.users.PhilipsHue.PhilipsUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yosef on 11/7/2017.
 */

public class PhilipsLight extends Light {
    public static String identifier = "PhilipsLight";
    public static String TAG = "PhilipsLight";
    protected PhilipsHueService service;
    protected PhilipsBridge parentBridge;
    protected PhilipsUser user;


    // ======== [start Constructor implementation] ========
    public PhilipsLight(String name, Location location, PhilipsUser user, PhilipsBridge parentBridge) {
        this.name = name;
        this.id = name;
        this.service = parentBridge.getService();

        this.location = location;

        this.parentBridge = parentBridge;
        this.user = user;
    }
    // ========= [end Constructor implementation] =========


    // ======== [start Getter Setter implementation] ========
    public PhilipsHueService getService() {
        return this.service;
    }

    public PhilipsBridge getParentBridge() {
        return this.parentBridge;
    }

    public PhilipsUser getUser() {
        return this.user;
    }
    // ========= [end Getter Setter implementation] =========


    // ======== [start Static methods] ========
    public static List<PhilipsLight> getAllDb(Context context) {
        Log.d(TAG, "Getting All PhilipsLights from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        // TODO Filter PhilipsLights as thingCategory
        List<ThingDb> dblights =  appDatabase.thingDao().getAllThings();

        List<PhilipsLight> lights = new ArrayList<>();

        for (ThingDb dblight: dblights) {
            BridgeDb bridgeDb = appDatabase.bridgeDao().getBridge(dblight.bridgeDbid);
            UserDb userDb = appDatabase.userDao().getUser(dblight.userDbid);
            lights.add(new PhilipsLight(dblight.name, dblight.location, new PhilipsUser(userDb.name, userDb.id, userDb.bridgeDbid), new PhilipsBridge(bridgeDb.name, bridgeDb.UUID, bridgeDb.baseUrl)));
        }

        return lights;
    }

    public static void deleteAllDb(Context context) {
        Log.d(TAG, "Deleting All PhilipsLights from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        // TODO Delete only PhilipsLights
        appDatabase.thingDao().deleteAllThings();
    }
    // ========= [end Static methods] =========


    // ======== [start DatabaseInterface implementation] ========
    public void addDb(Context context) {
        Log.d(TAG, "Adding PhilipsLight to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        BridgeDb bridgeDb = appDatabase.bridgeDao().getBridgeByUUID(this.parentBridge.getUUID());
        UserDb userDb = appDatabase.userDao().getUserById(this.user.getId());
        appDatabase.thingDao().addThing(new ThingDb(this.name, this.name, PhilipsLight.identifier, this.location, bridgeDb.dbid, userDb.dbid));
    }

    public void updateDb(Context context) {
        Log.d(TAG, "Updating PhilipsLight to Database - Not Implemented");
    }

    public void deleteDb(Context context) {
        Log.d(TAG, "Deleting PhilipsLight to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        BridgeDb bridgeDb = appDatabase.bridgeDao().getBridgeByUUID(this.parentBridge.getUUID());
        ThingDb philipsLight = appDatabase.thingDao().getThingByFeature(this.name, identifier, bridgeDb.dbid);
        appDatabase.thingDao().deleteThing(philipsLight.dbid);
    }
    // ========= [end DatabaseInterface implementation] =========


    // ======== [start ThingInterface implementation] ========
    @Override
    public String getCapability() {
        return "Return capability of PhilipsLight";
    }
    // ========= [end ThingInterface implementation] =========


    // ======== [start LightInterface implementation] ========
    @Override
    public void setOn() {
        HashMap<String, Object> lightState = new HashMap<>();
        lightState.put("on", true);
        lightState.put("bri", 50);

        service.putLight(this.user.getId(), this.id, lightState).enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                System.out.println(response.body());
            }

            @Override
            public void onFailure(Call<List<Object>> call, Throwable t) {}
        });
    }

    @Override
    public void setOff() {
        HashMap<String, Object> lightState = new HashMap<>();
        lightState.put("on", false);
        lightState.put("transitiontime", 0);

        service.putLight(this.user.getId(), this.id, lightState).enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                System.out.println(response.body());
            }

            @Override
            public void onFailure(Call<List<Object>> call, Throwable t) {}
        });
    }
    // ========= [end LightInterface implementation] =========


    // ======== [start LocationInterface implementation] ========
    @Override
    public void setLocation(Location location) {
        //placeholder
    }

    @Override
    public Location getLocation() {
        return new Location(0, 0);
    }
    // ========= [end LocationInterface implementation] =========
}
