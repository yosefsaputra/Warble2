package mpc.utexas.edu.warble2.services.PhilipsHue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by yosef on 11/7/2017.
 */

public class ConfigResponse {
    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
