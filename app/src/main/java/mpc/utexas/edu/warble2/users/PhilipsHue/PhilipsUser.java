package mpc.utexas.edu.warble2.users.PhilipsHue;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.UserDb;
import mpc.utexas.edu.warble2.users.User;

/**
 * Created by yosef on 11/7/2017.
 */

public class PhilipsUser extends User {
    public static List<PhilipsUser> getAllDb(Context context) {
        Log.d(TAG, "Getting All PhilipsUsers from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        // TODO Filter for only PhilipsUsers
        List<UserDb> dbusers = appDatabase.userDao().getAllUsers();

        List<PhilipsUser> users = new ArrayList<>();

        for (UserDb dbuser: dbusers) {
            users.add(new PhilipsUser(dbuser.name, dbuser.id, dbuser.category, dbuser.bridgeDbid));
        }

        return users;
    }

    public static void deleteAllDb(Context context) {
        Log.d(TAG, "Delete All PhilipsUsers from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        // TODO Delete only PhilipsUsers
        appDatabase.userDao().deleteAllUsers();
    }

    public PhilipsUser(String name, String id, String category, long bridgeDbid) {
        this.name = name;
        this.id = id;
        this.category = category;
        this.bridgeDbid = bridgeDbid;
    }

    public void addDb(Context context) {
        Log.d(TAG, "Add PhilipsUser to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        appDatabase.userDao().addUser(new UserDb(this.name, this.id, this.category, this.bridgeDbid));
    }

    public void updateDb(Context context) {
        Log.d(TAG, "Update PhilipsUser to Database - Not Implemented");
    }

    public void deleteDb(Context context) {
        Log.d(TAG, "Delete PhilipsUser from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        UserDb deleteUserDb = appDatabase.userDao().getUserById(this.id);
        appDatabase.userDao().delete(deleteUserDb.dbid);
    }
}
