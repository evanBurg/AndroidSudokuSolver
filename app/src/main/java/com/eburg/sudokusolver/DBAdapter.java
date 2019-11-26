package com.eburg.sudokusolver;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static com.eburg.sudokusolver.Utilities.bitmapToBytes;
import static com.eburg.sudokusolver.Utilities.bytesToBitmap;
import static com.eburg.sudokusolver.Utilities.flattenArray;
import static com.eburg.sudokusolver.Utilities.inflateArray;

public class DBAdapter {

    public interface Listener {
        void update();
    }

    static final String KEY_ROWID = "_id";
    static final String KEY_PROBLEM = "problem";
    static final String KEY_SOLUTION = "solution";
    static final String KEY_IMAGE = "image";

    static final String TAG = "SudokuSolverDatabase";

    static final String DATABASE_NAME = "SudokuSolver";
    static final String DATABASE_TABLE = "previouspuzzles";
    static final int DATABASE_VERSION = 1;

    ArrayList<Listener> listeners;

    static final String DATABASE_CREATE = String.format(
            "create table if not exists %s (" +
                    "%s integer primary key autoincrement," +
                    "%s TEXT not null," +
                    "%s TEXT not null," +
                    "%s BLOB not null," +
                    ")",
            DATABASE_TABLE,
            KEY_ROWID,
            KEY_PROBLEM,
            KEY_SOLUTION,
            KEY_IMAGE
    );

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS pins");
            onCreate(db);
        }
    }

    public DBAdapter open(Listener listener) throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        DBHelper.onCreate(db);
        listeners.add(listener);
        return this;
    }

    public void close()
    {
        DBHelper.close();
    }

    public long insertSolution(Solution solution)
    {
        ContentValues initialValues = new ContentValues();

        initialValues.put(KEY_PROBLEM, flattenArray(solution.getProblem()));
        initialValues.put(KEY_SOLUTION, flattenArray(solution.getSolution()));
        initialValues.put(KEY_IMAGE, bitmapToBytes(solution.getImage()));
        long id = db.insert(DATABASE_TABLE, null, initialValues);
        updateListeners();
        return id;
    }

    public boolean deleteSolution(long rowId)
    {
        boolean success = db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
        updateListeners();
        return success;
    }

    public boolean deleteAllSolutions()
    {
        boolean success = db.delete(DATABASE_TABLE, KEY_ROWID + "> -1", null) > 0;
        updateListeners();
        return success;
    }

    public ArrayList<Solution> getAllSolutions()
    {
        ArrayList<Solution> pins = new ArrayList<>();
        Cursor mCursor = db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_PROBLEM, KEY_SOLUTION, KEY_IMAGE}, null, null, null, null, null);
        while(mCursor.moveToNext()){
            pins.add(new Solution(
                    mCursor.getInt(0),
                    inflateArray(mCursor.getString(1)),
                    inflateArray(mCursor.getString(2)),
                    bytesToBitmap(mCursor.getBlob(3))
            ));
        }
        mCursor.close();

        return pins;
    }

    public Solution getSolution(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_PROBLEM, KEY_SOLUTION, KEY_IMAGE}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }else{
            return null;
        }

        return new Solution(
                mCursor.getInt(0),
                inflateArray(mCursor.getString(1)),
                inflateArray(mCursor.getString(2)),
                bytesToBitmap(mCursor.getBlob(3))
        );
    }

    public boolean updateSolution(Solution solution)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_PROBLEM, flattenArray(solution.getProblem()));
        args.put(KEY_SOLUTION, flattenArray(solution.getSolution()));
        args.put(KEY_IMAGE, bitmapToBytes(solution.getImage()));
        boolean success = db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + solution.getId(), null) > 0;
        updateListeners();
        return success;
    }

    private void updateListeners(){
        for(Listener listener : listeners){
            listener.update();
        }
    }
}