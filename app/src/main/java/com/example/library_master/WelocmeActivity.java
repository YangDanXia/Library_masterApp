package com.example.library_master;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WelocmeActivity extends Activity {
	
	private Button btn_login;
	private Button btn_alter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welocme);
		
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_alter = (Button) findViewById(R.id.btn_alter_password);
		
		btn_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {		
				Intent it = new Intent(WelocmeActivity.this, LoginActivity.class);
				startActivity(it);
				finish();
			}
		});
		
		btn_alter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent it = new Intent(WelocmeActivity.this, Alter_pass_Activity.class);
				startActivity(it);
				finish();
			}
		});
	}
}
