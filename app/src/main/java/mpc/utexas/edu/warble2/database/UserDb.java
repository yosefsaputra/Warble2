package mpc.utexas.edu.warble2.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by yosef on 12/21/2017.
 */

@Entity
public class UserDb {
    @PrimaryKey(autoGenerate = true)
    public long dbid;
    public String name;
    public String id;
    public String secretId;
    public String category;
    public String accessToken;
    public String refreshToken;

    public UserDb(String name, String id, String secretId, String category, String accessToken, String refreshToken) {
        this.name = name;
        this.id = id;
        this.secretId = secretId;
        this.category = category;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
