package mpc.utexas.edu.warble2.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by yosef on 1/21/2018.
 */

@Dao
public interface LightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addLight(Light light);

    @Query("SELECT * FROM Light")
    List<Light> getAllLights();

    @Query("SELECT * FROM Light WHERE bridgeId=:bridgeId")
    List<Light> getAllLightsForBridge(long bridgeId);

    @Query("DELETE FROM Light WHERE id=:id")
    void deleteLight(long id);

    @Query("DELETE FROM Light")
    void deleteAllLights();
}
