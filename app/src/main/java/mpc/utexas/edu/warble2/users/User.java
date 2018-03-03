package mpc.utexas.edu.warble2.users;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.DatabaseInterface;
import mpc.utexas.edu.warble2.database.ThingDb;
import mpc.utexas.edu.warble2.database.UserDb;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsLight;
import mpc.utexas.edu.warble2.things.Thing;
import mpc.utexas.edu.warble2.users.PhilipsHue.PhilipsUser;

/**
 * Created by yosef on 11/7/2017.
 */

public class User implements DatabaseInterface {
    public static String identifier = "User";
    public static String TAG = "User";
    protected String name;
    protected String id;
    protected String secretId;
    protected String category;
    protected String accessToken;
    protected String refreshToken;
    protected long dbid;

    public String getName(){
        return this.name;
    }

    public String getId(){
        return this.id;
    }

    public String getSecretId() {
        return this.secretId;
    }

    public String getCategory(){
        return this.category;
    }

    public String getAccessToken() {
        return this.secretId;
    }

    public String getRefreshToken() {
        return this.secretId;
    }

    public long getDbid() {
        return this.dbid;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User(String name, String id, String secretId, String accessToken, String refreshToken) {
        this.name = name;
        this.id = id;
        this.secretId = secretId;
        this.category = identifier;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public User(String name, String id, String secretId, String accessToken, String refreshToken, long dbid) {
        this.name = name;
        this.id = id;
        this.secretId = secretId;
        this.category = identifier;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;

        this.dbid = dbid;
    }

    // ======== [start Static methods] ========
    public static User getUserByDbid(Context context, long dbid) {
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        UserDb userDb = appDatabase.userDao().getUser(dbid);

        return new User(userDb.name, userDb.id, userDb.secretId, userDb.accessToken, userDb.refreshToken);
    }

    public static List<? extends User> getUsersByCategory(Context context, String category) {
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        List<User> users = new ArrayList<>();

        for (UserDb userDb: appDatabase.userDao().getAllUsersByCategory(category)) {
            users.add(new User(userDb.name, userDb.id, userDb.secretId, userDb.accessToken, userDb.refreshToken));
        }

        return users;
    }

    public static List<? extends User> getAllDb(Context context) {
        Log.d(TAG, "Getting All Users from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        List<UserDb> dbusers = appDatabase.userDao().getAllUsers();

        List<User> users = new ArrayList<>();

        for (UserDb dbuser: dbusers) {
            users.add(new User(dbuser.name, dbuser.id, dbuser.secretId, dbuser.accessToken, dbuser.refreshToken));
        }

        return users;
    }

    public static void deleteAllDb(Context context) {
        Log.d(TAG, "Delete All Users from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        appDatabase.userDao().deleteAllUsers();
    }
    // ========= [end Static methods] =========

    // ======== [start DatabaseInterface implementation] ========
    public void addDb(Context context) {
        Log.d(TAG, "Add User to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        this.dbid = appDatabase.userDao().addUser(new UserDb(this.name, this.id, "", this.category, "", ""));
    }

    public void updateDb(Context context) {
        Log.d(TAG, "Update User to Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        appDatabase.userDao().updateUser(new UserDb(this.name, this.id, "", this.category, "", ""));
    }

    public void deleteDb(Context context) {
        Log.d(TAG, "Delete User from Database");
        AppDatabase appDatabase = AppDatabase.getDatabase(context);
        UserDb deleteUserDb = appDatabase.userDao().getUserById(this.id);
        appDatabase.userDao().delete(deleteUserDb.dbid);
    }
    // ========= [end DatabaseInterface implementation] =========

    // ========= [start Other methods] =========
    public String toString() {
        String string = "";
        string += String.format("Name: %s, ", this.name);
        string += String.format("ID: %s\n", this.id);
        return string;
    }
    // ========== [end Other methods] ==========

}
