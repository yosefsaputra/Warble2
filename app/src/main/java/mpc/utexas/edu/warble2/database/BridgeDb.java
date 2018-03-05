package mpc.utexas.edu.warble2.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by yosef on 12/21/2017.
 */

//@Entity(foreignKeys =
//@ForeignKey(
//        entity = UserDb.class,
//        parentColumns = "dbid",
//        childColumns = "userDbid",
//        onDelete = ForeignKey.CASCADE
//),
//        indices = {@Index(value = "dbid"), @Index(value = "userDbid")}
//)
@Entity(indices = {@Index(value = "dbid"), @Index(value = "userDbid")}
)
public class BridgeDb {
    @PrimaryKey(autoGenerate = true)
    public long dbid;
    public String UUID;
    public String name;
    public String category;
    public String baseUrl;
    public long userDbid;

    public BridgeDb(String UUID, String name, String category, String baseUrl, long userDbid) {
        this.UUID = UUID;
        this.name = name;
        this.category = category;
        this.baseUrl = baseUrl;
        this.userDbid = userDbid;
    }

    public String toString() {
        String string = "";
        string += String.format("Name: %s, ", this.name);
        string += String.format("ID: %s, ", this.UUID);
        string += String.format("Base URL: %s, ", this.baseUrl);
        string += String.format("User dbid: %s", this.userDbid);
        return string;
    }
}
