package br.com.onibussantos.DAO;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import br.com.onibussantos.DbLugar;
import br.com.onibussantos.VO.LugarVO;

public class LugarDAO {
	private static String table_name = "lugar";
	private static Context ctx = null;
	private static String[] columns = {"id", "latitude", "longitude"};
	
	public LugarDAO(Context ctx){
		this.ctx = ctx;
	}
	
	public boolean insert(LugarVO vo){
		
		SQLiteDatabase db = new DbLugar(ctx).getWritableDatabase();
		ContentValues ctv = new ContentValues();
		
		ctv.put("latitude", vo.getLatitude());
		ctv.put("longitude", vo.getLongitude());
		
		return (db.insert(table_name, null, ctv) > 0);		
	}
	
	public boolean delete(LugarVO vo){
		
		SQLiteDatabase db = new DbLugar(ctx).getWritableDatabase();
		
		return (db.delete(table_name, "id=?", new String[] {vo.getId().toString()}) > 0);
	}
	
	public boolean update(LugarVO vo){
		
		SQLiteDatabase db = new DbLugar(ctx).getWritableDatabase();
		ContentValues ctv = new ContentValues();
		
		ctv.put("latitude", vo.getLatitude());
		ctv.put("longitude", vo.getLongitude());
		
		return (db.update(table_name, ctv, "id=?", new String[]{vo.getId().toString()}) > 0);
	}
	
	public LugarVO getById(Integer ID){
		
		SQLiteDatabase db = new DbLugar(ctx).getReadableDatabase();
		Cursor rs = db.query(table_name, columns, "id=?", new String []{ID.toString()}, null, null, null);
		
		LugarVO vo = null;
		
		if(rs.moveToFirst()){
			vo = new LugarVO();
			
			vo.setId(rs.getInt(rs.getColumnIndex("id")));
			vo.setLatitude(rs.getFloat(rs.getColumnIndex("latitude")));
			vo.setLongitude(rs.getFloat(rs.getColumnIndex("longitude")));
		}
		
		return vo;
	}
	
	public List<LugarVO> getAll() {
		SQLiteDatabase db = new DbLugar(ctx).getReadableDatabase();
		
		Cursor rs = db.rawQuery("SELECT * FROM lugar", null);
		
		List<LugarVO> lista = new ArrayList<LugarVO>();
		
		while(rs.moveToNext()){
			LugarVO vo = new LugarVO(rs.getInt(0), rs.getFloat(1), rs.getFloat(2));
			lista.add(vo);
		}
			
		return lista;
	}
	
}
