package mpc.utexas.edu.warble2.users.PhilipsHue;

import mpc.utexas.edu.warble2.users.User;

/**
 * Created by yosef on 11/7/2017.
 */

public class PhilipsUser extends User {
    private String userid;

    public static PhilipsUser getUserFromUserDb(mpc.utexas.edu.warble2.database.User dbUser) {
        PhilipsUser user = new PhilipsUser(dbUser.username, dbUser.userId);
        return user;
    }

    public PhilipsUser(String username, String userid) {
        this.username = username;
        this.userid = userid;
    }

    public String getUserid(){
        return this.userid;
    }
}
