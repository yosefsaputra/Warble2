package mpc.utexas.edu.warble2.services.PhilipsHue;

import java.util.HashMap;
import java.util.List;

import mpc.utexas.edu.warble2.services.Service;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsLight;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by yosef on 11/6/2017.
 */

public interface PhilipsHueService {
    @POST("/api")
    @Headers({
            "Content-Type: application/json"
    })
    Call<List<CreateUserResponse>> createUser(@Body CreateUserRequest createUserRequest);

    @GET("/api/{user}")
    Call<ResponseBody> getInfo(@Path("user") String userId);

    @GET("/api/{user}/lights")
    Call<ResponseBody> getLights(@Path("user") String userId);

    @GET("/api/{user}/groups")
    Call<List<Object>> getGroups(@Path("user") String userId);

    @GET("/api/{user}/config")
    Call<List<Object>> getConfig(@Path("user") String userId);

    @GET("/api/{user}/scenes")
    Call<List<Object>> getScenes(@Path("user") String userId);

    @GET("/api/{user}/sensors")
    Call<List<Object>> getSensors(@Path("user") String userId);

    @GET("/api/{user}/rules")
    Call<List<Object>> getRules(@Path("user") String userId);


    // Lights Capabilities
    @GET("/api/{user}/lights/{lightId}/state")
    Call<List<Object>> getLightState(@Path("user") String userId, @Path("lightId") int lightId);

    @PUT("/api/{user}/lights/{lightId}/state")
    @Headers({
            "Content-Type: application/json"
    })
    Call<List<Object>> putLight(@Path("user") String userId, @Path("lightId") String lightId, @Body HashMap<String, Object> lightState);
}
