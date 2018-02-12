package mpc.utexas.edu.warble2.things.Wink;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.services.Oauth2.Oauth2Service;
import mpc.utexas.edu.warble2.services.Wink.WinkService;
import mpc.utexas.edu.warble2.things.Bridge;
import mpc.utexas.edu.warble2.things.BridgeInterface;
import mpc.utexas.edu.warble2.things.Light;
import mpc.utexas.edu.warble2.things.Thing;
import mpc.utexas.edu.warble2.utils.Oauth2Util;
import mpc.utexas.edu.warble2.utils.WinkUtil;

/**
 * Created by yosef on 2/11/2018.
 */

public class WinkBridge extends Bridge implements BridgeInterface {
    public static String identifier = "WinkBridge";
    public static String TAG = "WinkBridge";
    public WinkService service;
    public Oauth2Service oauth2Service;

    // ======= [start Constructor methods] =======
    public WinkBridge(String name, String id) {
        super(name, id, "https://api.wink.com");
        this.service = WinkUtil.getService();
        this.oauth2Service = Oauth2Util.getOauth2Service(this.baseUrl);
    }

    public WinkBridge(String name, String id, long dbId) {
        super(name, id, "https://api.wink.com", dbId);
        this.service = WinkUtil.getService();
        this.oauth2Service = Oauth2Util.getOauth2Service(this.baseUrl);
    }
    // ======== [end Constructor methods] ========

    // ======== [start Static methods] ========
    public static List<Bridge> discover() {
        Log.d(TAG, "Discover Wink Bridges");
        List<Bridge> bridges = new ArrayList<>();

        for (Thing thing: WinkUtil.discoverThings()) {
            if (thing instanceof WinkBridge) {
                bridges.add((WinkBridge) thing);
            }
        }

        return bridges;
    }
    // ========= [end Static methods] =========

    // ======== [start BridgeInterface methods] ========
    @Override
    public List<Thing> discoverThings(Context context) {
        Log.d(TAG, "Discover Wink Things");
        return new ArrayList<>();
    }

    @Override
    public List<Light> discoverLights(Context context) {
        Log.d(TAG, "Discover Wink Lights");
        List<Light> lights = new ArrayList<>();

        for (Thing thing: WinkUtil.discoverThings()) {
            if (thing instanceof WinkLight) {
                lights.add((WinkLight) thing);
            }
        }

        return lights;
    }
    // ========= [end BridgeInterface methods] =========

}
