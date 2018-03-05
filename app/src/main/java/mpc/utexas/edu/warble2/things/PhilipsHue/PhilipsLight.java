package mpc.utexas.edu.warble2.things.PhilipsHue;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.BridgeDb;
import mpc.utexas.edu.warble2.database.ThingDb;
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

    // ======== [start Constructor implementation] ========
    public PhilipsLight(String name, String id, Location location, PhilipsUser user, PhilipsBridge parentBridge) {
        this.name = name;
        this.id = id;
        this.location = location;
        this.parentBridge = parentBridge;
        this.service = parentBridge.getService();
    }

    public PhilipsLight(String name, String id, Location location, PhilipsUser user, PhilipsBridge parentBridge, long dbid) {
        this.name = name;
        this.id = id;
        this.location = location;
        this.parentBridge = parentBridge;
        this.service = parentBridge.getService();
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
        List<ThingDb> dblights =  appDatabase.thingDao().getAllThingsByCategory(identifier);

        List<PhilipsLight> lights = new ArrayList<>();

        for (ThingDb dblight: dblights) {
            lights.add(new PhilipsLight(dblight.name, dblight.id, dblight.location, null, PhilipsBridge.getBridgeById(context, dblight.bridgeDbid), dblight.dbid));
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
        Log.d(TAG, "Add PhilipsLight to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        BridgeDb bridgeDb = appDatabase.bridgeDao().getBridgeByUUID(this.parentBridge.getUUID());
        this.dbid = appDatabase.thingDao().addThing(new ThingDb(this.name, this.id, PhilipsLight.identifier, this.location, bridgeDb.dbid));
    }

    public void updateDb(Context context) {
        Log.d(TAG, "Update PhilipsLight to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        ThingDb existingThingDb = appDatabase.thingDao().getThingById(this.id);

        if (existingThingDb == null) {
            this.addDb(context);
        } else {
            ThingDb thingDb = new ThingDb(this.name, this.id, this.getClass().getSimpleName(), this.location, this.parentBridge.getDbid());
            thingDb.dbid = this.dbid;
            appDatabase.thingDao().updateThing(thingDb);
        }
    }

    public void deleteDb(Context context) {
        Log.d(TAG, "Delete PhilipsLight to Database");
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
        Log.d(TAG, "Set Off (" + this.toString() + ")");
        HashMap<String, Object> lightState = new HashMap<>();
        lightState.put("on", true);
        lightState.put("bri", 50);

        service.putLightState(this.parentBridge.getUser().getId(), this.name, lightState).enqueue(new Callback<List<Object>>() {
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
        Log.d(TAG, "Set Off ("+ this.toString() + ")");
        HashMap<String, Object> lightState = new HashMap<>();
        lightState.put("on", false);
        lightState.put("transitiontime", 0);

        service.putLightState(this.parentBridge.getUser().getId(), this.name, lightState).enqueue(new Callback<List<Object>>() {
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
        Log.d(TAG, "Set Location (" + this.toString() + "): " + location.getxCoordinate() + ", "  + location.getyCoordinate());
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
        if (this.location == null) {
            return new Location(0, 0);
        } else {
            return this.location;
        }
    }
    // ========= [end LocationInterface implementation] =========

    // ======== [start Others methods] ========
    @Override
    public String toString() {
        String string = "";
        string += identifier + " - ";
        string += String.format("Name: %s, ", this.name);
        string += String.format("Id: %s, ", this.id);
        string += String.format("Parent Bridge: %s, ", this.parentBridge.getName());
        string += String.format("Dbid: %s", this.dbid);
        return string;
    }
    // ========= [end Others methods] =========
}
