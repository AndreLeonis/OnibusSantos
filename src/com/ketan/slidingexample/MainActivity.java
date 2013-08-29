package com.ketan.slidingexample;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends SherlockFragmentActivity implements Menufragment.MenuClickInterFace{
	SlidingMenu menu;
    TextView text;
    
    final int RQS_GooglePlayServices = 1;
	 private GoogleMap myMap;
	 private int mapType = GoogleMap.MAP_TYPE_NORMAL;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
						

		FragmentManager myFragmentManager = getSupportFragmentManager();
		   SupportMapFragment mySupportMapFragment 
		    = (SupportMapFragment)myFragmentManager.findFragmentById(R.id.map);
		   
		   myMap = mySupportMapFragment.getMap();
		   
		   //myMap.getUiSettings().isZoomControlsEnabled();
		   //diz a localizacao atual
		   myMap.setMyLocationEnabled(true);
		   
		   
		   //tipo de mapas
		   //myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		   myMap.setMapType(mapType);
		   //myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		  // myMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		   
		   myMap.getUiSettings().setZoomControlsEnabled(true);
	        myMap.getUiSettings().setCompassEnabled(true);
	        myMap.getUiSettings().setMyLocationButtonEnabled(true);
	        
	        myMap.getUiSettings().setRotateGesturesEnabled(true);
	        myMap.getUiSettings().setScrollGesturesEnabled(true);
	        myMap.getUiSettings().setTiltGesturesEnabled(true);
	        myMap.getUiSettings().setZoomGesturesEnabled(true);
	        //or myMap.getUiSettings().setAllGesturesEnabled(true);
	        
	        myMap.setTrafficEnabled(true);
		
		ActionBar ab = getSupportActionBar();
		
		ab.setDisplayShowCustomEnabled(true);
		ab.setHomeButtonEnabled(true);
		
		ab.setDisplayHomeAsUpEnabled(true);
		
		ab.setTitle("Rota de Onibus");
		
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.menu);
		menu.setSlidingEnabled(true);
		
		ab.setIcon(R.drawable.bus);		
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
				
		//super.onOptionsItemSelected(item);
		
		 switch (item.getItemId()) {		
		 		        
		 case R.id.homeAsUp:
			 menu.toggle();
			 Toast.makeText(getBaseContext(), "Clicou no home", Toast.LENGTH_LONG).show();
			 break;
	        case R.id.mapa_:
	        	Toast.makeText(getBaseContext(), "Clicou no mapa", Toast.LENGTH_LONG).show();
	        	 mapType = GoogleMap.MAP_TYPE_NORMAL;
	        	 item.setIcon(R.drawable.ic_map);
	        	
	           break;
	        case R.id.satelite_:
	        	Toast.makeText(getBaseContext(), "Clicou no satelite", Toast.LENGTH_LONG).show();
	        	mapType = GoogleMap.MAP_TYPE_SATELLITE;
	        	item.setIcon(R.drawable.ic_location_web_site);
	        	
	            break; 	        	
	        }
		myMap.setMapType(mapType);
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListitemClick(String item) {
	Toast.makeText(getBaseContext(), "Clicou" + item, Toast.LENGTH_LONG).show();
	//	text.setText(item);
	}
	
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu){
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		
		//inflater.inflate(R.menu.activity_main, menu);
		return true;		
		
	}
	
	protected void onResume() {
		  // TODO Auto-generated method stub
		  super.onResume();

		  int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		  
		  if (resultCode == ConnectionResult.SUCCESS){
		   Toast.makeText(getApplicationContext(), "isGooglePlayServicesAvailable SUCCESS",Toast.LENGTH_LONG).show();
		  }else{
		   GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
		  }
		  
		 }
	
}
