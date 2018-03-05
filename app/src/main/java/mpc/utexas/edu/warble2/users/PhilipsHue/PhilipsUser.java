package mpc.utexas.edu.warble2.users.PhilipsHue;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.UserDb;
import mpc.utexas.edu.warble2.users.User;

/**
 * Created by yosef on 11/7/2017.
 */

public class PhilipsUser extends User {
    public static String identifier = "PhilipsUser";
    public static String TAG = "PhilipsUser";

//    public static List<PhilipsUser> getAllDb(Context context) {
//        Log.d(TAG, "Getting All PhilipsUsers from Database");
//        AppDatabase appDatabase = AppDatabase.getDatabase(context);
//        List<UserDb> dbusers = appDatabase.userDao().getAllUsersByCategory(PhilipsUser.identifier);
//
//        List<PhilipsUser> users = new ArrayList<>();
//
//        for (UserDb dbuser: dbusers) {
//            users.add(new PhilipsUser(dbuser.name, dbuser.id, dbuser.category));
//        }
//
//        return users;
//    }
//
//    public static void deleteAllDb(Context context) {
//        Log.d(TAG, "Delete All PhilipsUsers from Database");
//        AppDatabase appDatabase = AppDatabase.getDatabase(context);
//        appDatabase.userDao().deleteAllUsersByCategory(PhilipsUser.identifier);
//    }

    public PhilipsUser(String name, String id, String secretId, String accessToken, String refreshToken) {
        super(name, id, secretId, accessToken, refreshToken);
        this.category = identifier;
    }

    // ======== [start Static methods] ========
    public static List<PhilipsUser> getAllDb(Context context) {
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        List<PhilipsUser> users = new ArrayList<>();

        for (UserDb userDb: appDatabase.userDao().getAllUsersByCategory(identifier)) {
            users.add(new PhilipsUser(userDb.name, userDb.id, userDb.secretId, userDb.accessToken, userDb.refreshToken));
        }

        return users;
    }

    public static PhilipsUser getUserByDbid(Context context, long dbid) {
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        UserDb userDb = appDatabase.userDao().getUser(dbid);

        if (userDb == null) {
            return null;
        }

        return new PhilipsUser(userDb.name, userDb.id, userDb.secretId, userDb.accessToken, userDb.refreshToken);
    }
    // ========= [end Static methods] =========


//    public void addDb(Context context) {
//        Log.d(TAG, "Add PhilipsUser to Database");
//        AppDatabase appDatabase = AppDatabase.getDatabase(context);
//        appDatabase.userDao().addUser(new UserDb(this.name, this.id, "", this.category, "", ""));
//    }
//
//    public void updateDb(Context context) {
//        Log.d(TAG, "Update PhilipsUser to Database - Not Implemented");
//    }
//
//    public void deleteDb(Context context) {
//        Log.d(TAG, "Delete PhilipsUser from Database");
//        AppDatabase appDatabase = AppDatabase.getDatabase(context);
//        UserDb deleteUserDb = appDatabase.userDao().getUserById(this.id);
//        appDatabase.userDao().delete(deleteUserDb.dbid);
//    }
}
