package mpc.utexas.edu.warble2;

import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.things.Bridge;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsBridge;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsLight;
import mpc.utexas.edu.warble2.things.Thing;
import mpc.utexas.edu.warble2.things.Wink.WinkBridge;
import mpc.utexas.edu.warble2.things.Wink.WinkLight;

/**
 * Created by yosef on 3/2/2018.
 */

public class settings {
    public static Class[] bridgeClasses;
    public static Class[] thingClasses;

    static {
        bridgeClasses = new Class[]{
                Bridge.class,
                PhilipsBridge.class,
                WinkBridge.class
        };
    }

    static {
        thingClasses = new Class[]{
                Thing.class,
                PhilipsLight.class,
                WinkLight.class
        };
    }
}
