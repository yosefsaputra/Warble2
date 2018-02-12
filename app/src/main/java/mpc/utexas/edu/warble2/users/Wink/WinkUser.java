package mpc.utexas.edu.warble2.users.Wink;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.UserDb;
import mpc.utexas.edu.warble2.users.User;

/**
 * Created by yosef on 2/11/2018.
 */

public class WinkUser extends User{
    public static String identifier = "WinkUser";
    public static String TAG = "WinkUser";

    public static List<WinkUser> getAllDb(Context context) {
        Log.d(TAG, "Getting All WinkUser from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        List<UserDb> dbusers = appDatabase.userDao().getAllUsersByCategory(WinkUser.identifier);

        List<WinkUser> users = new ArrayList<>();

        for (UserDb dbuser: dbusers) {
            users.add(new WinkUser(dbuser.name, dbuser.id, dbuser.category, dbuser.bridgeDbid));
        }

        return users;
    }

    public static void deleteAllDb(Context context) {
        Log.d(TAG, "Delete All WinkUser from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        appDatabase.userDao().deleteAllUsersByCategory(WinkUser.identifier);
    }

    public WinkUser(String name, String id, String category, long bridgeDbid) {
        this.name = name;
        this.id = id;
        this.category = category;
        this.bridgeDbid = bridgeDbid;
    }
}
