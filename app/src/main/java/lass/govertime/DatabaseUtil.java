package lass.govertime;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Nailson on 11/03/2018.
 */

public class DatabaseUtil extends android.app.Application{
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }

        return mDatabase;
    }
}
