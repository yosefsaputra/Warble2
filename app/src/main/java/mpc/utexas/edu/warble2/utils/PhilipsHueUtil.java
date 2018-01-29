package mpc.utexas.edu.warble2.utils;

import mpc.utexas.edu.warble2.services.PhilipsHue.PhilipsHueService;

/**
 * Created by yosef on 11/6/2017.
 */

public class PhilipsHueUtil {
    public static PhilipsHueService getService(String baseUrl) {
        return RetrofitClient.getRetrofitClient(baseUrl).create(PhilipsHueService.class);
    }
}
