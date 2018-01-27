package mpc.utexas.edu.warble2.things;

import android.content.Context;

/**
 * Created by yosef on 11/7/2017.
 */

public abstract class Light extends Thing implements LightInterface {
    public static String identifier = "Light";

    public abstract void addDb(Context context);
    public abstract void updateDb(Context context);
    public abstract void deleteDb(Context context);
}
