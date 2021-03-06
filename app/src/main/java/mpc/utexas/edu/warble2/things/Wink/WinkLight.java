package mpc.utexas.edu.warble2.things.Wink;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.BridgeDb;
import mpc.utexas.edu.warble2.database.ThingDb;
import mpc.utexas.edu.warble2.features.Location;
import mpc.utexas.edu.warble2.services.Wink.WinkService;
import mpc.utexas.edu.warble2.things.Light;
import mpc.utexas.edu.warble2.utils.WinkUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yosef on 2/11/2018.
 */

public class WinkLight extends Light {
    public static String identifier = "WinkLight";
    public static String TAG = "WinkLight";
    protected WinkService service;
    protected WinkBridge parentBridge;

    // ======== [start Constructor implementation] ========
    public WinkLight(String name, String id, Location location, WinkBridge parentBridge) {
        this.name = name;
        this.id = id;
        this.location = location;
        this.parentBridge = parentBridge;
        this.service = WinkUtil.getService();
    }

    public WinkLight(String name, String id, Location location, WinkBridge parentBridge, long dbid) {
        this.name = name;
        this.id = id;
        this.location = location;
        this.parentBridge = parentBridge;
        this.service = WinkUtil.getService();
        this.dbid = dbid;
    }
    // ========= [end Constructor implementation] =========


    // ======== [start Getter Setter implementation] ========
    // ========= [end Getter Setter implementation] =========


    // ======== [start Static methods] ========
    public static List<WinkLight> getAllDb(Context context) {
        Log.d(TAG, "Getting All WinkLights from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        List<ThingDb> dblights =  appDatabase.thingDao().getAllThingsByCategory(identifier);

        List<WinkLight> lights = new ArrayList<>();

        for (ThingDb dblight: dblights) {
            lights.add(new WinkLight(dblight.name, dblight.id, dblight.location, WinkBridge.getBridgeById(context, dblight.bridgeDbid), dblight.dbid));
        }
        return lights;
    }

    public static void deleteAllDb(Context context) {
        Log.d(TAG, "Deleting All WinkLights from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        // TODO Delete only WinkLights
        appDatabase.thingDao().deleteAllThings();
    }
    // ========= [end Static methods] =========


    // ======== [start DatabaseInterface implementation] ========
    public void addDb(Context context) {
        Log.d(TAG, "Add WinkLight to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        BridgeDb bridgeDb = appDatabase.bridgeDao().getBridgeByUUID(this.parentBridge.getUUID());
        this.dbid = appDatabase.thingDao().addThing(new ThingDb(this.name, this.id, WinkLight.identifier, this.location, bridgeDb.dbid));
    }

    public void updateDb(Context context) {
        Log.d(TAG, "Update WinkLight to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        ThingDb existingThingDb = appDatabase.thingDao().getThingById(this.id);

        if (existingThingDb == null) {
            this.addDb(context);
        } else {
            ThingDb thingDb = new ThingDb(this.name, this.id, WinkLight.identifier, this.location, this.parentBridge.getDbid());
            thingDb.dbid = this.dbid;
            appDatabase.thingDao().updateThing(thingDb);
        }
    }

    public void deleteDb(Context context) {
        Log.d(TAG, "Delete WinkLight to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        if (this.dbid != 0) {
            appDatabase.thingDao().deleteThing(this.dbid);
        }
    }
    // ========= [end DatabaseInterface implementation] =========


    // ======== [start ThingInterface implementation] ========
    @Override
    public String getCapability() {
        return "Return capability of WinkLight";
    }
    // ========= [end ThingInterface implementation] =========


    // ======== [start LightInterface implementation] ========
    @Override
    public void setOn() {
        Log.i(TAG, "Set On (" + this.toString() + ")");
        HashMap<String, Object> lightState = new HashMap<>();
        HashMap<String, Object> desiredState = new HashMap<>();
        desiredState.put("powered", "true");
        desiredState.put("brightness", "0");
        lightState.put("desired_state", desiredState);

        WinkService service = WinkUtil.getService();

        service.putLight("bearer " + WinkUtil.accessToken, this.id, lightState).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {}

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    @Override
    public void setOff() {
        Log.i(TAG, "Set Off (" + this.toString() + ")");
        HashMap<String, Object> lightState = new HashMap<>();
        HashMap<String, Object> desiredState = new HashMap<>();
        desiredState.put("powered", "false");
        desiredState.put("brightness", "0");
        lightState.put("desired_state", desiredState);

        WinkService service = WinkUtil.getService();

        service.putLight("bearer " + WinkUtil.accessToken, this.id, lightState).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {}

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
    }
    // ========= [end LightInterface implementation] =========


    // ======== [start LocationInterface implementation] ========
    @Override
    public void setLocation(Location location) {
        Log.i(TAG, "Set Location (" + this.toString() + "): " + location.getxCoordinate() + ", "  + location.getyCoordinate());
        HashMap<String, Object> lightState = new HashMap<>();
        lightState.put("location", String.format("%s,%s", location.getxCoordinate(), location.getyCoordinate()));

        WinkService service = WinkUtil.getService();

        service.putLight("bearer " + WinkUtil.accessToken, this.id, lightState).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {}

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
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
