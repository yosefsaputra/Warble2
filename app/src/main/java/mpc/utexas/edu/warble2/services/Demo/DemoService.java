package mpc.utexas.edu.warble2.services.Demo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by yosef on 3/5/2018.
 */

public interface DemoService {
    @GET("/loadTrajectory")
    Call<ResponseBody> loadTrajectory();

    @POST("/saveCurrentLocation")
    Call<ResponseBody> saveCurrentLocation(@Body String currentLocation);
}
