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
        entity = BridgeDb.class,
        parentColumns = "dbid",
        childColumns = "bridgeDbid",
        onDelete = ForeignKey.CASCADE
),
        indices = {@Index(value = "dbid"), @Index(value = "id"), @Index(value = "bridgeDbid")}
)
public class UserDb {
    @PrimaryKey(autoGenerate = true)
    public long dbid;
    public String name;
    public String id;
    public String secretId;
    public String category;
    public String accessToken;
    public String refreshToken;
    public long bridgeDbid;

//    public UserDb(String name, String id, String category, long bridgeDbid) {
//        this.name = name;
//        this.id = id;
//        this.secretId = null;
//        this.category = category;
//        this.accessToken = null;
//        this.refreshToken = null;
//        this.bridgeDbid = bridgeDbid;
//    }

    public UserDb(String name, String id, String secretId, String category, String accessToken, String refreshToken, long bridgeDbid) {
        this.name = name;
        this.id = id;
        this.secretId = secretId;
        this.category = category;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.bridgeDbid = bridgeDbid;
    }
}
