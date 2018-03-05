package mpc.utexas.edu.warble2.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by yosef on 1/21/2018.
 */

@Dao
public interface ThingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addThing(ThingDb thingDb);

    @Update
    void updateThing(ThingDb thingDb);

    @Query("SELECT * FROM ThingDb")
    List<ThingDb> getAllThings();

    @Query("SELECT * FROM ThingDb WHERE bridgeDbid=:bridgeDbid")
    List<ThingDb> getAllThingsForBridge(long bridgeDbid);

    @Query("SELECT * FROM ThingDb WHERE category=:category")
    List<ThingDb> getAllThingsByCategory(String category);

    @Query("SELECT * FROM ThingDb WHERE id=:id")
    ThingDb getThingById(String id);

    @Query("SELECT * FROM ThingDb WHERE name=:name AND category=:category AND bridgeDbid=:bridgeDbid")
    ThingDb getThingByFeature(String name, String category, long bridgeDbid);

    @Query("SELECT * FROM ThingDb WHERE dbid=:dbid")
    ThingDb getThing(long dbid);

    @Query("DELETE FROM ThingDb WHERE dbid=:dbid")
    void deleteThing(long dbid);

    @Query("DELETE FROM ThingDb WHERE category=:category")
    void deleteAllThingsByCategory(String category);

    @Query("DELETE FROM ThingDb")
    void deleteAllThings();
}
