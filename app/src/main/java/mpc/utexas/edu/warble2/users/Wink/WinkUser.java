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

//    public static List<WinkUser> getAllDb(Context context) {
//        Log.d(TAG, "Getting All WinkUser from Database");
//        AppDatabase appDatabase = AppDatabase.getDatabase(context);
//        List<UserDb> dbusers = appDatabase.userDao().getAllUsersByCategory(WinkUser.identifier);
//
//        List<WinkUser> users = new ArrayList<>();
//
//        for (UserDb dbuser: dbusers) {
//            users.add(new WinkUser(dbuser.name, dbuser.id, dbuser.category));
//        }
//
//        return users;
//    }
//
//    public static void deleteAllDb(Context context) {
//        Log.d(TAG, "Delete All WinkUser from Database");
//        AppDatabase appDatabase = AppDatabase.getDatabase(context);
//        appDatabase.userDao().deleteAllUsersByCategory(WinkUser.identifier);
//    }
//
//    public WinkUser(String name, String id, String category) {
//        this.name = name;
//        this.id = id;
//        this.category = category;
//    }

    public WinkUser(String name, String id, String secretId, String accessToken, String refreshToken) {
        super(name, id, secretId, accessToken, refreshToken);
        this.category = identifier;
    }

    // ======== [start Static methods] ========
    public static List<User> getAllDb(Context context) {
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        List<User> users = new ArrayList<>();

        for (UserDb userDb: appDatabase.userDao().getAllUsersByCategory(identifier)) {
            users.add(new WinkUser(userDb.name, userDb.id, userDb.secretId, userDb.accessToken, userDb.refreshToken));
        }

        return users;
    }

    public static WinkUser getUserByDbid(Context context, long dbid) {
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        UserDb userDb = appDatabase.userDao().getUser(dbid);

        if (userDb == null) {
            return null;
        }

        return new WinkUser(userDb.name, userDb.id, userDb.secretId, userDb.accessToken, userDb.refreshToken);
    }
    // ========= [end Static methods] =========
}
