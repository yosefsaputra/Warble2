package mpc.utexas.edu.warble2.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by yosef on 12/21/2017.
 */

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addUser(User user);

    @Query("SELECT * FROM user WHERE id=:id")
    List<User> getUser(long id);

    @Query("SELECT * FROM user WHERE bridgeId=:bridgeId")
    List<User> getUserForBridge(long bridgeId);

    @Query("DELETE FROM user WHERE id=:id")
    void delete(long id);

    @Query("DELETE FROM user")
    void deleteAllUsers();
}
