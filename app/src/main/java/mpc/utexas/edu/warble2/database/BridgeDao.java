package mpc.utexas.edu.warble2.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

/**
 * Created by yosef on 12/21/2017.
 */

@Dao
public interface BridgeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addBridge(Bridge bridge);

    @Update
    void updateBridge(Bridge bridge);

    @Query("SELECT * FROM bridge WHERE id=:id")
    Bridge getBridge(long id);

    @Query("SELECT * FROM bridge WHERE UUID=:UUID")
    Bridge getBridgeByUUID(String UUID);

    @Query("SELECT * FROM bridge")
    Bridge[] getAllBridges();

    @Query("DELETE FROM bridge WHERE id=:id")
    void delete(long id);

    @Query("DELETE FROM bridge")
    void deleteAllBridges();
}
