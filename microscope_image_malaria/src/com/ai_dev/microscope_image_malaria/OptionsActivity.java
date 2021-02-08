package com.ai_dev.microscope_image_malaria;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class OptionsActivity extends Activity {
	TextView txtView_IP_default;
	TextView txtView_IP_label;
	TextView txtView_Port_label;
	EditText etxt_IP;
	EditText etxt_Port;
	Button btn_done;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		Intent intent = getIntent();
		
		txtView_IP_default = (TextView) findViewById(R.id.TxtView_IP_default);
		txtView_IP_label = (TextView) findViewById(R.id.TxtView_IP_label);
		txtView_Port_label = (TextView) findViewById(R.id.TxtView_Port_label);
		etxt_IP = (EditText) findViewById(R.id.EditTxt_IP);
		etxt_Port = (EditText) findViewById(R.id.EditTxt_Port);
		btn_done = (Button) findViewById(R.id.Button_done);
		
		String IP_default = "IP_Address = " + MainActivity_malaria.IP_address +"\n" +"Port = " +  Integer.toString(MainActivity_malaria.Port_number);
		txtView_IP_default.setText(IP_default);
		
		btn_done.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				MainActivity_malaria.IP_address = etxt_IP.getText().toString();
				MainActivity_malaria.Port_number = Integer.parseInt(etxt_Port.getText().toString());
				
				String IP_default = "IP_Address = " + MainActivity_malaria.IP_address +"\n" +"Port = " +  Integer.toString(MainActivity_malaria.Port_number);
				txtView_IP_default.setText(IP_default);
				
				//*****Imageview test code************////
            	/*Intent intent = new Intent(getBaseContext(), MainActivity_malaria.class);
            	//intent.putExtra(EXTRA_MESSAGE, temp_file); // replace with temp_file for live tests
            	startActivity(intent);*/
            	//*****end of imageview test code****////
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}

}
