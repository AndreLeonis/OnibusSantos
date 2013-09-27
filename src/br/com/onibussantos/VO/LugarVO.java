package br.com.onibussantos.VO;

public class LugarVO {
	
	private int id;
	private float latitude;
	private float longitude;
	
	public LugarVO(){
		
	}
	
	public LugarVO(int id, float latitude, float longitude){
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		
	}
	public Integer getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public float getLatitude() {
		return latitude;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	public float getLongitude() {
		return longitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	
	

}
