package mpc.utexas.edu.warble2.users;

/**
 * Created by yosef on 11/7/2017.
 */

public abstract class User {
    public static String identifier = "UserDb";
    public static String TAG = "UserDb";
    protected String name;
    protected String id;
    protected long bridgeDbid;

    public String getName(){
        return this.name;
    }

    public String getId(){
        return this.id;
    }
}
