package com.example.library_master;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class Damage extends Activity {

	private TextView txv_bookid;
	private RadioGroup rg;
	private String level;
	private Button btn_Ok;
	private Button btn_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_damage);
		//ÊµÏÖ³Á½þÊ½×´Ì¬À¸
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// ×´Ì¬À¸ ¶¥²¿
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// µ¼º½À¸ µ×²¿
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}

		txv_bookid = (TextView) findViewById(R.id.damage_bookid);
		rg = (RadioGroup) findViewById(R.id.damage_radioGroup);
		btn_Ok = (Button) findViewById(R.id.damage_btn_ok);
		btn_back = (Button) findViewById(R.id.damage_btn_back);

		Intent it = getIntent();
		final String bookid = it.getStringExtra("BookId");
		txv_bookid.setText("Ë÷ÊéºÅ£º"+bookid);

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if (checkedId == R.id.radio0)
				{
					level = "3";
				}
				else if (checkedId == R.id.radio1)
				{
					level = "2";
				}
				else if (checkedId == R.id.radio2)
				{
					level = "1";
				}
			}
		});
		btn_Ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(){
					@Override
					public void run() {
						try {
							new Upload().upLoad_Damage_Msg(bookid, level);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.start();
				setResult(100);
				finish();
			}
		});
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.damage, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
