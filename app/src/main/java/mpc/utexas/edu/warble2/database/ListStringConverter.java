package mpc.utexas.edu.warble2.database;

import android.arch.persistence.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yosef on 3/2/2018.
 */

public class ListStringConverter {
    @TypeConverter
    public static List<String> toListString(String string) {
        if (string == null) {
            return null;
        }
        List<String> stringList = new ArrayList<>(Arrays.asList(string.split(",")));

        return stringList;
    }

    @TypeConverter
    public static String toString(List<String> stringList) {
        if (stringList == null) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (String i: stringList) {
            stringBuilder.append(",").append(i);
        }

        return stringBuilder.toString();
    }
}
