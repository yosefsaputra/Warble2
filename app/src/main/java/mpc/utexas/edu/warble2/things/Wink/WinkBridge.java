package mpc.utexas.edu.warble2.things.Wink;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.BridgeDb;
import mpc.utexas.edu.warble2.database.LocationConverter;
import mpc.utexas.edu.warble2.features.Location;
import mpc.utexas.edu.warble2.services.Oauth2.Oauth2Service;
import mpc.utexas.edu.warble2.services.Wink.GetThingsResponse;
import mpc.utexas.edu.warble2.services.Wink.WinkService;
import mpc.utexas.edu.warble2.things.Bridge;
import mpc.utexas.edu.warble2.things.BridgeInterface;
import mpc.utexas.edu.warble2.things.Light;
import mpc.utexas.edu.warble2.things.Thing;
import mpc.utexas.edu.warble2.users.PhilipsHue.PhilipsUser;
import mpc.utexas.edu.warble2.users.User;
import mpc.utexas.edu.warble2.users.Wink.WinkUser;
import mpc.utexas.edu.warble2.utils.Oauth2Util;
import mpc.utexas.edu.warble2.utils.WinkUtil;

/**
 * Created by yosef on 2/11/2018.
 */

public class WinkBridge extends Bridge implements BridgeInterface {
    public static String identifier = "WinkBridge";
    public static String TAG = "WinkBridge";
    public static String bridgeToUser = "manytoone";
    public WinkService service;
    public Oauth2Service oauth2Service;

    // ======= [start Constructor methods] =======
    public WinkBridge(String name, String id, User user) {
        super(name, id, "https://api.wink.com", identifier, user);
        this.service = WinkUtil.getService();
        this.oauth2Service = Oauth2Util.getOauth2Service(this.baseUrl);
    }

    public WinkBridge(String name, String id, User user, long dbId) {
        super(name, id, "https://api.wink.com", identifier, user, dbId);
        this.service = WinkUtil.getService();
        this.oauth2Service = Oauth2Util.getOauth2Service(this.baseUrl);
    }
    // ======== [end Constructor methods] ========

    // ======== [start Static methods] ========
    public static List<Bridge> discover(Context context) {
        Log.d(TAG, "Discover Wink Bridges");
        List<Bridge> bridges = new ArrayList<>();

        List<Bridge> existingBridges = WinkBridge.getAllDb(context);

        for (Thing thing: WinkUtil.discoverBridges()) {
            if (thing instanceof WinkBridge) {
                Bridge bridge = (WinkBridge) thing;
                for (Bridge i: existingBridges) {
                    if (i.getUUID().equals(bridge.getUUID())) {
                        bridge = i;
                        break;
                    }
                }
                bridges.add(bridge);
            }
        }

        return bridges;
    }

    public static List<Bridge> getAllDb(Context context) {
        Log.d(TAG, "Getting All WinkBridges from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        List<BridgeDb> dbBridgeDbs = appDatabase.bridgeDao().getAllBridgesByCategory(identifier);

        List<Bridge> winkBridges = new ArrayList<>();
        for (BridgeDb dbBridgeDb : dbBridgeDbs) {
            winkBridges.add(new WinkBridge(dbBridgeDb.name, dbBridgeDb.UUID, WinkUser.getUserByDbid(context, dbBridgeDb.userDbid)));
        }

        return winkBridges;
    }

    public static WinkBridge getBridgeById(Context context, long dbid) {
        Log.d(TAG, "Getting WinkBridge by Id from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        BridgeDb dbBridgeDb = appDatabase.bridgeDao().getBridge(dbid);

        WinkBridge bridge = null;
        if (dbBridgeDb.category.equals(WinkBridge.identifier)) {
            bridge = new WinkBridge(dbBridgeDb.name, dbBridgeDb.UUID, PhilipsUser.getUserByDbid(context, dbBridgeDb.userDbid), dbBridgeDb.dbid);
        }

        return bridge;
    }
    // ========= [end Static methods] =========

    // ======== [start BridgeInterface methods] ========
    @Override
    public List<Thing> discoverThings(Context context) {
        Log.d(TAG, "Discover Wink Things");

        List<Thing> things = new ArrayList<>();
        things.addAll(discoverLights(context));

        return things;
    }

    @Override
    public List<Light> discoverLights(Context context) {
        Log.d(TAG, "Discover Wink Lights");
        List<Light> lights = new ArrayList<>();

        WinkService service = WinkUtil.getService();

        try {
            // TODO handle if HTTP response is error
            GetThingsResponse getThingsResponse = service.getThings("bearer " + WinkUtil.accessToken).execute().body();

            for (GetThingsResponse.Thing thing : getThingsResponse.getData()) {
                if (thing.getName().contains("Light") | thing.getName().contains("light")) {
                    Location location = LocationConverter.toLocation(thing.getLocation());
                    if (location == null) {
                        location = new Location(0, 0);
                    }
                    WinkLight thingObj = new WinkLight(thing.getName(), thing.getThingId(), location, this);
                    Log.d(TAG, String.format("WinkLight %s %s", thing.getName(), thing.getThingId()));
                    lights.add(thingObj);
                }
            }
        } catch (IOException | NullPointerException e) {
            Log.e(TAG, "exception", e);
        }

        return lights;
    }
    // ========= [end BridgeInterface methods] =========

}
