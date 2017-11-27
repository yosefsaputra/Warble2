package mpc.utexas.edu.warble2.utils;

import mpc.utexas.edu.warble2.services.PhilipsHue.PhilipsHueService;

/**
 * Created by yosef on 11/6/2017.
 */

public class PhilipsHueUtil {
    // TODO Create a way to get IP address automatically when a new Philips hub is detected
    // private static final String BASE_URL = "http://192.168.1.74";
    // TODO To change back to the Philips hub IP address
    // private static final String BASE_URL = "http://apttest-182523.appspot.com";

    public static PhilipsHueService getService(String base_url) {
        return RetrofitClient.getRetrofitClient(base_url).create(PhilipsHueService.class);
    }
}
