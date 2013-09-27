package br.com.onibussantos;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ketan.slidingexample.R;
import com.slidingmenu.lib.SlidingMenu;

public class Principal extends SherlockFragmentActivity implements
		Menufragment.MenuClickInterFace {

	private GoogleMap myMap;
	private int mapType = GoogleMap.MAP_TYPE_NORMAL;
	final int RQS_GooglePlayServices = 1;

	private String[] bairroSpinner;

	private SlidingMenu menu;
	private Spinner sp1;

	private static final String LOG_TAG = Principal.class.getSimpleName();

	private static String STATE_TO_STORE = "state_to_store";

	// instance variables for Marker icon drawable resources
	private int userIcon, foodIcon, drinkIcon, shopIcon, otherIcon;

	// location manager
	private LocationManager locMan;

	// user marker
	private Marker userMarker;

	// places of interest
	private Marker[] placeMarkers;
	// max
	private final int MAX_PLACES = 20;// most returned from google
	// marker options
	private MarkerOptions[] places;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(LOG_TAG, "onCreate: savedInstanceState = "
				+ (savedInstanceState == null ? "NULL" : "Not NULL"));
		setContentView(R.layout.activity_main);


		// exibe os mapas
		FragmentManager myFragmentManager = getSupportFragmentManager();
		SupportMapFragment mySupportMapFragment = (SupportMapFragment) myFragmentManager
				.findFragmentById(R.id.map);

		myMap = mySupportMapFragment.getMap();

		myMap.getUiSettings().setZoomControlsEnabled(true);
		// myMap.setBuiltInZoomControls(true);

		myMap.setMyLocationEnabled(true);

		moveMapToMyLocation();

		// sliding menu
		ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		ab.setDisplayShowTitleEnabled(true);
		ab.setDisplayShowCustomEnabled(true);
		ab.setTitle("Rota de Onibus");
		ab.setIcon(R.drawable.bus);

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

		// get drawable IDs
		userIcon = R.drawable.yellow_point;
		foodIcon = R.drawable.red_point;
		drinkIcon = R.drawable.blue_point;
		shopIcon = R.drawable.green_point;
		otherIcon = R.drawable.purple_point;

		// find out if we already have it
		if (myMap == null) {
			// get the map
			SupportMapFragment myMapFragment = (SupportMapFragment) myFragmentManager
					.findFragmentById(R.id.map);
			// theMap =
			// ((MapFragment)getFragmentManager().findFragmentById(R.id.the_map)).getMap();
			// check in case map/ Google Play services not available
			if (myMap != null) {
				// ok - proceed
				myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				// create marker array
				placeMarkers = new Marker[MAX_PLACES];
				// update location
				updatePlaces();
			}

		}

	}

	// location listener functions

	@Override
	public void onLocationChanged(Location location) {
		Log.v("MyMapActivity", "location changed");
		updatePlaces();
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.v("MyMapActivity", "provider disabled");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.v("MyMapActivity", "provider enabled");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.v("MyMapActivity", "status changed");
	}

	private void updatePlaces() {
		// get location manager
		locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// get last location
		Location lastLoc = locMan
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		double lat = lastLoc.getLatitude();
		double lng = lastLoc.getLongitude();
		// create LatLng
		LatLng lastLatLng = new LatLng(lat, lng);

		// remove any existing marker
		if (userMarker != null)
			userMarker.remove();
		// create and set marker properties
		userMarker = myMap.addMarker(new MarkerOptions().position(lastLatLng)
				.title("You are here")
				.icon(BitmapDescriptorFactory.fromResource(userIcon))
				.snippet("Your last recorded location"));
		// move to location
		myMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 3000,
				null);

		// build places query string
		String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/"
				+ "json?location="
				+ lat
				+ ","
				+ lng
				+ "&radius=1000&sensor=true"
				+ "&types=food|bar|store|museum|art_gallery"
				+ "&key=AIzaSyAXqGkGEdPsw0eclR3vdtKt9FfxAAS6Mwg";// ADD KEY

		// execute query
		new GetPlaces().execute(placesSearchStr);

		locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000,
				100, (LocationListener) this);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		Log.d(LOG_TAG, "onRestoreInstanceState: savedInstanceState = "
				+ (savedInstanceState == null ? "NULL" : "Not NULL"));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(STATE_TO_STORE, 5); // store some int

		Log.d(LOG_TAG, "onSaveInstanceState bundle: " + outState.toString());
	}

	private void moveMapToMyLocation() {

		myMap.setMyLocationEnabled(true);

		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		Criteria crit = new Criteria();

		String provider = locationManager.getBestProvider(crit, true);

		Location myLocation = locationManager.getLastKnownLocation(provider);

		myMap.setMapType(mapType);

		double latitude = myLocation.getLatitude();
		double longitude = myLocation.getLongitude();

		LatLng latLng = new LatLng(latitude, longitude);

		myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

		myMap.animateCamera(CameraUpdateFactory.zoomBy(15));
		myMap.addMarker(new MarkerOptions().position(
				new LatLng(latitude, longitude)).title("Você está aqui!"));

	}

	// metodo para ir a localizacao atual ao clicar no botao gps
	@Override
	public void onMyLocationChange(Location location) {

		// Obtendo latitude do local atual
		double latitude = location.getLatitude();

		// Obtendo longitude da localização atual
		double longitude = location.getLongitude();

		// Criando um objeto LatLng para o local atual
		LatLng latLng = new LatLng(latitude, longitude);

		// Mostrando a localização atual no Google Map
		myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

		// Zoom no Google Map
		myMap.animateCamera(CameraUpdateFactory.zoomTo(15));

		// Setting latitude and longitude in the TextView tv_location

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// super.onOptionsItemSelected(item);

		switch (item.getItemId()) {

		case android.R.id.home:
			menu.toggle();

			return true;
			// break;
		case R.id.mapa_:

			// item.setIcon(R.drawable.ic_location_web_site);
			Toast.makeText(getBaseContext(), "Clicou no mapa",
					Toast.LENGTH_LONG).show();
			mapType = GoogleMap.MAP_TYPE_NORMAL;
			// item.findItem(R.id.mapa).setIcon(R.drawable.ic_location_web_site);

			break;
		case R.id.satelite_:
			Toast.makeText(getBaseContext(), "Clicou no satelite",
					Toast.LENGTH_LONG).show();
			mapType = GoogleMap.MAP_TYPE_SATELLITE;
			// menu.setId(R.drawable.ic_map);
			// item.setIcon(R.drawable.ic_map);
			// menu.getResources().getXml(R.id.mapa);

			break;

		}
		myMap.setMapType(mapType);
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListitemClick(String item) {
		Log.i("ERRO", "ONLIST" + item);
		Toast.makeText(getBaseContext(), "Clicou " + item, Toast.LENGTH_LONG)
				.show();
		
		myMap.addMarker(new MarkerOptions().position(
				new LatLng(-23.93847, -46.383902)).title("Agência Rádio Clube"));
		myMap.addMarker(new MarkerOptions().position(
				new LatLng(-23.968649,-46.332951)).title("Agência SHOPPING BALNEÁRIO"));
		myMap.addMarker(new MarkerOptions().position(
				new LatLng(-23.935263,-46.332771)).title("Agência Rodoviária"));
		myMap.addMarker(new MarkerOptions().position(
				new LatLng(-23.976913,-46.310815)).title("Agência SUPERMECADO CARREFOUR"));
		// text.setText(item);
		
	}

	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);

		// LayoutInflater layout =
		// (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// View v = layout.inflate(android.R.layout.simple_list_item_checked,
		// null);

		// menu.findItem(R.menu.activity_main).setIcon(R.drawable.people);

		return true;

	}

	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());

		if (resultCode == ConnectionResult.SUCCESS) {
			Toast.makeText(getApplicationContext(),
					"isGooglePlayServicesAvailable SUCCESS", Toast.LENGTH_LONG)
					.show();
		} else {
			GooglePlayServicesUtil.getErrorDialog(resultCode, this,
					RQS_GooglePlayServices);
		}

	}

	private class GetPlaces extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... placesURL) {
			// Busca lugares

			// busca resultado de string
			StringBuilder placesBuilder = new StringBuilder();
			// Busca o processo de string(s)
			for (String placeSearchURL : placesURL) {
				HttpClient placesClient = new DefaultHttpClient();
				try {
					// Tenta buscar os dados

					// HTTP recebe a sequencia de URL string
					HttpGet placesGet = new HttpGet(placeSearchURL);
					// executa GET com Cliente - retornando a resposta
					HttpResponse placesResponse = placesClient
							.execute(placesGet);
					// verifica o estado da resposta
					StatusLine placeSearchStatus = placesResponse
							.getStatusLine();
					// So continua se a resposta for OK
					if (placeSearchStatus.getStatusCode() == 200) {
						// pega a resposta
						HttpEntity placesEntity = placesResponse.getEntity();
						// pega a configuracao do fluxo de entrada
						InputStream placesContent = placesEntity.getContent();
						// cria o leitor
						InputStreamReader placesInput = new InputStreamReader(
								placesContent);
						// utiliza o leitor para processar o Buffer
						BufferedReader placesReader = new BufferedReader(
								placesInput);
						// ler uma linha de cada vez, e anexa a string builder
						String lineIn;
						while ((lineIn = placesReader.readLine()) != null) {
							placesBuilder.append(lineIn);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return placesBuilder.toString();
		}

		// processo de recuperacao do doInBackground
		protected void onPostExecute(String result) {
			// parse place data returned from Google Places
			// remove os marcadores existentes
			if (placeMarkers != null) {
				for (int pm = 0; pm < placeMarkers.length; pm++) {
					
					if (placeMarkers[pm] != null)
						placeMarkers[pm].remove();
				}
			}
			try {
				// parse JSON

				// cria JSONObject, passa a string que retorna doInBackground
				JSONObject resultObject = new JSONObject(result);
				// pega os "resultados" do array
				JSONArray placesArray = resultObject.getJSONArray("results");
				// marca os lugares que retornou
				places = new MarkerOptions[placesArray.length()];
				// loop através dos lugares
				for (int p = 0; p < placesArray.length(); p++) {
					// analisa cada lugar
					// se os valores faltarem não será mostrado no marcador
					boolean missingValue = false;
					LatLng placeLL = null;
					String placeName = "";
					String vicinity = "";
					int currIcon = otherIcon;
					try {
						// tenta recuperar os valores de dados locais
						missingValue = false;
						// obtem lugar neste indice
						JSONObject placeObject = placesArray.getJSONObject(p);
						// obtem a secao local
						JSONObject loc = placeObject.getJSONObject("geometry")
								.getJSONObject("location");
						// ler a lat lng
						placeLL = new LatLng(Double.valueOf(loc
								.getString("lat")), Double.valueOf(loc
								.getString("lng")));
						// obtem os tipos
						JSONArray types = placeObject.getJSONArray("types");
						// loop através dos tipos
						for (int t = 0; t < types.length(); t++) {
							//que tipo é
							String thisType = types.get(t).toString();
							// verifica se há tipos especificos dos conjuntos de icones
							if (thisType.contains("food")) {
								currIcon = foodIcon;
								break;
							} else if (thisType.contains("bar")) {
								currIcon = drinkIcon;
								break;
							} else if (thisType.contains("store")) {
								currIcon = shopIcon;
								break;
							}
						}
						// ao redor
						vicinity = placeObject.getString("vicinity");
						// nome
						placeName = placeObject.getString("name");
					} catch (JSONException jse) {
						Log.v("PLACES", "missing value");
						missingValue = true;
						jse.printStackTrace();
					}
					// se os valores faltarem não irá exibir
					if (missingValue)
						places[p] = null;
					else
						places[p] = new MarkerOptions()
								.position(placeLL)
								.title(placeName)
								.icon(BitmapDescriptorFactory
										.fromResource(currIcon))
								.snippet(vicinity);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (places != null && placeMarkers != null) {
				for (int p = 0; p < places.length && p < placeMarkers.length; p++) {
					// se os valores faltarem não irá exibir
					if (places[p] != null)
						placeMarkers[p] = myMap.addMarker(places[p]);
				}
			}

		}
	}
}
