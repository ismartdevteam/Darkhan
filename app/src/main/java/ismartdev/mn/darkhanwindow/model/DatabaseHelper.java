package ismartdev.mn.darkhanwindow.model;

/**
 * Created by Ulziiburen on 1/22/2016.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static String databaseName = "darkhan.ormlite";
    private static int databaseVersion = 1;
    Dao<Places, Integer> places = null;

    private static DatabaseHelper helper = null;

    public DatabaseHelper(Context context) {
        super(context, databaseName, null, databaseVersion);
        // TODO Auto-generated constructor stub

    }

    public static synchronized DatabaseHelper getHelper(Context context) {
        if (helper == null) {
            helper = new DatabaseHelper(context);
        }

        return helper;
    }

    // creating tables
    @Override
    public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
        // TODO Auto-generated method stub
        try {

            TableUtils.createTable(arg1, Places.class);


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // updgrade tables
    @Override
    public void onUpgrade(SQLiteDatabase arg0, ConnectionSource connectionSource, int arg2,
                          int arg3) {
        // TODO Auto-generated method stub

        try {
            TableUtils.dropTable(connectionSource, Places.class, true);

            onCreate(arg0, connectionSource);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Dao<Places, Integer> getPlaces(){
        if (places == null) {
            try {
                places = getDao(Places.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return places;
    }

    public void deletePlaces() {
        try {
            TableUtils.clearTable(connectionSource, Places.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        places = null;

        super.close();
    }

}
