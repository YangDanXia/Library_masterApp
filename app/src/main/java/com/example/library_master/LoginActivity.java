package com.example.library_master;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private EditText user_name;
	private EditText user_pass;
	private Button btn_login;
	private Button btn_back;
	private Button btn_alter_pass;
	private String name;
	private String password;
	private boolean isLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}

		user_name = (EditText) findViewById(R.id.user_name);
		user_pass = (EditText) findViewById(R.id.user_pass);
		btn_login = (Button) findViewById(R.id.btn_login);
//		btn_back = (Button) findViewById(R.id.btn_login_back);
		btn_alter_pass = (Button) findViewById(R.id.alter_pass);

		btn_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				name = user_name.getText().toString();
				password = user_pass.getText().toString();
				if (name.equals("") || password.equals(""))
				{
					if (name.equals(""))
					{
						Toast mtaost = Toast.makeText(getApplicationContext(), "账号不能为空", Toast.LENGTH_LONG);
						mtaost.setGravity(Gravity.CENTER, 0, 0);
						mtaost.show();
					}
					else if (password.equals(""))
					{
						Toast mtaost = Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_LONG);
						mtaost.setGravity(Gravity.CENTER, 0, 0);
						mtaost.show();
					}
				}
				if (!name.equals("") && !password.equals(""))
				{
					if (isNetworkConnected(getApplicationContext()))
					{
						btn_login.setClickable(false);
						try {
							new Thread(){
								public void run() {
									boolean access = false;
									try {
										access = new Upload().login(name, password);
									} catch (Exception e) {
										e.printStackTrace();
									}
									if (access) {
										isLogin = true;
										Intent it = new Intent(LoginActivity.this,MainActivity.class);
										startActivity(it);
										finish();
									} else {
										Looper.prepare();
										Toast.makeText(getApplicationContext(),"账号错误", Toast.LENGTH_LONG)
												.show();
										Looper.loop();
									}
								}
							}.start();
						} catch (Exception e) {
							e.printStackTrace();
						}
						btn_login.setClickable(true);
					}
					else{
						Toast.makeText(getApplicationContext(),"密码错误", Toast.LENGTH_LONG)
								.show();
					}
				}
			}
		});
//		btn_back.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent it = new Intent(LoginActivity.this, WelocmeActivity.class);
//				startActivity(it);
//				finish();
//			}
//		});
		btn_alter_pass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(LoginActivity.this, Alter_pass_Activity.class);
				startActivity(it);

			}
		});
	}


	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isConnected()) {
				if (mNetworkInfo.getState() == NetworkInfo.State.CONNECTED)
				{
					return true;
				}
			}
		}
		return false;
	}

}
