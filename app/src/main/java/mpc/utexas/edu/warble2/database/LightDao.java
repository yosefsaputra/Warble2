package mpc.utexas.edu.warble2.database;

import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

/**
 * Created by yosef on 1/21/2018.
 */

public interface LightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addLight(Light light);

    @Query("SELECT * FROM Light")
    void getAllLights();

    @Query("SELECT * FROM Light WHERE bridgeId=:bridgeId")
    void getAllLightsForBridge(long bridgeId);

    @Query("DELETE FROM Light WHERE id=:id")
    void deleteLight(long id);

    @Query("DELETE FROM Light")
    void deleteAllLights();
}
