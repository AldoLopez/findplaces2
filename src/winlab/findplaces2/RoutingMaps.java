package winlab.findplaces2;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.GeolocationPermissions;
import android.widget.Toast;
public class RoutingMaps extends Activity{	
	
	private GoogleMap mMap;
	public Context ctx = this;
	public Geocoder geo;
	public ArrayList<ArrayList<LatLng>> places;
	public ArrayList<ArrayList<Polyline>> polyLines;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_viewing);
        ArrayList<PlaceLocations> placeLocations = new ArrayList<PlaceLocations>();
        placeLocations.addAll(JSONstuff.places);
        
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        geo = new Geocoder(ctx);
        places = new ArrayList<ArrayList<LatLng>>();
        if(mMap != null){
        	mMap.setMyLocationEnabled(true);
        	mMap.isMyLocationEnabled();
        	mMap.getMyLocation();
        	ArrayList<Address> address = new ArrayList<Address>();
        	double lat, lon;
        	String home = getHomeAddress();
        	String work = getWorkAddress();
        	Address h = getAddress(home);
        	LatLng hLat = new LatLng(h.getLatitude(), h.getLongitude());
        	Address w = getAddress(work);
        	LatLng wLat = new LatLng(w.getLatitude(), w.getLongitude());
        	address.add(h);
        	address.add(w);

        	//add home marker
        	mMap.addMarker(new MarkerOptions().position(new LatLng(h.getLatitude(), h.getLongitude()))
        						.title("home")
        						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        	//add work marker
        	mMap.addMarker(new MarkerOptions().position(new LatLng(w.getLatitude(), w.getLongitude()))
					.title("work")
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        	

        	if(placeLocations == null){
        		showToast("no places to go");
        		Intent intent = new Intent(ctx, MainScreen.class);
        		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        		startActivity(intent);
        	}
        	for(int i = 0; i < placeLocations.size(); i++){
        		mMap.addMarker(new MarkerOptions().position(placeLocations.get(i).getLatLngObject()).title(placeLocations.get(i).getName())
        								.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        	}
        	
        	
        	
        	ArrayList<PlaceLocations> pl = new ArrayList<PlaceLocations>();
        	pl.addAll(placeLocations);
        	new drawTheLines().execute(pl);
        	
//        	get first task
//        	Cursor c = getPlaces();
//        	if(c == null){
//        		showToast("no places to go");
//        		Intent i = new Intent(ctx, MainScreen.class);
//        		startActivity(i);
//        	}
//        	c.moveToFirst();
//        	int arrayCount = 0;
//        	while(!c.isAfterLast()){
//        		List<Address> place = getAddressList(c.getString(0), h, w);        		
//        		places.add(new ArrayList<LatLng>());
//        		for(int i=0; i<place.size(); i++){
//        			if(c.getString(0) == ""){
//        				continue;
//        			}
//        			LatLng ll = new LatLng(place.get(i).getLatitude(), place.get(i).getLongitude());
//        			places.get(arrayCount).add(ll); 
//        			mMap.addMarker(new MarkerOptions().position(ll).title(c.getString(0))
//        								.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//        			/*Polyline line = mMap.addPolyline(new PolylineOptions()
//        							.add(hLat, ll).geodesic(true));
//        			Polyline lin2 = mMap.addPolyline(new PolylineOptions()
//									.add(ll, wLat).geodesic(false));*/
//        		}
//        		arrayCount++;
//        		c.moveToNext();
//        	}
        }
        
    }
	
	private class drawTheLines extends AsyncTask<ArrayList<PlaceLocations>, Integer, ArrayList<PlaceLocations>>{
		protected ArrayList<PlaceLocations> doInBackground(ArrayList<PlaceLocations>... pl) {
			//array list of lists of places in order they will be visited
			ArrayList<ArrayList<PlaceLocations>> listOfPlaces = new ArrayList<ArrayList<PlaceLocations>>();			 
			ArrayList<ArrayList<String>> parallel = new ArrayList<ArrayList<String>>();
			int types = 0;
			ArrayList<String> tTypes = new ArrayList<String>();
			for(int i=0; i < pl[0].size(); i++){
				if(!tTypes.contains(pl[0].get(i).getType())){
					types++;
					tTypes.add(pl[0].get(i).getType());
				}
			}
			int iterations = (int) Math.pow(2, types);
			
			int ofEach = pl[0].size()/types;
			
			tTypes.clear();
			ArrayList<PlaceLocations> firstNodes = new ArrayList<PlaceLocations>();
			ArrayList<String> parallelsList = new ArrayList<String>();
			
			for(int i=0; i<ofEach; i++){
				for(int j=0; j<pl[0].size(); j++){
					if(pl[0].get(j).getType().equals(pl[0].get(0).getType())){
						firstNodes.add(pl[0].get(j));
						parallelsList.add(pl[0].get(j).getType());
						listOfPlaces.add(firstNodes);		
						parallel.add(parallelsList);
					}
					parallelsList.clear();
					firstNodes.clear();
				}
			}
			
			for(int i=0; i<listOfPlaces.size(); i++){
				for(int k=0; k<pl[0].size(); k++){
					if(!parallel.get(i).contains(pl[0].get(k).getType())){
						listOfPlaces.get(i).add(pl[0].get(k));
						parallel.get(i).add(pl[0].get(k).getType());
					}
				}
			}
			Log.e("size of listofplaces", "size is: " + listOfPlaces.size());
			Log.e("size of listofplaces[0]", "size is: " + listOfPlaces.get(1).get(0).getName());
			return null;
		}
}
	
	/**
	 * returns a Cursor that contains the places
	 * @return
	 */
	private Cursor getPlaces(){
		SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
		try{
			Cursor c = db.rawQuery("SELECT name FROM placesToGoArray", null);
			c.moveToFirst();
			return c;
		}
		catch(android.database.sqlite.SQLiteException e){
			return null;
		}
	}
	
	/**
	 * returns work address
	 * @return
	 */
	private String getWorkAddress() {
		SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
		Cursor c = db.rawQuery("SELECT work FROM workAddress", null);
		c.moveToFirst();
		db.close();
		return c.getString(0);
	}

	/**
	 * returns home address
	 * @return
	 */
	private String getHomeAddress() {
		SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
		Cursor c = db.rawQuery("SELECT home FROM homeAddress", null);
		c.moveToFirst();
		db.close();
		return c.getString(0);
	}

	/**
	 * takes in a location name and returns the latitude and longitude
	 * as an address
	 * @param location
	 * @return
	 */
	public Address getAddress(String location){
		List<Address> address;
		Geocoder geo = new Geocoder(this);
		try{
			address = geo.getFromLocationName(location, 5);
			if(address == null){
				return null;
			}
			Address loc = address.get(0);
			loc.getLatitude();
			loc.getLongitude();
			return loc;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;		
	}
	
	/**
	 * takes in the place to go, the home address, and the work
	 * address. It returns a List<Address> that contains a list 
	 * of up to 5 locations that match the place that it was 
	 * given.
	 * @param location
	 * @param home
	 * @param work
	 * @return
	 */
	public List<Address> getAddressList(String location, Address home, Address work){
		List<Address> address;
		Geocoder geo = new Geocoder(this);
		//these will be bounding box for search
		double lowerLeftLat = 0, lowerLeftLong = 0, upperRightLat = 0, upperRightLong = 0;
		if(home.getLatitude() >= work.getLatitude()){
			upperRightLat = home.getLatitude();
			lowerLeftLat = work.getLatitude();
		}else{
			upperRightLat = work.getLatitude();
			lowerLeftLat = home.getLatitude();
		}
		if(home.getLongitude() <= work.getLongitude()){
			upperRightLong = home.getLongitude();
			lowerLeftLong = work.getLongitude();
		}else{
			upperRightLong = work.getLongitude();
			lowerLeftLong = home.getLongitude();
		}
		
		try{
			address = geo.getFromLocationName(location, 5,
												work.getLatitude(),	work.getLongitude(),
												home.getLatitude(),home.getLongitude());
			
			if(address == null){
				return null;
			}
			return address;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;		
	}
	
	void showToast(String s){
		Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
	}
}
