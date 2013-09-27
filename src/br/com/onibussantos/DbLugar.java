package br.com.onibussantos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbLugar extends SQLiteOpenHelper{
	
	private static String dbName = "lugar.db";
	private static String sql = "CREATE TABLE IF NOT EXISTS [lugar] ([id] INTEGER PRIMARY KEY AUTOINCREMENT, [latitude] float(10,8), [longitude] float(10,8));"; 
	private static int version = 1;
	
	public DbLugar(Context ctx){
		super(ctx,dbName, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sql);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
