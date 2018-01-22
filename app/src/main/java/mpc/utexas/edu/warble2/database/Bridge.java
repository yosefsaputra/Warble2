package mpc.utexas.edu.warble2.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by yosef on 12/21/2017.
 */

@Entity
public class Bridge {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String UUID;
    public String name;
    public String base_url;

    public Bridge(String UUID, String name, String base_url) {
        this.UUID = UUID;
        this.name = name;
        this.base_url = base_url;
    }
}
