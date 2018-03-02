package mpc.utexas.edu.warble2.utils;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.features.Location;
import mpc.utexas.edu.warble2.services.Wink.GetThingsResponse;
import mpc.utexas.edu.warble2.services.Wink.WinkService;
import mpc.utexas.edu.warble2.things.Thing;
import mpc.utexas.edu.warble2.things.Wink.WinkBridge;
import mpc.utexas.edu.warble2.things.Wink.WinkLight;

/**
 * Created by yosef on 2/9/2018.
 */

public class WinkUtil {
    private static String TAG = "WinkUtil";

    public static String accessToken = "gH0b8JfsOrmO8H0ix43CQ1LrSdQ5dkMR";

    public static WinkService getService() {
        return RetrofitClient.getRetrofitClient("https://api.wink.com").create(WinkService.class);
    }

    public static List<Thing> discoverThings() {
        WinkService service = WinkUtil.getService();

        List<Thing> things = new ArrayList<>();

        try {
            // TODO handle if HTTP response is error
            GetThingsResponse getThingsResponse = service.getThings("bearer " + WinkUtil.accessToken).execute().body();

            for (GetThingsResponse.Thing thing : getThingsResponse.getData()) {
                if (thing.getName().contains("Light") | thing.getName().contains("light")) {
                    Location location;
                    if (thing.getLocation() == "1451815") {
                        location = new Location(8, 8);
                    } else {
                        location = new Location(0, 0);
                    }
                    Thing thingObj = new WinkLight(thing.getName(), thing.getThingId(), location);
                    Log.d(TAG, String.format("Light %s %s %s", thing.getName(), thing.getThingId(), thing.getHubId()));
                    things.add(thingObj);
                } else if (thing.getName().contains("Hub") | thing.getName().contains("hub")) {
                    Thing thingObj = new WinkBridge(thing.getName(), thing.getHubId());
                    Log.d(TAG, String.format("Hub %s %s", thing.getName(), thing.getHubId()));
                    things.add(thingObj);
                }
            }
        } catch (IOException | NullPointerException e) {
            Log.e(TAG, "exception", e);
        }

        return things;
    }
}
