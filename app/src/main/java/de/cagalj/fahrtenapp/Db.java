package de.cagalj.fahrtenapp;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import org.json.*;

public class Db extends SQLiteOpenHelper {
    Db(Context c){super(c,"fahrten.db",null,1);}
    @Override public void onCreate(SQLiteDatabase d){
        d.execSQL("CREATE TABLE customers(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL)");
        d.execSQL("CREATE TABLE projects(id INTEGER PRIMARY KEY AUTOINCREMENT,customer_id INTEGER NOT NULL,name TEXT NOT NULL,address TEXT,km REAL DEFAULT 0,work TEXT,rate REAL DEFAULT 0,km_rate REAL DEFAULT 0,contact TEXT,phone TEXT,number TEXT)");
        d.execSQL("CREATE TABLE entries(id INTEGER PRIMARY KEY AUTOINCREMENT,project_id INTEGER NOT NULL,day TEXT NOT NULL,start TEXT,end TEXT,break_min INTEGER DEFAULT 0,travel_out INTEGER DEFAULT 0,travel_back INTEGER DEFAULT 0,travel_minutes INTEGER DEFAULT 0,extra REAL DEFAULT 0,status TEXT DEFAULT 'Offen',rate REAL DEFAULT 0,km_rate REAL DEFAULT 0,km REAL DEFAULT 0,note TEXT)");
    }
    @Override public void onUpgrade(SQLiteDatabase d,int oldV,int newV){}
    Cursor query(String sql,String... args){return getReadableDatabase().rawQuery(sql,args);}
    long insert(String table,ContentValues v){return getWritableDatabase().insert(table,null,v);}
    void update(String table,long id,ContentValues v){getWritableDatabase().update(table,v,"id=?",new String[]{""+id});}
    void delete(String table,long id){getWritableDatabase().delete(table,"id=?",new String[]{""+id});}
    JSONArray backup() throws JSONException {
        JSONArray all=new JSONArray();
        for(String table:new String[]{"customers","projects","entries"}){
            JSONArray rows=new JSONArray(); Cursor c=query("SELECT * FROM "+table);
            while(c.moveToNext()){JSONObject o=new JSONObject();for(int i=0;i<c.getColumnCount();i++){int type=c.getType(i);Object value=type==Cursor.FIELD_TYPE_NULL?JSONObject.NULL:type==Cursor.FIELD_TYPE_INTEGER?c.getLong(i):type==Cursor.FIELD_TYPE_FLOAT?c.getDouble(i):c.getString(i);o.put(c.getColumnName(i),value);}rows.put(o);}c.close();
            JSONObject item=new JSONObject();item.put("table",table);item.put("rows",rows);all.put(item);
        }return all;
    }
    void restore(JSONArray all)throws JSONException{
        SQLiteDatabase d=getWritableDatabase();d.beginTransaction();try{
            if(all.length()!=3)throw new JSONException("Ungültiges Backup");
            String[] tables={"customers","projects","entries"};
            for(int j=0;j<3;j++)if(!tables[j].equals(all.getJSONObject(j).getString("table")))throw new JSONException("Ungültige Tabellen");
            for(int j=2;j>=0;j--)d.delete(tables[j],null,null);
            for(int j=0;j<3;j++){JSONArray rows=all.getJSONObject(j).getJSONArray("rows");for(int i=0;i<rows.length();i++){JSONObject o=rows.getJSONObject(i);ContentValues v=new ContentValues();java.util.Iterator<String> keys=o.keys();while(keys.hasNext()){String k=keys.next();Object val=o.get(k);if(val==JSONObject.NULL)v.putNull(k);else if(val instanceof Integer||val instanceof Long)v.put(k,((Number)val).longValue());else if(val instanceof Number)v.put(k,((Number)val).doubleValue());else v.put(k,String.valueOf(val));}if(d.insertOrThrow(tables[j],null,v)<0)throw new JSONException("Import fehlgeschlagen");}}
            d.setTransactionSuccessful();
        }finally{d.endTransaction();}
    }
}
