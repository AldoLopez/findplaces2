package winlab.findplaces2;

import java.util.ArrayList;

import org.json.JSONObject;

public class ReturnHelper {
	
	private ArrayList<JSONObject> theArray;
	private String type;
	
	public ReturnHelper(){
		theArray = new ArrayList<JSONObject>();
		type = "";
	}
	
	public ReturnHelper(ArrayList<JSONObject> arr, String t){
		theArray = arr;
		type = t;
	}
	
	public void setArray(ArrayList<JSONObject> arr){
		theArray = arr;
	}
	
	public void setType(String t){
		type = t;
	}

	public String getType(){
		return type;
	}
	
	public ArrayList<JSONObject> getJSONArray(){
		return theArray;
	}
}
