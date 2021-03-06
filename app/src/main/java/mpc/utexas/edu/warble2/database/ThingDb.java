package mpc.utexas.edu.warble2.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import mpc.utexas.edu.warble2.features.Location;

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
        indices = {@Index(value = "dbid"),  @Index(value = "bridgeDbid")}
)
public class ThingDb {
    @PrimaryKey(autoGenerate = true)
    public long dbid;
    public String name;
    public String id;
    public String category;
    @TypeConverters(LocationConverter.class)
    public Location location;
    public long bridgeDbid;

    public ThingDb(String name, String id, String category, Location location, long bridgeDbid) {
        this.name = name;
        this.id = id;
        this.category = category;
        if (location == null) {
            this.location = new Location(0,0);
        } else {
            this.location = location;
        }
        this.bridgeDbid = bridgeDbid;
    }

    public String toString() {
        String string = "";
        string += String.format("Name: %s, ", this.name);
        string += String.format("Category: %s, ", this.category);
        string += String.format("Location: %s", this.location);
        return string;
    }
}
