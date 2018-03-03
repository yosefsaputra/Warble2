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
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addUser(UserDb userDb);

    @Update
    void updateUser(UserDb userDb);

    @Query("SELECT * FROM UserDb")
    List<UserDb> getAllUsers();

    @Query("SELECT * FROM UserDb WHERE category=:category")
    List<UserDb> getAllUsersByCategory(String category);

    @Query("SELECT * FROM UserDb WHERE dbid=:dbid")
    UserDb getUser(long dbid);

    @Query("SELECT * FROM UserDb WHERE id=:id")
    UserDb getUserById(String id);

    @Query("DELETE FROM UserDb WHERE dbid=:dbid")
    void delete(long dbid);

    @Query("DELETE FROM UserDb")
    void deleteAllUsers();

    @Query("DELETE FROM UserDb WHERE category=:category")
    void deleteAllUsersByCategory(String category);
}
