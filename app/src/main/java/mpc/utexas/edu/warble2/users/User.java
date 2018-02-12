package mpc.utexas.edu.warble2.users;

import android.content.Context;

import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.ThingDb;
import mpc.utexas.edu.warble2.database.UserDb;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsLight;
import mpc.utexas.edu.warble2.things.Thing;
import mpc.utexas.edu.warble2.users.PhilipsHue.PhilipsUser;

/**
 * Created by yosef on 11/7/2017.
 */

public abstract class User {
    public static String identifier = "User";
    public static String TAG = "User";
    protected String name;
    protected String id;
    protected String category;
    protected long bridgeDbid;

    public String getName(){
        return this.name;
    }

    public String getId(){
        return this.id;
    }

    public String getCategory(){
        return this.category;
    }

    // ======== [start Static methods] ========
    public static User getUserByDbid(Context context, long dbid) {
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        UserDb userDb = appDatabase.userDao().getUser(dbid);

        if (userDb.category.equals(PhilipsUser.identifier)) {
            return new PhilipsUser(userDb.name, userDb.id, userDb.category, userDb.bridgeDbid);
        }
        return null;
    }
    // ========= [end Static methods] =========
}
