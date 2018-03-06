package mpc.utexas.edu.warble2.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by yosef on 12/21/2017.
 */

@Dao
public interface BridgeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addBridge(BridgeDb bridgeDb);

    @Update
    void updateBridge(BridgeDb bridgeDb);

    @Query("SELECT * FROM BridgeDb WHERE dbid=:dbid")
    BridgeDb getBridge(long dbid);

    @Query("SELECT * FROM BridgeDb WHERE UUID=:UUID")
    BridgeDb getBridgeByUUID(String UUID);

    @Query("SELECT * FROM BridgeDb")
    BridgeDb[] getAllBridges();

    @Query("SELECT * FROM BridgeDb WHERE category=:category")
    List<BridgeDb> getAllBridgesByCategory(String category);

    @Query("DELETE FROM BridgeDb WHERE dbid=:dbid")
    void delete(long dbid);

    @Query("DELETE FROM BridgeDb")
    void deleteAllBridges();
}
