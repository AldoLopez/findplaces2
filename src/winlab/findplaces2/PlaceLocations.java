package winlab.findplaces2;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

public class PlaceLocations {
	
	private String name;
	private String type;
	private String address;
	private LatLng ll;
	
	/**
	 * Constructor for a PlaceLocations object
	 * 
	 * @param placeName a string that is the name of the place
	 * @param latitude place's latitude
	 * @param longitude place's longitude
	 */
	public PlaceLocations(String placeName, double latitude, double longitude, String type, String address){
		name = placeName;
		ll = new LatLng(latitude, longitude);
		this.type = type; 
		this.address = address;
	}
	
	/**
	 * returns string value of name
	 * @return String
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * return double value of latitude
	 * @return double
	 */
	public double getLatitude(){
		return ll.latitude;
	}
	
	/**
	 * return double value of longitude
	 * @return double
	 */
	public double getLongitude(){
		return ll.longitude;
	}

	/**
	 * returns type of place
	 * @return String
	 */
	public String getType(){
		return type;
	}
	
	public boolean typeMatches(String typeQuery){
		if(type.contains(typeQuery)){
			return true;
		}
		return false;
	}
	
	/**
	 * returns address of place
	 * @return {@link String}
	 */
	public String getAddress(){
		return address;
	}
	
	/**
	 * returns a LatLng object
	 * for the PlaceLocation
	 * @return {@link LatLng}
	 */
	public LatLng getLatLngObject(){
		return ll;
	}
}
