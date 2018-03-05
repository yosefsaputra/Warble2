package mpc.utexas.edu.warble2.utils;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.services.Wink.GetThingsResponse;
import mpc.utexas.edu.warble2.services.Wink.WinkService;
import mpc.utexas.edu.warble2.things.Thing;
import mpc.utexas.edu.warble2.things.Wink.WinkBridge;

/**
 * Created by yosef on 2/9/2018.
 */

public class WinkUtil {
    private static String TAG = "WinkUtil";

    public static String accessToken = "gH0b8JfsOrmO8H0ix43CQ1LrSdQ5dkMR";

    public static WinkService getService() {
        return RetrofitClient.getRetrofitClient("https://api.wink.com").create(WinkService.class);
    }

    public static List<Thing> discoverBridges() {
        WinkService service = WinkUtil.getService();

        List<Thing> things = new ArrayList<>();

        try {
            // TODO handle if HTTP response is error
            GetThingsResponse getThingsResponse = service.getThings("bearer " + WinkUtil.accessToken).execute().body();

            for (GetThingsResponse.Thing thing : getThingsResponse.getData()) {
                if (thing.getName().contains("Hub") | thing.getName().contains("hub")) {
                    Thing thingObj = new WinkBridge(thing.getName(), thing.getHubId(), null);
                    things.add(thingObj);
                }
            }
        } catch (IOException | NullPointerException e) {
            Log.e(TAG, "exception", e);
        }

        return things;
    }
}
