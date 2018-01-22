package mpc.utexas.edu.warble2.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by yosef on 12/21/2017.
 */

@Entity(foreignKeys =
            @ForeignKey(
                    entity = Bridge.class,
                    parentColumns = "id",
                    childColumns = "bridgeId",
                    onDelete = ForeignKey.CASCADE
            ),
        indices = {@Index(value = "id"), @Index(value = "userId")}
)
public class User {
    @PrimaryKey(autoGenerate = true)
    long id;
    public String user;
    public String userId;
    long bridgeId;

    public User(String user, String userId, long bridgeId) {
        this.user = user;
        this.userId = userId;
    }
}
