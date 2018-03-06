package mpc.utexas.edu.warble2.utils;

import mpc.utexas.edu.warble2.services.Oauth2.Oauth2Service;

/**
 * Created by yosef on 2/11/2018.
 */

public class Oauth2Util {
    public static Oauth2Service getOauth2Service(String baseUrl) {
        return RetrofitClient.getRetrofitClient(baseUrl).create(Oauth2Service.class);
    }
}
