package mpc.utexas.edu.warble2.services.Wink;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by yosef on 2/11/2018.
 */

public class PutThingRequest {
    @SerializedName("desired_state")
    @Expose
    public DesiredState desiredState;

    public class DesiredState {
        @SerializedName("powered")
        @Expose
        public String powered;

        @SerializedName("brightness")
        @Expose
        public String brightness;
    }

    public PutThingRequest(String powered, String brightness) {
        this.desiredState.powered = powered;
        this.desiredState.brightness = brightness;
    }
}
