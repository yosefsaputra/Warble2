package mpc.utexas.edu.warble2.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by yosef on 3/8/2018.
 */

public class ViewUtil {
    public static float pxToDp(Context context, float px)  {
        Resources resources = context.getResources();
        return (float) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, resources.getDisplayMetrics());
    }

    public static int dpToPx(Context context, int dp) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }


}
