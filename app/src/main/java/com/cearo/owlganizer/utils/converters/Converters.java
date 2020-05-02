package com.cearo.owlganizer.utils.converters;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/*
    This class will store TypeConverters needed by Room to translate unsupported SQLite data
    types into supported ones. The @TypeConverter Annotation is used by Room to know what types of
    converters it has available when it encounters an issue inserting a field into the database.
 */

public class Converters {
    /*
        LocalDate to String Converters. SQLite does not accept Dates of any kind. Instead Dates
        must be stored as Strings or Longs. Since we are working with LocalDate and not Date, we
        will convert from LocalDate to String and back when retrieving from the database.
     */

    // Takes in YYYY-MM-DD formatted String and returns a LocalDate object matching it.
    @TypeConverter
    public static LocalDate toLocalDate(String dateString) {
        // Null safety
        return dateString != null ? LocalDate.parse(dateString) : null;
    }

    // Takes in a LocalDate object and returns a yyyy-MM-dd formatted String representation
    @TypeConverter
    public static String fromLocalDate(LocalDate date) {
        // Format String
        final String DATE_FORMAT = "yyyy-MM-dd";
        // Creating Formatter using the Format String
        DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return date != null ? date.format(dtFormat) : null;
    }

//    @TypeConverter
//    public static String toStringBuilder(String string) {
//        return new StringBuilder(string);
//    }
//
//    @TypeConverter
//    public static String fromStringBuilder(String builder) {
//        return builder != null ? builder.toString() : null;
//    }
}
