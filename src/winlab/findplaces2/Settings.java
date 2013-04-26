package winlab.findplaces2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends Activity{
	
	Button work, home, legal;
	Context ctx;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_menu_layout);
		ctx = this;
		work = (Button) findViewById(R.id.work_address_set);
		home = (Button) findViewById(R.id.home_address_edit);
		legal = (Button) findViewById(R.id.legal_button);
		
		work.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeWorkAddresses();
			}
		});
		home.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				changeHomeAddresses();
			}
		});
		legal.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showLegal();
			}
		});
	}
	
	private void showLegal(){
		//insert legal from google for google maps
		showToast("Legal from google goes here soon");
		//getOpenSourceSoftwareLicenseInfo(this);
	}
	
	private void changeHomeAddresses(){
		final Dialog dialog = new Dialog(ctx);
		dialog.setContentView(R.layout.set_home_work_address);
		dialog.setCanceledOnTouchOutside(false);
		Button set = (Button) dialog.findViewById(R.id.set_addresses);
		final EditText home = (EditText) dialog.findViewById(R.id.home_address_setting);
		final EditText work = (EditText) dialog.findViewById(R.id.work_address_setting);
		work.setVisibility(View.GONE);
		set.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {				
				String homeAddress = home.getText().toString();
				//String workAddress = work.getText().toString();
				if(homeAddress.equals("") /*|| workAddress.equals("")*/){
					showToast("please fill address");
				}
				else{
					changeHomeAddress(homeAddress);
					//changeWorkAddress(workAddress);
					dialog.cancel();
				}
			}
		});
		dialog.show();
	}
	private void changeWorkAddresses(){
		final Dialog dialog = new Dialog(ctx);
		dialog.setContentView(R.layout.set_home_work_address);
		dialog.setCanceledOnTouchOutside(false);
		Button set = (Button) dialog.findViewById(R.id.set_addresses);
		final EditText home = (EditText) dialog.findViewById(R.id.home_address_setting);
		final EditText work = (EditText) dialog.findViewById(R.id.work_address_setting);
		home.setVisibility(View.GONE);
		set.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {				
				//String homeAddress = home.getText().toString();
				String workAddress = work.getText().toString();
				if(/*homeAddress.equals("") /*||*/ workAddress.equals("")){
					showToast("please fill address");
				}
				else{
					//changeHomeAddress(homeAddress);
					changeWorkAddress(workAddress);
					dialog.cancel();
				}
			}
		});
		dialog.show();
	}
	/**
	 * changes home address 
	 * @param homeAddress new home address
	 * 
	 * 	update homeaddress
		
		set name='home', home='new apartment place'
		
		where name='home';
	 */
	private void changeHomeAddress(String homeAddress){		
		SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS homeAddress (name VARCHAR PRIMARY KEY, home VARCHAR);");
		db.execSQL("UPDATE homeAddress SET name=\"home\", home=\""+homeAddress+"\" " +
				"where name=\"home\";");
		db.close();
	}
	/**
	 * changed work address
	 * @param workAddress new work address
	 */
	private void changeWorkAddress(String workAddress){
		SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS workAddress (name VARCHAR PRIMARY KEY, home VARCHAR);");
		db.execSQL("UPDATE workAddress SET name=\"work\", work=\""+workAddress+"\" " +
				"where name=\"work\";");
		db.close();
	}
	
	/***
	 * shows toast
	 */
	private void showToast(String output){
		Toast.makeText(ctx, output, Toast.LENGTH_SHORT).show();
	}	

}
