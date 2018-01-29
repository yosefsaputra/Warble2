package mpc.utexas.edu.warble2.database;

import android.content.Context;

/**
 * Created by yosef on 1/27/2018.
 */

public interface DatabaseInterface {
    void addDb(Context context);
    void updateDb(Context context);
    void deleteDb(Context context);
}
