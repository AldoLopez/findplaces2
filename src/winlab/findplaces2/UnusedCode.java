
/*
 * 		//	prevId = tvIds.get(tvIds.size()-1);
//			i = textViews.size();
//			l  = (LinearLayout) findViewById(R.id.textview_layout);
//			for(int k = 0; k < textViews.size(); k++){
//				TextView newTextView = new TextView(ctx);
//				newTextView.setText(textViews.get(k));
//				int currId = prevId + 1;
//				newTextView.setId(currId);
//				tvIds.add(currId);
//		//		newTextView.setId(tvIds.get(k));
//				RelativeLayout.LayoutParams params =
//						new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
//								RelativeLayout.LayoutParams.WRAP_CONTENT);
//				params.addRule(RelativeLayout.BELOW, prevId);
//				newTextView.setLayoutParams(params);
//				showToast("id of textview is " + newTextView.getId());
//				newTextView.setTextSize(30);				
//				l.addView(newTextView);
//				newTextView.setOnLongClickListener(new View.OnLongClickListener() {
//					
//					@Override
//					public boolean onLongClick(View v) {
//						// TODO Auto-generated method stub
//						showToast("LONG CLIKEROO");						
//						return false;
//					}
//				});
//				prevId = currId;
//			}
 *//**
	 * change address of home and work after settings clicked
	// */
/*	private void changeAddresses(){
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
				if(homeAddress.equals("") || workAddress.equals("")){
					showToast("please fill out both addresses");
				}
				else{
					changeHomeAddress(homeAddress);
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
	 *
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
	 *
	private void changeWorkAddress(String workAddress){
		SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS workAddress (name VARCHAR PRIMARY KEY, home VARCHAR);");
		db.execSQL("UPDATE workAddress SET name=\"work\", work=\""+workAddress+"\" " +
				"where name=\"work\";");
		db.close();
	}
 * 
 * 
 * public static JSONObject getLocationInfo(String address){
					StringBuilder strB = new StringBuilder();
					try{
						address = address.replaceAll(" ", "%20");//replaces all spaces with url %20
						HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
						HttpClient client = new DefaultHttpClient();
						HttpResponse response;
						strB = new StringBuilder();
							
							response = client.execute(httppost);
							HttpEntity entity = response.getEntity();
							InputStream stream = entity.getContent();
							int b;//will be lat long
							while((b = stream.read()) != -1){
								strB.append((char) b);
							}
					}catch(ClientProtocolException e){			
					}catch(IOException e){			
					}
					
					JSONObject jsonObject = new JSONObject();
					try{
						jsonObject = new JSONObject(strB.toString());
					}catch(JSONException e){
						e.printStackTrace();
					}
					
					return jsonObject;
				}
 * 
 * 
 */
