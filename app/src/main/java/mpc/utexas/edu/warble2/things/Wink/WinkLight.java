package mpc.utexas.edu.warble2.things.Wink;

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
import mpc.utexas.edu.warble2.services.Wink.PutThingRequest;
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
    public WinkService service;

    // ======== [start Constructor implementation] ========
    public WinkLight(String name, String id, Location location) {
        this.name = name;
        this.id = id;
        this.location = location;
        this.service = WinkUtil.getService();
    }

    public WinkLight(String name, String id, Location location, long dbid) {
        this.name = name;
        this.id = id;
        this.location = location;
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
        // TODO Filter WinkLights as thingCategory
        List<ThingDb> dblights =  appDatabase.thingDao().getAllThings();

        List<WinkLight> lights = new ArrayList<>();

//        for (ThingDb dblight: dblights) {
//            // TODO Is this necessary?
//            BridgeDb bridgeDb = appDatabase.bridgeDao().getBridge(dblight.bridgeDbid);
//            UserDb userDb = appDatabase.userDao().getUser(dblight.userDbid);
//            lights.add(new WinkLight(dblight.name, dblight.location, dblight.dbid));
//        }

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
        Log.d(TAG, "Adding WinkLight to Database");
//        AppDatabase appDatabase = AppDatabase.getDatabase(context);
//        BridgeDb bridgeDb = appDatabase.bridgeDao().getBridgeByUUID(this.parentBridge.getUUID());
//        UserDb userDb = appDatabase.userDao().getUserById(this.user.getId());
//        this.dbid = appDatabase.thingDao().addThing(new ThingDb(this.name, this.name, PhilipsLight.identifier, this.location, bridgeDb.dbid, userDb.dbid));
    }

    public void updateDb(Context context) {
        Log.d(TAG, "Updating PhilipsLight to Database");
//        AppDatabase appDatabase = AppDatabase.getDatabase(context);
//        BridgeDb bridgeDb = appDatabase.bridgeDao().getBridgeByUUID(this.parentBridge.getUUID());
//        UserDb userDb = appDatabase.userDao().getUserById(this.user.getId());
//        appDatabase.thingDao().updateThing(new ThingDb(this.name, this.name, PhilipsLight.identifier, this.location, bridgeDb.dbid, userDb.dbid));
    }

    public void deleteDb(Context context) {
        Log.d(TAG, "Deleting PhilipsLight to Database");
//        AppDatabase appDatabase = AppDatabase.getDatabase(context);
//        BridgeDb bridgeDb = appDatabase.bridgeDao().getBridgeByUUID(this.parentBridge.getUUID());
//        ThingDb philipsLight = appDatabase.thingDao().getThingByFeature(this.name, identifier, bridgeDb.dbid);
//        appDatabase.thingDao().deleteThing(philipsLight.dbid);
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
        System.out.println("seton " + this.id);
//        PutThingRequest lightState = new PutThingRequest("true", "0");
        HashMap<String, Object> lightState = new HashMap<>();
        HashMap<String, Object> desiredState = new HashMap<>();
        desiredState.put("powered", "true");
        desiredState.put("brightness", "0");
        lightState.put("desired_state", desiredState);

        WinkService service = WinkUtil.getService();

        service.putLight("bearer " + WinkUtil.accessToken, this.id, lightState).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println(response.code());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    @Override
    public void setOff() {
        System.out.println("setoff " + this.id);
//        PutThingRequest lightState = new PutThingRequest("false", "0");
        HashMap<String, Object> lightState = new HashMap<>();
        HashMap<String, Object> desiredState = new HashMap<>();
        desiredState.put("powered", "false");
        desiredState.put("brightness", "0");
        lightState.put("desired_state", desiredState);

        WinkService service = WinkUtil.getService();

        service.putLight("bearer " + WinkUtil.accessToken, this.id, lightState).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println(response.code());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
    }
    // ========= [end LightInterface implementation] =========


    // ======== [start LocationInterface implementation] ========
    @Override
    public void setLocation(Location location) {
//        this.location = location;
//
//        PutThingRequest locationState = new PutThingRequest(null, null, String.format("%s,%s,%s", WinkLight.identifier, location.getxCoordinate(), location.getyCoordinate()));
//
//        this.service.putLight(WinkUtil.accessToken, locationState).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                System.out.println(response.code());
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {}
//        });
    }

    @Override
    public Location getLocation() {
        return this.location;
    }
    // ========= [end LocationInterface implementation] =========
}
