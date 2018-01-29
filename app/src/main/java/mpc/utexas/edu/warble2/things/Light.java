package mpc.utexas.edu.warble2.things;

import mpc.utexas.edu.warble2.features.Location;
import mpc.utexas.edu.warble2.features.LocationInterface;

/**
 * Created by yosef on 11/7/2017.
 */

public abstract class Light extends Thing implements LightInterface, LocationInterface {
    public static String identifier = "Light";

    protected Location location;
}
