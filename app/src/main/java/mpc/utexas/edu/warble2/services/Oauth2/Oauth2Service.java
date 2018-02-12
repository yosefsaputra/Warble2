package mpc.utexas.edu.warble2.services.Oauth2;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by yosef on 2/9/2018.
 */

public interface Oauth2Service {
    @GET("/oauth2/authorize?response_type=code")
    Call<ResponseBody> authorize(@Query("client_id") String client_id, @Query("redirect_uri") String redirect_uri);
}
