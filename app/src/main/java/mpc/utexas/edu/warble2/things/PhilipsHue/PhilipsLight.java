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
import mpc.utexas.edu.warble2.users.User;
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

    // ======== [start Constructor implementation] ========
    public PhilipsLight(String name, Location location, PhilipsUser user, PhilipsBridge parentBridge) {
        this.name = name;
        this.id = name;
        this.service = parentBridge.getService();

        this.location = location;

        this.parentBridge = parentBridge;
    }

    public PhilipsLight(String name, Location location, PhilipsUser user, PhilipsBridge parentBridge, long dbid) {
        this.name = name;
        this.id = name;
        this.service = parentBridge.getService();

        this.location = location;

        this.parentBridge = parentBridge;

        this.dbid = dbid;
    }
    // ========= [end Constructor implementation] =========


    // ======== [start Getter Setter implementation] ========
    public PhilipsHueService getService() {
        return this.service;
    }

    public PhilipsBridge getParentBridge() {
        return this.parentBridge;
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
            lights.add(new PhilipsLight(dblight.name, dblight.location, null, PhilipsBridge.getBridgeById(context, dblight.bridgeDbid)));
        }

        return lights;
    }

    public static void deleteAllDb(Context context) {
        Log.d(TAG, "Deleting All PhilipsLights from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        appDatabase.thingDao().deleteAllThingsByCategory(identifier);
    }
    // ========= [end Static methods] =========


    // ======== [start DatabaseInterface implementation] ========
    public void addDb(Context context) {
        Log.d(TAG, "Adding PhilipsLight to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        BridgeDb bridgeDb = appDatabase.bridgeDao().getBridgeByUUID(this.parentBridge.getUUID());
        this.dbid = appDatabase.thingDao().addThing(new ThingDb(this.name, this.name, PhilipsLight.identifier, this.location, bridgeDb.dbid));
    }

    public void updateDb(Context context) {
        Log.d(TAG, "Updating PhilipsLight to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        BridgeDb bridgeDb = appDatabase.bridgeDao().getBridgeByUUID(this.parentBridge.getUUID());
        appDatabase.thingDao().updateThing(new ThingDb(this.name, this.name, PhilipsLight.identifier, this.location, bridgeDb.dbid));
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

        service.putLightState(this.parentBridge.getUser().getId(), this.id, lightState).enqueue(new Callback<List<Object>>() {
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

        service.putLightState(this.parentBridge.getUser().getId(), this.id, lightState).enqueue(new Callback<List<Object>>() {
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
        this.location = location;
        HashMap<String, Object> locationState = new HashMap<>();
        locationState.put("name", String.format("%s,%s,%s", PhilipsLight.identifier, location.getxCoordinate(), location.getyCoordinate()));

        this.service.putLight(this.parentBridge.getUser().getId(), this.id, locationState).enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                System.out.println(response.body());
            }

            @Override
            public void onFailure(Call<List<Object>> call, Throwable t) {}
        });
    }

    @Override
    public Location getLocation() {
        return this.location;
    }
    // ========= [end LocationInterface implementation] =========
}
