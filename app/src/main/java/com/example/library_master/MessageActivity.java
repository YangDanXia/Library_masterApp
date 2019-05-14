package com.example.library_master;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MessageActivity extends Activity implements View.OnClickListener,View.OnLongClickListener{

	/*
	 * btn_back: 返回键
	 * mlist_msg: 列表
	 * mlist_msg_1: 用来存储第一本书的信息
	 * mlist_msg_2: 用来存储第二本书的信息
	 * book_1、book_2: 接收intent传过来的书的信息
	 * mLstView: 列表
	 * state: 代表一个状态，表明是借书还是还书
	 */
	private Button btn_back;
	private Button btn_OK;
	private TextView txv_toolbar;
	private static List<HashMap<String, Object>> mlist_msg_1 = new ArrayList<HashMap<String,Object>>();
	private static List<HashMap<String, Object>> mlist_msg_2 = new ArrayList<HashMap<String,Object>>();
	private List<String> book_1 = new ArrayList<String>();
	private List<String> book_2 = new ArrayList<String>();
	private ListView mLstView;
	private String state;

	private Button btn_g1_1;
	private Button btn_g1_2;
	private Button btn_g1_3;
	private Button btn_g2_1;
	private Button btn_g2_2;
	private Button btn_g2_3;

	private TextView txv_pay;

	//用于评价书籍受损的程度
	private ButtonGroup mButtonGroup_1;
	private ButtonGroup mButtonGroup_2;


	//损坏登记按钮
	private Button btn_damage_registration_1;
	private Button btn_damage_registration_2;

	//用于返回的结果码
	private int RESULTCODE = 100;

	//一个计数的标志
	private int flag = 0;

	/*
	 * 定义一个handler来处理当加载好信息后
	 * 显示到listview
	 */
	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what == 0x123)
			{
				/*
				 * 把列表二中的数据加到列表一上
				 * 显示列表一中的数据
				 */
				for (HashMap<String, Object> hashMap : mlist_msg_2)
				{
					mlist_msg_1.add(hashMap);
				}
				doSimpleAdapter(mlist_msg_1);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);

		//实现沉浸式状态栏
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 状态栏 顶部
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 导航栏 底部
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}

		//初始化控件
		btn_back = (Button) findViewById(R.id.message_btn_back);
		btn_OK = (Button) findViewById(R.id.message_btn_ok);
		mLstView = (ListView) findViewById(R.id.message_listview);
		txv_toolbar = (TextView) findViewById(R.id.message_toolbar_txv);
		btn_damage_registration_1 = (Button) findViewById(R.id.message_damage_registration_1);
		btn_damage_registration_2 = (Button) findViewById(R.id.message_damage_registration_2);
		btn_g1_1 = (Button) findViewById(R.id.message_button_group_1_btn_1);
		btn_g1_2 = (Button) findViewById(R.id.message_button_group_1_btn_2);
		btn_g1_3 = (Button) findViewById(R.id.message_button_group_1_btn_3);
		btn_g2_1 = (Button) findViewById(R.id.message_button_group_2_btn_1);
		btn_g2_2 = (Button) findViewById(R.id.message_button_group_2_btn_2);
		btn_g2_3 = (Button) findViewById(R.id.message_button_group_2_btn_3);
		txv_pay = (TextView) findViewById(R.id.message_txv_pay);

		txv_pay.setVisibility(View.GONE);
		//获取图书数据
		Intent intent = getIntent();
		book_1 = intent.getStringArrayListExtra("book_1");
		book_2 = intent.getStringArrayListExtra("book_2");
		state = intent.getStringExtra("state");

		mButtonGroup_1 = new ButtonGroup(btn_damage_registration_1,
				btn_g1_1, btn_g1_2, btn_g1_3);
		mButtonGroup_2 = new ButtonGroup(btn_damage_registration_2,
				btn_g2_1, btn_g2_2, btn_g2_3);
		mButtonGroup_1.setViewGone();
		mButtonGroup_2.setViewGone();


		//如果是借书状态，则显示“借书清单”
		//否则显示“还书清单”，且显示损坏登记按钮
		if (state.equals("L"))
		{
			txv_toolbar.setText("借书清单");
			btn_damage_registration_1.setVisibility(View.GONE);
			btn_damage_registration_2.setVisibility(View.GONE);
		}
		else
		{
			txv_toolbar.setText("还书清单");
			btn_damage_registration_1.setVisibility(View.VISIBLE);
			btn_damage_registration_1.setOnClickListener(this);
			btn_damage_registration_1.setOnLongClickListener(this);
			if (book_2 != null)
			{
				btn_damage_registration_2.setVisibility(View.VISIBLE);
				btn_damage_registration_2.setOnClickListener(this);
				btn_damage_registration_2.setOnLongClickListener(this);
			}
			else
			{
				btn_damage_registration_2.setVisibility(View.GONE);
			}
		}

		//因为设置当flag为2的时候显示，所以当只有一本书要显示的时候，要让flag+1
		if (book_2 == null)
			flag++;
		addData(book_1, mlist_msg_1);
		addData(book_2, mlist_msg_2);


		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//退出后清空mlist_msg的数据
				mlist_msg_1.clear();
				mlist_msg_2.clear();
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		btn_OK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//退出后清空mlist_msg的数据
				mlist_msg_1.clear();
				mlist_msg_2.clear();
				if (book_2 != null)
				{
//					new Thread(){
//						public void run()
//						{
//							mButtonGroup_1.upLoad_Damage(book_1.get(0), book_1.get(8), book_1.get(2), book_1.get(9));
//							mButtonGroup_2.upLoad_Damage(book_2.get(0), book_2.get(8), book_2.get(2), book_2.get(9));
//						};
//					}.start();
				}
				setResult(RESULTCODE);
				finish();
			}
		});
	}

	/*
	 * 添加信息
	 * 当书本不是null的时候，把要显示的信息从book里提取出来
	 */
	private void addData(final List<String> book, final List<HashMap<String, Object>> mlist_msg)
	{
		if (book != null)
		{
			new Thread()
			{
				public void run()
				{
					Bitmap mbitmap = null;
					/*
					 * 通过path地址来下载图片
					 * DownLoad_Picture.download会将传入的path
					 * 解析成url进行下载图片
					 * 返回一个bitmap对象
					 */
					if (state.equals("L"))
					{
						final String path = book.get(5);
						mbitmap = DownLoad_Picture.download(path);
						HashMap<String, Object> mHashMap = new HashMap<String, Object>();
						mHashMap.put("book_num", "索书号: "+book.get(0));
						mHashMap.put("book_name", "书名: "+book.get(1));
						mHashMap.put("lent_time", "藏书地点: "+book.get(4));
						mHashMap.put("book_cover", mbitmap);
						mHashMap.put("return_time", "到期时间: "+getTime(1));
						mHashMap.put("author", "作者: "+book.get(2));
						mHashMap.put("press", "出版社: "+book.get(3));
						mHashMap.put("book_address", book.get(4));
						mlist_msg.add(mHashMap);
					}
					else
					{
						final String path = book.get(4);
						mbitmap = DownLoad_Picture.download(path);
						HashMap<String, Object> mHashMap = new HashMap<String, Object>();
//						mHashMap.put("person_ID", "账号: " + (String)book.get(0));
						mHashMap.put("book_num", "索书号: " + (String)book.get(0));
						mHashMap.put("book_name", "书名: " + (String)book.get(1));
//	                    mHashMap.put("user_name", "用户名: " + (String)book.get(3));
						mHashMap.put("lent_time", "借出时间: " + (String)book.get(2));
						mHashMap.put("return_time", "到期时间: " + (String)book.get(3));
						mHashMap.put("book_cover", mbitmap);
//	                    mHashMap.put("phone_num", "电话: " + (String)book.get(7));
//	                    mHashMap.put("ISBN", "ISBN: " + (String)book.get(8));
						mHashMap.put("author", "作者: " + (String)book.get(5));
						mHashMap.put("press", "出版社: " + (String)book.get(6));
						mlist_msg.add(mHashMap);
					}
					sendMessage();
				}
			}.start();

		}
	}

	/*
	 * 负责把mListMap显示到对应的列表上
	 */
	private void doSimpleAdapter(List<HashMap<String, Object>> mListMap)
	{

		SimpleAdapter mAdapter = new SimpleAdapter(getApplicationContext(), mListMap, R.layout.mlistview_message,
				new String[]{"book_cover","book_num","book_name","author","press","lent_time","return_time"},
				new int[]{R.id.img_book,R.id.txv_book_num,R.id.txv_book_name,R.id.txv_author,
						R.id.txv_press,R.id.txv_lent_time,R.id.txv_return_time});

		//通过setViewBinder接口将bitmap转化
		mAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object bitmapData, String s) {
				if(view instanceof ImageView && bitmapData instanceof Bitmap){
					ImageView i = (ImageView)view;
					i.setImageBitmap((Bitmap) bitmapData);
					return true;
				}
				return false;
			}
		});
		mLstView.setAdapter(mAdapter);
	}

	private void sendMessage()
	{
		flag++;
		if (flag == 2)
		{
			handler.sendEmptyMessage(0x123);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			mlist_msg_1.clear();
			mlist_msg_2.clear();
			finish();
		}
		return false;
	}

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
			case R.id.message_damage_registration_1:
				btn_damage_registration_1.setText("损坏登记");
				mButtonGroup_1.setViewGone();
				break;
			case R.id.message_damage_registration_2:
				btn_damage_registration_2.setText("损坏登记");
				mButtonGroup_2.setViewGone();
				break;
			default:
				break;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.message_damage_registration_1:
				mButtonGroup_1.setViewVisible();
				mButtonGroup_2.setViewGone();
				break;
			case R.id.message_damage_registration_2:
				mButtonGroup_1.setViewGone();
				mButtonGroup_2.setViewVisible();
				break;
			default:
				break;
		}

	}

	/*
	 * op = 1时获取到期时间
	 * op = 0时获取当前
	 */
	public String getTime(int op) {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH)+1;
		int date = c.get(Calendar.DATE);
		if (op == 0)
		{
			return year+"-"+month+"-"+date;
		}
		return year+"-"+(month+1)+"-"+date;
	}

}
