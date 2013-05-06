package winlab.findplaces2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream.PutField;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ResponseCache;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class JSONstuff extends Activity{
	
	ListView lv;
	JSONObject jsonObject;
	ArrayAdapter<String> ss;
	ProgressBar loading;
	Context ctx;
	String apiKey = "AIzaSyA6c2ZSB1JlobFZfzyMWzdjY6ObxL9_qkY";
	static ArrayList<PlaceLocations> places;
	ArrayList<JSONObject> placesJSONArray;
	ArrayList<ArrayList> routes;
	Boolean wait = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.json_stuff_layout);
		ctx = this;
		lv = (ListView) findViewById(R.id.results_listview);
		loading = (ProgressBar) findViewById(R.id.progressBar);
		routes = new ArrayList<ArrayList>(3);		
		placesJSONArray = new ArrayList<JSONObject>();
		places = new ArrayList<PlaceLocations>();
		
		lv.setVisibility(0x00000004);
		loading.setVisibility(0x00000000);
		
		String[] stringUrl = new String[5];
		//gets driving directions from home to work
		String home = getHomeAddress();
		for(int i=0; i < home.length(); i++){
			if(Character.isSpace(home.charAt(i))){
				String temp = home.substring(i+1, home.length());
				home = home.substring(0, i);
				home = home + "+" + temp;
			}
		}
		String work = getWorkAddress();
		for(int i=0; i < work.length(); i++){
			if(Character.isSpace(work.charAt(i))){
				String temp = work.substring(i+1, work.length());
				work = work.substring(0, i);
				work = work + "+" + temp;
			}
		}
		if(home.equals("") || work.equals("")){
			showToast("Home or work address \n cannot be empty. \n" +
					"Please go to settings \n and enter an address.");
		}

