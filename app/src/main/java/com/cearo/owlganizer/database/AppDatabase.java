package com.cearo.owlganizer.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.cearo.owlganizer.database.daos.AssessmentDao;
import com.cearo.owlganizer.database.daos.CourseDao;
import com.cearo.owlganizer.database.daos.MentorDao;
import com.cearo.owlganizer.database.daos.NoteDao;
import com.cearo.owlganizer.database.daos.TermDao;
import com.cearo.owlganizer.models.Assessment;
import com.cearo.owlganizer.models.Course;
import com.cearo.owlganizer.models.Mentor;
import com.cearo.owlganizer.models.Note;
import com.cearo.owlganizer.models.Term;
import com.cearo.owlganizer.utils.converters.Converters;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
    This class will serve as the database reference for the application. Database operations will
    be performed using the DAO's and the databaseWriteExecutor (as Room doesn't allow db operations
    on the main thread due to UI locking). As there should only be 1 database reference, the
    Constructor is a Singleton to ensure only one instance can be created.

    This class was created using this guide as reference:
    https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#6
*/

// Room annotation declaring a database
@Database(entities = {Term.class, Course.class, Mentor.class, Note.class, Assessment.class},
        version = 1,
        exportSchema = false)
// Room annotation declaring data type converters for Room to use
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    // **** Getters for DAO's ****

    public abstract TermDao termDao();
    public abstract CourseDao courseDao();
    public abstract MentorDao mentorDao();
    public abstract NoteDao noteDao();
    public abstract AssessmentDao assessmentDao();

    // **** End Getters for DAO's ****

    private static volatile AppDatabase INSTANCE;
    // Thread pool
    private static final int NUMBER_OF_THREADS = 4;
    // Thread Executor for db operations as Room doesn't allow them on the main thread.
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Singleton Constructor ensuring only one instance can exist.
    public static AppDatabase getDatabase(final Context context) {
        /*
            If INSTANCE is null, build a RoomDatabase instance, otherwise return the one that exists.
         */
        if (INSTANCE == null) {
            // Thread safety
            synchronized (AppDatabase.class) {
                // Ensuring INSTANCE is still null after the lock is acquired.
                if (INSTANCE == null) {
                    // Build the RoomDatabase instance
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), // App context
                            AppDatabase.class, // RoomDatabase class
                    "owlganizer_database") // Database file name
                            .addCallback(populateDatabase) // Callback to populate the database
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /*
        This Callback will be added to the method chain in getDatabase to seed the database with
        values upon database creation. These values are just model data values to showcase the
        application in a populated state.
     */
    private static RoomDatabase.Callback populateDatabase = new RoomDatabase.Callback() {

        // This method will be called when the database is created
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // DB operations cannot be performed on the main thread
            databaseWriteExecutor.execute(() -> {
                MentorDao mentorDao = INSTANCE.mentorDao();
                TermDao termDao = INSTANCE.termDao();

                // **** Begin Sample Data ****
                Mentor mentor1 = new Mentor("John Porter",
                        "385-428-5747", "john.porter@wgu.edu");
                Mentor mentor2 = new Mentor("Dana Cobbs",
                        "384-758-8459", "dana.cobbs@wgu.edu");
                Mentor mentor3 = new Mentor("Amy Antonucci",
                        "385-932-1275", "amy.antonucci@wgu.edu");
                Mentor[] mentors = {mentor1, mentor2, mentor3};

                mentorDao.insertMentors(mentors);

                // Current date at the time the method is called
                LocalDate startDate = LocalDate.now();
                // Creating a Term duration of 6 months
                LocalDate endDate = startDate.plus(6, ChronoUnit.MONTHS);
                Term term1 = new Term("Term 1", startDate, endDate);

                // 2nd test Term's start will be 6 months after the first started
                LocalDate startDate2 = startDate.plus(6, ChronoUnit.MONTHS);
                // Term duration of 6 months
                LocalDate endDate2 = startDate2.plus(6, ChronoUnit.MONTHS);
                Term term2 = new Term ("Term 2", startDate2, endDate2);

                // Sample Terms to be inserted into DB
                Term[] termList = {term1, term2};

                // **** End Sample Data ****

                // Inserting sample Terms
                termDao.insertAllTerms(termList);
            });
        }
    };
}
