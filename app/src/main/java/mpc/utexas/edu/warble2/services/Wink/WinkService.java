package mpc.utexas.edu.warble2.services.Wink;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by yosef on 2/11/2018.
 */

public interface WinkService {
    @GET("/users/me/wink_devices")
    Call<GetThingsResponse> getThings(@Header("Authorization") String accessToken);

    @PUT("/light_bulbs/{light_id}")
    Call<ResponseBody> putLight(@Header("Authorization") String accessToken, @Path("light_id") String lightId, @Body HashMap<String, Object> lightState);
}