//		for(int i = 0; i < stringUrl.length; i++){
//			try {
//				urlArray[i] = new URL(stringUrl[i]);
//			} catch (MalformedURLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" +
						home +
						"&destination=" +
						work +
						"&sensor=false";
		URL[] urlArray = new URL[1];
		try {
			urlArray[0] = new URL(url);
		} catch (MalformedURLException e) {			
			e.printStackTrace();
		}
		//Log.e("about to do jsonexecute", "going in");
		new getJSONDirections().execute(urlArray);
		//Log.e("just did jsonexecute", "came out");
		
		
	}
	
	/**
	 * returns a string that is the home address
	 * @return
	 */
	private String getHomeAddress(){
		SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);		
		try{
			Cursor c = db.rawQuery("SELECT home FROM homeAddress", null);
			c.moveToFirst();
			String homeAddress = c.getString(0);
			db.close();
			return homeAddress;
		}catch(Exception e){
			Log.e("get home address", "not in db");
			return "";
		}		
	}
	
	/**
	 * returns a string that is the work address
	 * @return
	 */
	private String getWorkAddress(){
		SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);		
		try{
			Cursor c = db.rawQuery("SELECT work FROM workAddress", null);
			c.moveToFirst();
			String workAddress = c.getString(0);
			db.close();
			return workAddress;
		}catch(Exception e){
			Log.e("get work address", "not in db");
			return "";
		}		
	}
	
	
	
	private class getJSONDirections extends AsyncTask<URL, Integer, ArrayList<PlaceLocations>> {
		protected ArrayList<PlaceLocations> doInBackground(URL... urls) {
			String[] jsonOutput = new String[urls.length];
			//Log.e("entered doinbackground", "about to do for");
			Log.e("tag", urls[0].toString());
			for(int i=0; i<urls.length; i++){
				HttpURLConnection httpconn;
				StringBuilder response = new StringBuilder();
				try {
					httpconn = (HttpURLConnection)urls[i].openConnection();	         
					if(httpconn.getResponseCode() == HttpURLConnection.HTTP_OK){	        	 
						try {
							BufferedReader input = new BufferedReader(new InputStreamReader(httpconn.getInputStream()));
							String strLine = null;
							while((strLine = input.readLine()) != null){
								response.append(strLine);
							}
							input.close();
						}catch (IOException e) {
							// 	TODO Auto-generated catch block
							e.printStackTrace();
		        		}
					}
		        } catch (IOException e) {
		        	// TODO Auto-generated catch block
		        	e.printStackTrace();
	        	}
				//Log.e("tag", "message");
				jsonOutput[i] = response.toString();
				//response.delete(0, response.length()-1);
			}
			
			
			
			//showToast("past the second loge");
			ArrayList<String> adapterString = new ArrayList<String>(jsonOutput.length);
			for(int i = 0; i < jsonOutput.length; i++){
				try {
					jsonObject = new JSONObject(jsonOutput[i]);
					JSONArray  routesArray = jsonObject.getJSONArray("routes");
					JSONObject route = routesArray.getJSONObject(0);
					JSONArray legs = route.getJSONArray("legs");
					JSONObject leg = legs.getJSONObject(0);
					JSONArray steps = leg.getJSONArray("steps");
					LatLng prev = null;
					int dist = 400;
					Log.e("steps length", "length is: " + steps.length());
					for(int j=0; j < steps.length(); j++){
						JSONObject step = steps.getJSONObject(j);
						JSONObject endLocation = step.getJSONObject("end_location");						
						double lat = endLocation.getDouble("lat");
						double lng = endLocation.getDouble("lng");
						//new thread
						// send it lat and lng
						//search for places near it
						//places api calls
						//
						
						LatLng ll = new LatLng(lat, lng);
						//send LL to new thread
						//checks if ll 						
						if(j>0){
							Location l1 = new Location("");
							l1.setLatitude(ll.latitude);
							l1.setLongitude(ll.longitude);
							
							Location l2 = new Location("");
							l2.setLatitude(prev.latitude);
							l2.setLongitude(prev.longitude);
							
							if(l1.distanceTo(l2) <= dist){
								dist = dist/2;
								continue;
							}
							dist = 1609;
							//JSONObjects of places near LL
							//ArrayList<JSONObject> placesJSONArray; //= 
							ReturnHelper rh = getPlacesArray(ll);
							placesJSONArray = rh.getJSONArray();
							Log.e("placesJSONARRAY", placesJSONArray.toString());
							
							try{
						    	 for(int k = 0; k<placesJSONArray.size(); k++){
									Log.e("for", "iteration " + k);
									JSONObject obj =  placesJSONArray.get(k);
										Log.e("obj JSONOBject", obj.toString());
									JSONArray results = obj.getJSONArray("results");
										Log.e("results JSONArray", results.toString());
								
										
									JSONObject place = results.getJSONObject(0);
										Log.e("JSONObject place	", place.toString());
									JSONObject geometry = place.getJSONObject("geometry");
										Log.e("JSONObject geometry", geometry.toString());
									JSONObject location = geometry.getJSONObject("location");
										Log.e("JSONOBject location", location.toString());
									String name = place.getString("name");
										Log.e("String name", name);
									String address = place.getString("vicinity");
										Log.e("String address", address);
										
									double placeLat = location.getDouble("lat");
										Log.e("placeLat", "place lat is " + placeLat);
									double placeLng = location.getDouble("lng");
										Log.e("placeLng", "place lng is " + placeLng);
										
//									JSONArray types = place.getJSONArray("types");
//									ArrayList<String> typesAL = new ArrayList<String>();
//									for(int l=0; l<types.length(); l++){
//										typesAL.add(types.getString(l));
//									}
									PlaceLocations pL = new PlaceLocations(name, placeLat, placeLng, rh.getType(), address);
									Log.e("placesLocations name", name);
									if(!places.contains(pL)){
										places.add(pL);
										Log.e("places", places.get(0).getName());
									}

									//second place
									place = results.getJSONObject(1);
									Log.e("JSONObject place	", place.toString());
									geometry = place.getJSONObject("geometry");
										Log.e("JSONObject geometry", geometry.toString());
									location = geometry.getJSONObject("location");
										Log.e("JSONOBject location", location.toString());
									name = place.getString("name");
										Log.e("String name", name);
									address = place.getString("vicinity");
										Log.e("String address", address);
										
									placeLat = location.getDouble("lat");
										Log.e("placeLat", "place lat is " + placeLat);
									placeLng = location.getDouble("lng");
										Log.e("placeLng", "place lng is " + placeLng);
										
//									types = place.getJSONArray("types");
//									typesAL = new ArrayList<String>();
//									for(int l=0; l<types.length(); l++){
//										typesAL.add(types.getString(l));
//									}
									PlaceLocations pL2 = new PlaceLocations(name, placeLat, placeLng, rh.getType(), address);
									Log.e("placesLocations name", name);
									if(!places.contains(pL2)){
										places.add(pL2);
										Log.e("places", places.get(1).getName());
									}
									
									//third place
									place = results.getJSONObject(1);
									Log.e("JSONObject place	", place.toString());
									geometry = place.getJSONObject("geometry");
										Log.e("JSONObject geometry", geometry.toString());
									location = geometry.getJSONObject("location");
										Log.e("JSONOBject location", location.toString());
									name = place.getString("name");
										Log.e("String name", name);
									address = place.getString("vicinity");
										Log.e("String address", address);
										
									placeLat = location.getDouble("lat");
										Log.e("placeLat", "place lat is " + placeLat);
									placeLng = location.getDouble("lng");
										Log.e("placeLng", "place lng is " + placeLng);
										
//									types = place.getJSONArray("types");
//									typesAL = new ArrayList<String>();
//									for(int l=0; l<types.length(); l++){
//										typesAL.add(types.getString(l));
//									}
									PlaceLocations pL3 = new PlaceLocations(name, placeLat, placeLng, rh.getType(), address);
									Log.e("placesLocations name", name);
									if(!places.contains(pL3)){
										places.add(pL3);
										Log.e("places", places.get(2).getName());
									}
								}
					    	 }catch(JSONException e){
					    		 e.printStackTrace();
					    	 }
							
							
							
							/*
							 * get closest one and create a 
							 * PlaceLocations object
							 * insert in places 
							 */
							
							
						}
						prev = ll;
						//Log.e("for loop j in thread", "lat is :"+lat);
					} 
					
					
//					JSONObject durationObj = leg.getJSONObject("duration");					
//					String duration = durationObj.getString("text");
//					Log.e("in for loop", duration);
//					adapterString.add(duration);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return places;
		}
		
		protected void onProgressUpdate(Integer... progress) {
			
		}

		protected void onPostExecute(ArrayList<PlaceLocations> result) {	
			//Log.e("poste xecute", "doing the right thing");
			//Log.e("poste xecute", result[0]);
			
			//ss = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, adapterString);			
			//lv.setAdapter(ss);
			loading.setVisibility(0x00000004);		
			Intent i = new Intent(ctx, RoutingMaps.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
			//lv.setVisibility(0x00000000);	
			
			
			
	    }
		/**
		 * give this a LatLng object and it will
		 * get the return an ArrayList of JSONObjects
		 * which includes that are the distances of 
		 * all the places in the database from the 
		 * LatLng point.
		 * 
		 * @param ll LatLng 
		 * @return ArrayList
		 */
		private ReturnHelper getPlacesArray(LatLng ll) {
			ArrayList<JSONObject> jsonArray = new ArrayList<JSONObject>();
			HttpURLConnection httpconn;
			ReturnHelper rh = new ReturnHelper();
			Cursor c = getPlaces();
			c.moveToFirst();
			while(!c.isAfterLast()){				
				Log.e("Cursor is ", c.getString(0));
				String type = c.getString(0);
				String temp = "";
				for(int i=0; i<type.length();i++){
					if(Character.isSpace(type.charAt(i))){
						temp = temp+"_";
					}
					else{
						temp = temp+type.charAt(i);
					}
				}
				type = temp;
				rh.setType(type);
				String stringUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
						"json?location="+ ll.latitude +","+ ll.longitude +
						"&rankby=distance&types=" +
						type + "&" +
						"sensor=false&" +
						"key=" + apiKey;
				URL url;
				try {
					url = new URL(stringUrl);				
					StringBuilder response = new StringBuilder();
					try {
						httpconn = (HttpURLConnection)url.openConnection();	         
						if(httpconn.getResponseCode() == HttpURLConnection.HTTP_OK){	        	 
							try {
								BufferedReader input = new BufferedReader(new InputStreamReader(httpconn.getInputStream()));
								String strLine = null;
								while((strLine = input.readLine()) != null){
									response.append(strLine);
								}
								input.close();
							}catch (IOException e) {
								e.printStackTrace();
			        		}
						}
			        } catch (IOException e) {
			        	e.printStackTrace();
		        	}
					try {
					//	Log.e("JSONObject", response.toString());
						JSONObject json = new JSONObject(response.toString());							
						jsonArray.add(json);
					//	placesJSONArray.add(json);
					//	Log.e("getting json", "size is " +jsonArray.size());
						//Log.e("getting json", "size is " +placesJSONArray.size());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				c.moveToNext();
			}
			
			rh.setArray(jsonArray);
			return rh;
			
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

		
	}
	
//	/**
//	 * should check places around the latlng sent to it
//	 * latlng should be sent only if the one before was 
//	 * less than a mile away. 
//	 * 
//	 * searches for all places (passed from listview in mainscreen.java)
//	 * 
//	 * create n ArrayLists (N = number of things passed from listview)
//	 * 
//	 * make each ArrayList size = the size of the steps JSONArray so that
//	 * each one can hold at most as many as every step (only going to store
//	 * closest one to each step)
//	 * 
//	 * 
//	 * @author Aldo
//	 *
//	 */
//	private class checkPlacesAround extends AsyncTask<LatLng, Integer, ArrayList<JSONObject>> {
//	     protected ArrayList<JSONObject> doInBackground(LatLng... ll) {
//	    	 		 	    	
//	    	 return ar;
//	     }
//
//	     protected void onProgressUpdate(Integer... progress) {
//	     }
//
//	     protected void onPostExecute(ArrayList<JSONObject> result) {
//	    	 Log.e("jsonsize:", "results is " +result.size());
//	    	 placesJSONArray.addAll(result);	    	 
//	    	 Log.e("jsonsize:", "jsonarraysize is " +placesJSONArray.size());					    	 
//	    	 
//	     }
//	 }
	
	public void showToast(String toast){
		Toast.makeText(ctx, toast, Toast.LENGTH_SHORT).show();
	}
}
