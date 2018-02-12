package mpc.utexas.edu.warble2.services.Wink;

import android.arch.persistence.room.Ignore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by yosef on 2/11/2018.
 */

public class GetThingsResponse {
    @SerializedName("data")
    @Expose
    private List<Thing> data;

    @SerializedName("errors")
    @Expose
    private List<String> errors;

    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

    public class Thing {
        // TODO Parse other fields
//        @SerializedName("uuid")
//        @Expose
//        private String uuid;
//
//        @SerializedName("desired_state")
//        @Expose
//        private String desiredState;
//
//        @SerializedName("last_reading")
//        @Expose
//        private String lastReading;
//
//        @SerializedName("subscription")
//        @Expose
//        private String subscription;
//
//        @SerializedName("light_bulb_id")
//        @Expose
//        private String thingId;
//
//        @SerializedName("name")
//        @Expose
//        private String name;
//
//        @SerializedName("locale")
//        @Expose
//        private String locale;
//
//        @SerializedName("units")
//        @Expose
//        private String units;
//
//        @SerializedName("created_at")
//        @Expose
//        private Date created_at;
//
//        @SerializedName("hidden_at")
//        @Expose
//        private String hidden_at;
//
//        @SerializedName("capabilities")
//        @Expose
//        private String capabilities;
//
//        @SerializedName("triggers")
//        @Expose
//        private String triggers;
//
//        @SerializedName("manufacturer_device_model")
//        @Expose
//        private String manufacturerDeviceModel;
//
//        @SerializedName("manufacturer_device_id")
//        @Expose
//        private String manufacturerDeviceId;
//
//        @SerializedName("device_manufacturer")
//        @Expose
//        private String deviceManufacturer;
//
//        @SerializedName("model_name")
//        @Expose
//        private String modelName;
//
//        @SerializedName("upc_id")
//        @Expose
//        private String upcId;
//
//        @SerializedName("upc_code")
//        @Expose
//        private String upcCode;
//
//        @SerializedName("primary_upc_code")
//        @Expose
//        private String primaryUpcCode;
//
//        @SerializedName("gang_id")
//        @Expose
//        private String gangId;
//
//        @SerializedName("hub_id")
//        @Expose
//        private String hubId;
//
//        @SerializedName("local_id")
//        @Expose
//        private String localId;
//
//        @SerializedName("radio_type")
//        @Expose
//        private String radioType;
//
//        @SerializedName("linked_service_id")
//        @Expose
//        private String linkedServiceId;
//
//        @SerializedName("lat_lng")
//        @Expose
//        private String latLng;
//
//        @SerializedName("location")
//        @Expose
//        private String location;
//
//        @SerializedName("order")
//        @Expose
//        private int order;

        public String getThingId() {
            return this.thingId;
        }

        public String getName() {
            return this.name;
        }

        public String getLocation() {
            return this.location;
        }

        public String getHubId() {
            return this.hubId;
        }

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("light_bulb_id")
        @Expose
        private String thingId;

        @SerializedName("location")
        @Expose
        private String location;

        @SerializedName("hub_id")
        @Expose
        private String hubId;
    }

    private class Pagination {
        @SerializedName("count")
        @Expose
        private int count;
    }

    public List<Thing> getData() {
        return this.data;
    }

    public Pagination getPagination() {
        return this.pagination;
    }
}
