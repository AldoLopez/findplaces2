package winlab.findplaces2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


//import com.find.my.places.ActivityListPlaces.FancyAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterViewFlipper;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainScreen extends ListActivity{

	//private FancyAdapter mFancyAdapter;
	ProgressDialog progressDialog;
	private ListView myListView;
	List<String> placesList;
	Button newPlaceButton;
	Button route;
	Context ctx = this;
	AutoCompleteTextView insertStopOff;
	private LinearLayout l;
	private int prevId;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*keyboard doesnt show up*/ 
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_main_screen);	
		route = (Button) findViewById(R.id.route);
		newPlaceButton = (Button) findViewById(R.id.new_place_ok);
		insertStopOff = (AutoCompleteTextView) findViewById(R.id.new_place_edittext);
		myListView =  getListView();//(ListView) findViewById(android.R.id.list);		
		//insertStopOff = (AutoCompleteTextView) findViewById(R.id.autocomp);		
		
		int accepted = getAcceptance();
		if(accepted != 1){
			firstTime();
			final Dialog dialog = new Dialog(ctx);
			dialog.setContentView(R.layout.set_home_work_address);
			dialog.setCanceledOnTouchOutside(false);
			Button set = (Button) dialog.findViewById(R.id.set_addresses);
			final EditText home = (EditText) dialog.findViewById(R.id.home_address_setting);
			final EditText work = (EditText) dialog.findViewById(R.id.work_address_setting);
			set.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					String homeAddress = home.getText().toString();
					String workAddress = work.getText().toString();
					setHomeAddress(homeAddress);
					setWorkAddress(workAddress);
					dialog.cancel();
				}
			});
			dialog.show();
		}	
		/*
		 * Here is where the code for the autocompletetextview will be done.
		 * and ArrayAdapter will be created to store the values stored in the
		 * string array located in res/values/strings
		 */		
		/*
		 * places autocomplete adapter would be the one at bottom of this
		 */
		//ArrayAdapter<CharSequence> stoppOffAdapter = ArrayAdapter.createFromResource(ctx, R.array.PlaceTypes, android.R.layout.simple_dropdown_item_1line);		
		//insertStopOff.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.activity_main_screen));		
		final ArrayAdapter<CharSequence> stringAdapter  = ArrayAdapter.createFromResource(this, R.array.PlaceTypes, android.R.layout.simple_spinner_dropdown_item);		
		stringAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		insertStopOff.setAdapter(stringAdapter);		


		final String[] hasToBeInHere = getResources().getStringArray(R.array.PlaceTypes); 
		
		
		 /* on click of button the text from the text view will get inserted 
		 * into the listview with the following snip of code
		 */
		final ArrayList<String> textViews = new ArrayList<String>();		
		final ArrayAdapter<String> ss = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, textViews);
		myListView.setAdapter(ss);
		final ArrayList<Integer> tvIds = new ArrayList<Integer>();
		prevId = 0;//have to put ids in db so this is temp
		if(dbHasEntries()){
			
			SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
			Cursor names = db.rawQuery("SELECT name FROM placesToGoArray", null);
		//	Cursor ids = db.rawQuery("SELECT id FROM placesToGoArray", null);
			names.moveToFirst();
		//	ids.moveToFirst();
			while(names.isAfterLast() == false){
				textViews.add(names.getString(0));
		//		tvIds.add(ids.getInt(0));
				names.moveToNext();
		//		ids.moveToNext();
			}

			//myListView.setAdapter(ss);
			
		}
		else{
			prevId = 0;//have to put ids in db so this is temp
		}
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		newPlaceButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				ArrayList<String> inHere = new ArrayList<String>();
				for(int i=0; i<hasToBeInHere.length; i++){
					inHere.add(hasToBeInHere[i]);
				}
				String newPlace = insertStopOff.getText().toString();				
				if(newPlace.equals("")){
					showToast("Please enter a stopoff to add.");			
				}
				else if(!inHere.contains(newPlace)){
					showToast("Please only enter places from dropdown");
				}
				else{				
					
					int currId = prevId + 1;
					//do stuff
					boolean isIn = false;
					SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
//					db.execSQL("CREATE TABLE IF NOT EXISTS placesToGoArray(name varchar(50) PRIMARY KEY," +
//							"id int);");
					if(dbHasEntries()){
						//ArrayAdapter<String> ss = (ArrayAdapter<String>) myListView.getAdapter();
						Cursor c = db.rawQuery("SELECT name FROM placesToGoArray", null);
						c.moveToFirst();
						while(!c.isLast()){
							//showToast(c.getString(0));
							if(c.getString(0).equals(newPlace)){
								isIn = true;
								showToast(newPlace+ " is in the DB");
								break;
							}
							c.moveToNext();
						}
						db.close();
						prevId = currId;										 					
						
						if(!isIn){
							if(insertNewPlace(newPlace, currId)){	
								textViews.add(newPlace);
							}
							//ss.add(newPlace);
							//myListView.setAdapter(ss);						
						}
						insertStopOff.setText("");
					}
					else{
						
						//ss.add(newPlace);
						//myListView.setAdapter(ss);
						if(insertNewPlace(newPlace, currId)){	
							textViews.add(newPlace);
						}
						insertStopOff.setText("");
					}
					
				}
			}

			/**
			 * inserts name of place and id of string
			 * into a db
			 * @param name
			 * @param id
			 */
			private boolean insertNewPlace(String name, int id) {
				SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
				db.execSQL("CREATE TABLE IF NOT EXISTS placesToGoArray(name varchar(50) PRIMARY KEY," +
						"id int);");	
				try{
					db.execSQL("INSERT INTO placesToGoArray(name, id) VALUES (\""+ name +"\", \""+ id +"\");");
				}
				catch(SQLiteConstraintException e){
					showToast(name + " is in db");
					return false;
				}
				db.close();
				return true;				
			}			
		});		
		
		/**
		 * starts map intent and shows routes
		 */
		route.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Intent i = new Intent(ctx, RoutingMaps.class);
				Intent i = new Intent(ctx, JSONstuff.class);
				startActivity(i);
				showToast("route coming here");
			}
		});
		
	//	myListView.setClickable(true);
		myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3){
				final int positionToRemove = position;
				final Object o = myListView.getItemAtPosition(position);
				final Dialog d = new Dialog(ctx);				
				d.setContentView(R.layout.delete_place_to_go);
				d.setCanceledOnTouchOutside(true);
				Button ok = (Button) d.findViewById(R.id.ok_delete_place);
				Button no = (Button) d.findViewById(R.id.cancel_delete_place);
				
				ok.setOnClickListener(new View.OnClickListener() {					
					@Override
					public void onClick(View v) {						
						SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
						Log.e("what is object", o.toString());
						Log.e("what is mysqli call", "DELETE FROM placestogoarray WHERE name = \""+ o.toString() +"\";");
//						Cursor c = db.rawQuery("SELECT name FROM placesToGoArray", null);
//						c.moveToFirst();
//						while(!c.isLast()){
//							Log.e("Cursor is ", c.getString(0));
//							c.moveToNext();
//						}
						db.execSQL("DELETE FROM placestogoarray WHERE name = \""+ o.toString() +"\";");
						//myListView.removeViewAt(positionToRemove);						
						db.close();		
						ArrayAdapter<String> ss = (ArrayAdapter<String>) myListView.getAdapter();
						ss.remove(myListView.getItemAtPosition(positionToRemove).toString());						
						
						d.cancel();						
					}
				});
				no.setOnClickListener(new View.OnClickListener() {					
					@Override
					public void onClick(View v) {
						d.cancel();
					}
				});
				d.show();
			}
		});
	}
	
	/**
	 * returns true if the database with the list of places to go
	 * has any entries in it
	 * else returns false
	 * @return
	 */
	private boolean dbHasEntries(){
		SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
		try{
			Cursor c = db.rawQuery("SELECT name FROM placesToGoArray", null);
			int num = c.getCount();
			db.close();
			if(num < 1){
				return false;
			}
		}
		catch(android.database.sqlite.SQLiteException e){
			return false;
		}
		return true;
	}
	
	/**
	 * sets home address
	 * @param homeAddress
	 */
	
	private void setHomeAddress(String homeAddress){
		SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
		for(int i=0; i < homeAddress.length(); i++){
			if(Character.isSpace(homeAddress.charAt(i))){
				String temp = homeAddress.substring(i+1, homeAddress.length());
				homeAddress = homeAddress.substring(0, i);
				homeAddress = homeAddress + "+" + temp;
			}
		}
		db.execSQL("CREATE TABLE IF NOT EXISTS homeAddress (name VARCHAR PRIMARY KEY, home VARCHAR);");
		db.execSQL("INSERT INTO homeAddress(name, home) VALUES (\"home\", \""+homeAddress+"\");");
		db.close();
	}
	
	/**
	 * sets work address
	 * @param workAddress
	 */
	private void setWorkAddress(String workAddress){
		SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
		for(int i=0; i < workAddress.length(); i++){
			if(Character.isSpace(workAddress.charAt(i))){
				String temp = workAddress.substring(i+1, workAddress.length());
				workAddress = workAddress.substring(0, i);
				workAddress = workAddress + "+" + temp;
			}
		}
		db.execSQL("CREATE TABLE IF NOT EXISTS workAddress (name VARCHAR PRIMARY KEY, work VARCHAR);");		
		db.execSQL("INSERT INTO workAddress(name, work) VALUES (\"work\", \""+workAddress+"\");");
		db.close();
	}
	
	private void firstTime(){
		//if(isFirstTime()){		
			makeAcceptedDB();
			final Dialog dialog = new Dialog(ctx);
			dialog.setContentView(R.layout.first_time_dialog);
			dialog.setCanceledOnTouchOutside(false);
			
			Button accept = (Button) dialog.findViewById(R.id.accept);
			Button decline = (Button) dialog.findViewById(R.id.decline);
			accept.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					setAccept();
					dialog.cancel();
				}			
			});
			decline.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					setDecline();
				}
			});
			dialog.show();
		//}
	}
	
	/**
	 * returns whether or not the user accepted the disclaimer as an int
	 * 1 = yes and 0 = no
	 * @return 1 or 0
	 */
	private int getAcceptance(){
		SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS accepted (yesorno INT PRIMARY KEY);");
		Cursor c = db.rawQuery("SELECT * FROM accepted", null);
		if(c.getCount() == 1){
			c.moveToFirst();
			int answer = c.getInt(c.getColumnIndex("yesorno"));
			db.close();
			return answer;
		}
		db.close();
		return -1;
	}
	
	/**
	 * makes the datebase to check if the user accepted disclaimer
	 */
	private void makeAcceptedDB(){
		SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS accepted (yesorno INT PRIMARY KEY);");
		db.close();
	}
	
	/**
	 * puts into database that user accepted disclaimer
	 */
	private void setAccept(){
		SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS accepted (yesorno INT PRIMARY KEY);");
		db.execSQL("INSERT INTO accepted(yesorno) VALUES(1);");
		db.close();
	}
	
	/**
	 * puts into database that user declined disclaimer
	 */
	private void setDecline(){
		finish();
		System.exit(0);
	}
	
	/***
	 * Checks that application runs first time and write flag at SharedPreferences 
	 * @return true if 1st time
	 */
	private boolean isFirstTime()
	{
	    SharedPreferences preferences = getPreferences(MODE_PRIVATE);
	    boolean ranBefore = preferences.getBoolean("RanBefore", false);
	    if (!ranBefore) {
	        // first time
	        SharedPreferences.Editor editor = preferences.edit();
	        editor.putBoolean("RanBefore", true);
	        editor.commit();
	    }
	    return !ranBefore;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);		
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.action_settings){
			Intent i = new Intent(this, Settings.class);
			startActivity(i);
			return true;
		}else{
			return super.onOptionsItemSelected(item);
		}
	}
	
	/***
	 * shows toast
	 */
	private void showToast(String output){
		Toast.makeText(ctx, output, Toast.LENGTH_SHORT).show();
	}	
	
}
