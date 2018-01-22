package mpc.utexas.edu.warble2.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by yosef on 1/21/2018.
 */

@Entity(foreignKeys =
@ForeignKey(
        entity = Bridge.class,
        parentColumns = "id",
        childColumns = "bridgeId",
        onDelete = ForeignKey.CASCADE
),
        indices = {@Index(value = "id")}
)
public class Light {
    @PrimaryKey(autoGenerate = true)
    long id;
    public String lightName;
    public long bridgeId;

    public Light(String lightName) {
        this.lightName = lightName;
    }
}
