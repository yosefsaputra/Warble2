package mpc.utexas.edu.warble2.things;

import mpc.utexas.edu.warble2.database.DatabaseInterface;
import mpc.utexas.edu.warble2.services.Service;

/**
 * Created by yosef on 11/7/2017.
 */

public abstract class Thing implements DatabaseInterface {
    public static String identifier = "Thing";
    protected String name;
    protected String id;

    // ======== [start Getter Setter implementation] ========
    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }
    // ========= [end Getter Setter implementation] =========
}
