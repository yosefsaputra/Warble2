package mpc.utexas.edu.warble2.services.PhilipsHue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by yosef on 11/6/2017.
 */

public class CreateUserRequest {
    @SerializedName("devicetype")
    @Expose
    private String devicetype;

    public String getDevicetype(){
        return devicetype;
    }

    public void setDevicetype(String devicetype){
        this.devicetype = devicetype;
    }
}
