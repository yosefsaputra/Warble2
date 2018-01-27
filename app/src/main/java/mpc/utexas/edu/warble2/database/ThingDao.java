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
public interface ThingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addThing(ThingDb thing);

    @Query("SELECT * FROM ThingDb")
    List<ThingDb> getAllThings();

    @Query("SELECT * FROM ThingDb WHERE bridgeDbid=:bridgeDbid")
    List<ThingDb> getAllThingsForBridge(long bridgeDbid);

    @Query("SELECT * FROM ThingDb WHERE name=:name AND category=:category AND bridgeDbid=:bridgeDbid")
    ThingDb getThingByFeature(String name, String category, long bridgeDbid);

    @Query("SELECT * FROM ThingDb WHERE dbid=:dbid")
    List<ThingDb> getThing(long dbid);

    @Query("DELETE FROM ThingDb WHERE dbid=:dbid")
    void deleteThing(long dbid);

    @Query("DELETE FROM ThingDb")
    void deleteAllThings();
}
