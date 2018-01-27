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
        entity = BridgeDb.class,
        parentColumns = "dbid",
        childColumns = "bridgeDbid",
        onDelete = ForeignKey.CASCADE
),
        indices = {@Index(value = "dbid"),  @Index(value = "bridgeDbid"), @Index(value = "userDbid")}
)
public class ThingDb {
    @PrimaryKey(autoGenerate = true)
    public long dbid;
    public String name;
    public String id;
    public String category;
    public long bridgeDbid;
    public long userDbid;

    public ThingDb(String name, String id, String category, long bridgeDbid, long userDbid) {
        this.name = name;
        this.id = id;
        this.category = category;
        this.bridgeDbid = bridgeDbid;
        this.userDbid = userDbid;
    }
}
