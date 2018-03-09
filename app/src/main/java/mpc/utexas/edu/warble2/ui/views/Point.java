package mpc.utexas.edu.warble2.ui.views;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yosef on 3/8/2018.
 */

public class Point {
    public static String TAG = "Point";
    public static String identifier = "Point";
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Point toPoint(String pointString) {
        if (pointString == null) {
            return null;
        }

        String regex = "(\\(*)(\\d+)(,)(\\d+)(\\)*)";

        Pattern r = Pattern.compile(regex);
        Matcher m = r.matcher(pointString);

        if (m.find()) {
            return new Point(Integer.parseInt(m.group(2)), Integer.parseInt(m.group(4)));
        } else {
            return null;
        }
    }

    public static String toString(Point point) {
        if (point == null) {
            return null;
        }

        return String.format("(%s,%s)", point.x, point.y);
    }
}
