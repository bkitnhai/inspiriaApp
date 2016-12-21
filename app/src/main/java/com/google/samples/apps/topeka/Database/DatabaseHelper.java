package com.google.samples.apps.topeka.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by NguyenHai on 3/23/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "nguyenhai";

    // Table Names
    private static final String DB_TABLE = "table_image";

    // column names
    private static final String KEY_NAME = "image_name";
    private static final String KEY_EMAIL = "image_email";
    private static final String KEY_IMAGE = "image_data";

    // Table create statement
    private static final String CREATE_TABLE_IMAGE = "CREATE TABLE " + DB_TABLE + "(" +
            KEY_NAME + " TEXT," +
            KEY_EMAIL + " TEXT," +
            KEY_IMAGE + " BLOB);";

    private static final String KEY_MAC = "mac_image";
    // Table Names
    private static final String DB_MAC = "mac_image_db";
    private static final String CREATE_MAC_IMAGE = "CREATE TABLE " + DB_MAC + "(" +
            KEY_MAC + " TEXT," +
            KEY_IMAGE + " BLOB);";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        _("DatabaseHelper");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        _("onCreate");
        // creating table
        db.execSQL(CREATE_TABLE_IMAGE);
        db.execSQL(CREATE_MAC_IMAGE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        _("onUpgrade");
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DB_MAC);

        // create new table
        onCreate(db);
    }

    public void addEntry(String name, String email, byte[] image) {
        _("addEntry");
        Log.i("test12", name);
        Log.i("test12",email);
        Log.i("test12", String.valueOf(image));

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM table_image");
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(KEY_NAME, name);
            cv.put(KEY_EMAIL, email);
            cv.put(KEY_IMAGE, image);
           // db.insert(DB_TABLE, null, cv);
            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(DB_TABLE, null, cv);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("error", "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }
    public void addImageMac(String[] mac, Bitmap[] image) {

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM mac_image_db");
        db.beginTransaction();
        try {

            DbBitmapUtility contertByte = new DbBitmapUtility();
            for (int temp = 0; temp < mac.length; temp++) {
                ContentValues cv = new ContentValues();
                byte[] image2 = contertByte.getBytes(image[temp]);// put this one inni
                String mac2 = mac[temp];
                cv.put(KEY_MAC, mac2);
                cv.put(KEY_IMAGE, image2);
                db.insertOrThrow(DB_MAC, null, cv);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("error", "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }


    }
    public byte[] getImage(String name) {
        _("getImage");
        SQLiteDatabase db = getWritableDatabase();

        if (db== null)  Log.i("test11", "null db");
        Cursor c = db.rawQuery("SELECT * FROM table_image", null);
        if (c== null)  Log.i("test11", "null c");

        int nameIndex = c.getColumnIndex(KEY_NAME);
        int ImageIndex = c.getColumnIndex(KEY_IMAGE);
        try {
            if (c.moveToFirst()) {
                do {
                    if (c.getString(nameIndex).equals(name)) {
                        return c.getBlob(ImageIndex);
                    }

                } while(c.moveToNext());
            }
        } catch (Exception e) {
            Log.d("error", "Error while trying to get posts from database");
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return null;
        //byte[] image = cursor.getBlob(1);
    }
    public ArrayList<Bitmap> getImageForQuestion(String mac) {
        _("getImageForQuestion");
        SQLiteDatabase db = getWritableDatabase();

        if (db == null) Log.i("test11", "null db");
        Cursor c = db.rawQuery("SELECT * FROM mac_image_db", null);
        if (c == null) Log.i("test11", "null c");
        int nameIndex = c.getColumnIndex(KEY_MAC);
        int ImageIndex = c.getColumnIndex(KEY_IMAGE);
        ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
        if (c != null) {
            while (c.moveToNext()) {
                Log.i("nameIndex",c.getString(nameIndex));
                if (c.getString(nameIndex).equals(mac)) {

                    byte[] blob = c.getBlob(ImageIndex);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                    bitmaps.add(bitmap);
                }
            }
        }
        //byte[] image = cursor.getBlob(1);
        return bitmaps;
    }

    public ArrayList<Cursor> getData(String Query){
        _("ArrayList");
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }

    public void _(String s){
        Log.i("test", "DatabaseHelper"+"#####"+s);
    }
    }