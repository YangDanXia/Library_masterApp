package com.example.library_master;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Alter_pass_Activity extends Activity {

	private EditText user_name;
	private EditText user_pass;
	private Button btn_back;
	private Button btn_OK;
	private boolean flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alter_pass_);

		//实现沉浸式状态栏
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 状态栏 顶部
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 导航栏 底部
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}

		init();
		setOnClickListener();
	}

	private void init() {
		user_name = (EditText) findViewById(R.id.a_user_name);
		user_pass = (EditText) findViewById(R.id.a_user_pass);
		btn_back = (Button) findViewById(R.id.a_btn_login_back);
		btn_OK = (Button) findViewById(R.id.a_btn_OK);
	}

	private void setOnClickListener() {
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent it = new Intent(Alter_pass_Activity.this,
						LoginActivity.class);
				startActivity(it);
				finish();
			}
		});

		btn_OK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				Toast mToast = Toast.makeText(getApplicationContext(), "请稍后...", Toast.LENGTH_LONG);
//				mToast.setGravity(Gravity.CENTER, 0, 0);
//				mToast.show();
				btn_OK.setClickable(false);
				final String name = user_name.getText().toString();
				final String password = user_pass.getText().toString();
				if (isNetworkConnected(getApplicationContext())) {
					btn_OK.setText("请稍后...");
					try {
						new Thread() {
							public void run() {
								boolean access = false;
								// 提交账号和密码到云端，验证是否正确
								try {
									access = new Upload().login(name, password);
								} catch (Exception e) {
									e.printStackTrace();
								}
								if (access) {
									/*
									 * 如果原密码无误，则可以修改密码
									 */
									flag = true;
								} else {
									flag = false;
								}
							}
						}.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (flag)
					{
						/*
						 * 调用修改密码函数
						 */
						alter_Pass(name, password);
					}
					else
					{
						Toast.makeText(getApplicationContext(),
								"账户或密码错误", Toast.LENGTH_LONG)
								.show();
					}
					btn_OK.setClickable(true);
					btn_OK.setText("确认");
				} else {
					Toast.makeText(getApplicationContext(), "请检查网络设置",
							Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	/*
	 * 修改密码，上传用户的参数，若账号与密码都正确且匹配，则允许用户修改新密码
	 */
	private void alter_Pass(final String name, String password) {

		setContentView(R.layout.ensure_new_pass);

		Button btn_back;
		Button btn_OK;
		final EditText new_pass;

		btn_back = (Button) findViewById(R.id.a_btn_back);
		btn_OK = (Button) findViewById(R.id.a_btn_OK);
		new_pass = (EditText) findViewById(R.id.a_new_pass);

		btn_OK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.d("HHH", "error0");
				final String newpass = new_pass.getText().toString();
				if(newpass.equals(new_pass))
				{
					Toast.makeText(getApplicationContext(), "不能与原密码一致", Toast.LENGTH_LONG).show();
				}
				else
				{
					try {
						new Thread(){
							public void run() {
								boolean isOK = false;
								try {
									isOK = new Upload().alter_pass(newpass, name);
								} catch (MalformedURLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if (isOK)
								{
									Looper.prepare();
									Toast.makeText(getApplicationContext(), "修改密码成功", Toast.LENGTH_LONG).show();
									Looper.loop();
								}
								else
								{
									Looper.prepare();
									Toast.makeText(getApplicationContext(), "修改密码失败", Toast.LENGTH_LONG).show();
									Looper.loop();
								}
							};
						}.start();

					} catch (Exception e) {
						e.printStackTrace();
						Log.d("HHH", "error");
					}
					finally
					{
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Intent it = new Intent(Alter_pass_Activity.this, LoginActivity.class);
						startActivity(it);
						finish();
					}
				}
			}
		});

		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent it = new Intent(Alter_pass_Activity.this, LoginActivity.class);
				startActivity(it);
				finish();
			}
		});
	}

	/*
	 * 判断网络是否已经连接
	 * 并且网络可用
	 */
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
