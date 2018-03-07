package mpc.utexas.edu.warble2.utils;

import mpc.utexas.edu.warble2.services.Demo.DemoService;

/**
 * Created by yosef on 3/5/2018.
 */

public class DemoUtil {
    private static String TAG = "DemoUtil";

    public static DemoService getService() {
        return RetrofitClient.getRetrofitClient("https://apttest-182523.appspot.com").create(DemoService.class);
    }
}
