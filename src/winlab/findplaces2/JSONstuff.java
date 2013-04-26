package winlab.findplaces2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
	ArrayAdapter<String> ss;
	ProgressBar loading;
	Context ctx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.json_stuff_layout);
		ctx = this;
		lv = (ListView) findViewById(R.id.results_listview);
		loading = (ProgressBar) findViewById(R.id.progressBar);
		
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
		Log.e("about to do jsonexecute", "going in");
		new getJSONDirections().execute(urlArray);
		Log.e("just did jsonexecute", "came out");
		
		
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
			Log.e("get home address", "not in db");
			return "";
		}		
	}
	
	
	private class getJSONDirections extends AsyncTask<URL, Integer, String[]> {
		protected String[] doInBackground(URL... urls) {
			String[] jsonOutput = new String[urls.length];
			Log.e("entered doinbackground", "about to do for");
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
				Log.e("tag", "message");
				jsonOutput[i] = response.toString();
				//response.delete(0, response.length()-1);
			}
			return jsonOutput;
		}
		
		protected void onProgressUpdate(Integer... progress) {
			
		}

		protected void onPostExecute(String[] result) {	
			Log.e("poste xecute", "doing the right thing");
			Log.e("poste xecute", result[0]);
			showToast("past the second loge");
			ArrayList<String> adapterString = new ArrayList<String>(result.length);
			for(int i = 0; i < result.length; i++){
				try {
					JSONObject jsonObject = new JSONObject(result[i]);
					JSONArray  routesArray = jsonObject.getJSONArray("routes");
					JSONObject route = routesArray.getJSONObject(0);
					JSONArray legs = route.getJSONArray("legs");
					JSONObject leg = legs.getJSONObject(0);
					
					JSONArray steps = leg.getJSONArray("steps");
					LatLng prev = null;
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
							
							if(l1.distanceTo(l2) < 1609){
								continue;
							}
						}
						
						new checkPlacesAround().execute(ll);
						
						prev = ll;
						Log.e("for loop j in thread", "lat is :"+lat);
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
			ss = new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, adapterString);			
			lv.setAdapter(ss);
			loading.setVisibility(0x00000004);
			lv.setVisibility(0x00000000);
	    }
	}
	
	private class checkPlacesAround extends AsyncTask<LatLng, Integer, LatLng> {
	     protected LatLng doInBackground(LatLng... ll) {
	         
	    	 return null;
	     }

	     protected void onProgressUpdate(Integer... progress) {
	     }

	     protected void onPostExecute(LatLng result) {
	     }


	 }
	
	public void showToast(String toast){
		Toast.makeText(ctx, toast, Toast.LENGTH_SHORT).show();
	}
}
