package mpc.utexas.edu.warble2.things;

import android.content.Context;

import java.util.List;

/**
 * Created by yosef on 11/12/2017.
 */

public interface BridgeInterface extends ThingInterface {
    List<? extends Thing> discoverThings(Context context);
    List<? extends Light> discoverLights(Context context);
}
