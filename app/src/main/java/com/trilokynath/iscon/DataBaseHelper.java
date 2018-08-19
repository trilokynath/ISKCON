package com.trilokynath.iscon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

class DataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DATA_CONTACTS";

    Context context;
    DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // todo text
        db.execSQL("CREATE TABLE " + "contacts" + " (" +
                "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name" + " TEXT, " +
                "Address" + " TEXT, " +
                "Person" + " TEXT, " +
                "Mobile" + " TEXT unique " +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }
    boolean addRecords(DATA DATA){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Name", DATA.getName());
        cv.put("Address", DATA.getAddress());
        cv.put("Mobile", DATA.getMobile());
        cv.put("Person", DATA.getPerson());
        Long id  = db.insert("contacts", null, cv);
        return true;
    }

    boolean updateRecords(DATA DATA, Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Name", DATA.getName());
        cv.put("Address", DATA.getAddress());
        cv.put("Mobile", DATA.getMobile());
        cv.put("Person", DATA.getPerson());
        db.update("contacts", cv, "ID="+id, null);
        return true;
    }

    public ArrayList<DATA> getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DATA> listItems = new ArrayList<DATA>();
        Cursor cursor = db.rawQuery("SELECT * from " + "contacts order by Name asc", new String[]{});
        if (cursor.moveToFirst()) {
            do {
                DATA DATA = new DATA();

                DATA.ID = cursor.getInt(cursor.getColumnIndex("ID"));
                DATA.name = cursor.getString(cursor.getColumnIndex("Name"));
                DATA.address = cursor.getString(cursor.getColumnIndex("Address"));
                DATA.mobile = cursor.getString(cursor.getColumnIndex("Mobile"));
                DATA.person = cursor.getString(cursor.getColumnIndex("Person"));
                listItems.add(DATA);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listItems;
    }

    public void deleteContact(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + "contacts where ID="+id);
    }

    public void clearData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + "contacts");
    }

    public boolean getRecord(String mobile){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        DATA DATA = new DATA();
        try {
            cursor = db.rawQuery("SELECT * FROM contacts WHERE Mobile="+mobile,null);
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();

                DATA.ID = cursor.getInt(cursor.getColumnIndex("ID"));
                DATA.name = cursor.getString(cursor.getColumnIndex("Name"));
                DATA.address = cursor.getString(cursor.getColumnIndex("Address"));
                DATA.mobile = cursor.getString(cursor.getColumnIndex("Mobile"));
                DATA.person = cursor.getString(cursor.getColumnIndex("Person"));
                return true;
            }
            return false;
        }finally {
            cursor.close();
        }
    }

    public long getDATACount(){
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, "contacts");
        db.close();
        return count;
    }
}