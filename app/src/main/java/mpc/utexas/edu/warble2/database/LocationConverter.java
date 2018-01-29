package mpc.utexas.edu.warble2.database;

import android.arch.persistence.room.TypeConverter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mpc.utexas.edu.warble2.features.Location;

/**
 * Created by yosef on 1/28/2018.
 */

public class LocationConverter {
    @TypeConverter
    public static Location toLocation(String locationString) {
        if (locationString == null) {
            return null;
        }

        String regex = "(\\()(\\d+)(,)(\\d+)(\\))";

        Pattern r = Pattern.compile(regex);
        Matcher m = r.matcher(locationString);

        if (m.find()) {
            return new Location(Integer.parseInt(m.group(2)), Integer.parseInt(m.group(4)));
        } else {
            return null;
        }
    }

    @TypeConverter
    public static String toString(Location location) {
        if (location == null) {
            return null;
        }

        return String.format("(%s,%s)", location.getxCoordinate(), location.getyCoordinate());
    }
}
