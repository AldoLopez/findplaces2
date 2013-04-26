package winlab.findplaces2;

import java.io.Serializable;

import org.apache.http.entity.SerializableEntity;

import android.widget.TextView;

public class textViewNodes implements Serializable{

	static String  text;
	static TextView tview;
	static int id;
	public textViewNodes(TextView textview, int id){
		text = textview.getText().toString();
		tview = textview;
		this.id = id;
	}
	
	public static String getText(){
		String t = text;
		return t;
	}
	
	public static TextView getTextView(){
		TextView tv= tview;
		return tv;
	}
	public static int getId(){
		int i = id;
		return i;
	}
}
