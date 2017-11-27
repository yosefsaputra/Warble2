package mpc.utexas.edu.warble2.services.PhilipsHue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by yosef on 11/6/2017.
 */

public class CreateUserResponse {
    @SerializedName("success")
    @Expose
    private Success success;

    public Success getSuccess() {
        return success;
    }

    public class Success {
        @SerializedName("username")
        @Expose
        private String username;

        public String getUsername() {
            return username;
        }
    }

    @SerializedName("error")
    @Expose
    private Error error;

    public Error getError() {
        return error;
    }

    public class Error {
        @SerializedName("type")
        @Expose
        private String type;

        public String getType() {
            return type;
        }

        @SerializedName("address")
        @Expose
        private String address;

        public String getAddress() {
            return address;
        }

        @SerializedName("description")
        @Expose
        private String description;

        public String getDescription() {
            return description;
        }
    }
}
