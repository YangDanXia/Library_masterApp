package com.example.library_master;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.library_master.MyAdapter.MyClickListener;
import com.zxing.activity.CaptureActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.ContactsContract.Contacts.Data;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button btn_lend;
	private Button btn_return;
	private Button btn_left;
	private Button btn_ceter;
	private Button btn_right;
	private Button btn_search;
	private Button btn_logout;
	private Button btn_registration;
	private TextView txv_untreated;
	private TextView txv_await;
	private ImageView img_delete;
	private TextView txv_left_norecord;
	private EditText search_edi;

	private TextView txv_list_1;
	private TextView txv_list_3;
	private TextView txv_list_4;
	private Chronometer main_Chronometer;

	private TextView txv_pay;

	private int flag_m;

	private int RESULTCODE = 100;
	//用于标识识别到书的数量是不是两本
	private Boolean findTwo;

	private TextView txv_state_lent;
	private TextView txv_state_return;
	/*
	 * book_name:需要上传的书名
	 * person_ID:需要上传的借书人ID
	 * user_name:需要上传的用户名
	 * state:需要上传的借阅状态
	 * book_num:需要上传的索书号
	 */
	private String book_name;
	private String person_ID;
	private String state;
	private String book_cover;
	private String book_num;
	private String lent_time;
	private String return_time;
	//这是一个显示管理员当班情况（借阅或归还）的详情
	private ListView mListView;
	//这是一个显示预约情况的详情列表
	private ListView mRListView;
	//用map来存放扫描到的所有数据
	private Map<String, String> book_AllMsg = new HashMap<>();

	/*
	 * 用mListMap来存放listview要显示的信息
	 * mListMap_lent表示存放借阅情况
	 * mListMap_return表示归还书籍情况
	 * mListMap_order表示预约书籍情况
	 */
	private static List<HashMap<String, String>> mListMap_lent = new ArrayList<HashMap<String,String>>();
	private static List<HashMap<String, String>> mListMap_return = new ArrayList<HashMap<String,String>>();
	private static List<HashMap<String, String>> mListMap_order = new ArrayList<HashMap<String,String>>();

	private List<HashMap<String, String>> mListMap_untreaded = new ArrayList<HashMap<String,String>>();
	private List<HashMap<String, String>> mListMap_await = new ArrayList<HashMap<String,String>>();

	//把List<HashMap<String, String>>类型转化为List<HashMap<String, Object>>类型
	List<HashMap<String, Object>> mListMap_new = new ArrayList<HashMap<String,Object>>();

	private int page = 1;

	private int REQUESTCODE_DISPLAY = 10;

	//代表系统时间
	private String time;

	private long timeBase;

	private String ReaderId;
	private String[] ISBN;
	private String CirculationId = "";

	//存放book_1和book_2的信息
	private List<ArrayList<String>> book_list = new ArrayList<ArrayList<String>>();

	private JSONArray mJSONArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//实现沉浸式状态栏
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 状态栏 顶部
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 导航栏 底部
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}

		//获取系统当前时间
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());
		time = formatter.format(curDate);

		init_certer();
		timeBase = main_Chronometer.getBase();
		init_toobar();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1)
		{
			state = "L";
		}
		if (requestCode == 2)
		{
			state = "R";
		}
		if (requestCode == 3)
		{
			try{
				String result = data.getExtras().getString("result");
				Intent it = new Intent(MainActivity.this, Damage.class);
				it.putExtra("BookId", result);
				startActivityForResult(it, 4);
			}
			catch(Exception e){
			}
		}
		if (resultCode == 100)
		{
			Toast.makeText(getApplicationContext(), "操作成功", Toast.LENGTH_SHORT).show();
		}
		if (resultCode == RESULT_OK && requestCode != 3) {
			//result 是扫描二维码得到的信息
			String result = data.getExtras().getString("result");
//			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG)
//					.show();
			Log.i("msg", result);
			//用正则表达式对数据处理
			String pat_1 = "(.*?);(.*?);(.*?);(.*?);(.*?);(.*?);(.*?);(.*?);(.*?);";
			String pat_2 = "(.*?);(.*?);(.*?);(.*?);(.*?);(.*?);(.*?);(.*?);(.*?);(.*?);(.*?);(.*?);"
					+ "(.*?);(.*?);(.*?);";
			String pat_3 = "(.*?);(.*?);(.*?);(.*?);";
			String pat_4 = "(.*?);(.*?);(.*?);";
			Pattern r_1 = Pattern.compile(pat_1);
			Pattern r_2 = Pattern.compile(pat_2);
			Pattern r_3 = Pattern.compile(pat_3);
			Pattern r_4 = Pattern.compile(pat_4);
			Matcher m_1 = r_1.matcher(result);
			Matcher m_2 = r_2.matcher(result);
			Matcher m_3 = r_3.matcher(result);
			Matcher m_4 = r_4.matcher(result);
			if (m_2.find() && state.equals("R"))
			{
				findTwo = true;
				book_AllMsg.put("flag", m_2.group(1));
				book_AllMsg.put("person_ID", m_2.group(2));
				book_AllMsg.put("return_time", m_2.group(3));

				book_AllMsg.put("book_num", m_2.group(4));
				book_AllMsg.put("book_cover", m_2.group(5));
				book_AllMsg.put("book_name", m_2.group(6));
				book_AllMsg.put("author", m_2.group(7));
				book_AllMsg.put("press", m_2.group(8));
				book_AllMsg.put("lent_time", m_2.group(9));

				book_AllMsg.put("book_num_2", m_2.group(10));
				book_AllMsg.put("book_cover_2", m_2.group(11));
				book_AllMsg.put("book_name_2", m_2.group(12));
				book_AllMsg.put("author_2", m_2.group(13));
				book_AllMsg.put("press_2", m_2.group(14));
				book_AllMsg.put("lent_time_2", m_2.group(15));

//				book_AllMsg.put("book_num", m_2.group(3));
//				book_AllMsg.put("book_name", m_2.group(4));
//				book_AllMsg.put("user_name", m_2.group(5));
//				book_AllMsg.put("lent_time", m_2.group(6));
//				book_AllMsg.put("return_time", m_2.group(7));
//				book_AllMsg.put("book_cover", m_2.group(8));
//				book_AllMsg.put("phone_num", m_2.group(9));
//				book_AllMsg.put("ISBN", m_2.group(10));
//				book_AllMsg.put("author", m_2.group(11));
//				book_AllMsg.put("press", m_2.group(12));
//
//				book_AllMsg.put("person_ID_2", m_2.group(13));
//				book_AllMsg.put("book_num_2", m_2.group(14));
//				book_AllMsg.put("book_name_2", m_2.group(15));
//				book_AllMsg.put("user_name_2", m_2.group(16));
//				book_AllMsg.put("lent_time_2", m_2.group(17));
//				book_AllMsg.put("return_time_2", m_2.group(18));
//				book_AllMsg.put("book_cover_2", m_2.group(19));
//				book_AllMsg.put("phone_num_2", m_2.group(20));
//				book_AllMsg.put("ISBN_2", m_2.group(21));
//				book_AllMsg.put("author_2", m_2.group(22));
//				book_AllMsg.put("press_2", m_2.group(23));
				flag_m = 2;
				display(state);
			}
			else if (m_1.find() && state.equals("R"))
			{
				findTwo = false;
				book_AllMsg.put("flag", m_1.group(1));
				book_AllMsg.put("person_ID", m_1.group(2));
				book_AllMsg.put("return_time", m_1.group(3));

				book_AllMsg.put("book_num", m_1.group(4));
				book_AllMsg.put("book_cover", m_1.group(5));
				book_AllMsg.put("book_name", m_1.group(6));
				book_AllMsg.put("author", m_1.group(7));
				book_AllMsg.put("press", m_1.group(8));
				book_AllMsg.put("lent_time", m_1.group(9));

//				book_AllMsg.put("person_ID", m_1.group(2));
//				book_AllMsg.put("book_num", m_1.group(3));
//				book_AllMsg.put("book_name", m_1.group(4));
//				book_AllMsg.put("user_name", m_1.group(5));
//				book_AllMsg.put("lent_time", m_1.group(6));
//				book_AllMsg.put("return_time", m_1.group(7));
//				book_AllMsg.put("book_cover", m_1.group(8));
//				book_AllMsg.put("phone_num", m_1.group(9));
//				book_AllMsg.put("ISBN", m_1.group(10));
//				book_AllMsg.put("author", m_1.group(11));
//				book_AllMsg.put("press", m_1.group(12));
				flag_m = 1;
				//调用display将内容通过显示出来
				display(state);
			}
			else if (m_3.find())
			{
				flag_m = 3;
				try {
					ISBN = new String[]{m_3.group(3), m_3.group(4)};
					ReaderId = m_3.group(2);
					new Thread(){
						public void run()
						{
							try {
								mJSONArray = new Upload().get_Msg_From_ISBN(ISBN);
								display_book(mJSONArray);
							} catch (Exception e) {
								e.printStackTrace();
							}
						};
					}.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if (m_4.find())
			{
				flag_m = 4;
				try {
					ISBN = new String[]{m_4.group(3)};
					ReaderId = m_4.group(2);
					new Thread(){
						public void run()
						{
							try {
								mJSONArray = new Upload().get_Msg_From_ISBN(ISBN);
								display_book(mJSONArray);
							} catch (Exception e) {
								e.printStackTrace();
							}
						};
					}.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else{
				Toast.makeText(getApplicationContext(), "请输入有效的二维码", Toast.LENGTH_LONG).show();
			}

		}
		if (requestCode == REQUESTCODE_DISPLAY && resultCode == RESULTCODE)
		{
			if (flag_m == 3 || flag_m == 4)
			{
//				this.waitForPay(ISBN);
//				this.setTextView("等待用户支付...", 1);
				handler.sendEmptyMessage(0x222);
			}
			else {
				Log.i("just", "doit");
				person_ID = book_AllMsg.get("person_ID");
				book_num = book_AllMsg.get("book_num");
				lent_time = book_AllMsg.get("lent_time");
				return_time = book_AllMsg.get("return_time");
				book_name = book_AllMsg.get("book_name");
				book_cover = book_AllMsg.get("book_cover");


				//上传数据
				upLoad(person_ID, book_num, state, lent_time, return_time, book_name, book_cover);
				if (findTwo)
				{
					person_ID = book_AllMsg.get("person_ID_2");
					book_num = book_AllMsg.get("book_num_2");
					lent_time = book_AllMsg.get("lent_time_2");
					return_time = book_AllMsg.get("return_time_2");
					book_name = book_AllMsg.get("book_name_2");
					book_cover = book_AllMsg.get("book_cover_2");
					upLoad(person_ID, book_num, state, lent_time, return_time, book_name, book_cover);
				}
			}

		}
	}



	private void display(String state)
	{
		Intent it = new Intent(MainActivity.this, MessageActivity.class);
		ArrayList<String> book_1 = new ArrayList<String>();
		ArrayList<String> book_2 = null;
//		book_1.add(book_AllMsg.get("person_ID"));
		book_1.add(book_AllMsg.get("book_num"));
		book_1.add(book_AllMsg.get("book_name"));
//		book_1.add(book_AllMsg.get("user_name"));
		book_1.add(book_AllMsg.get("lent_time"));
		book_1.add(book_AllMsg.get("return_time"));
		book_1.add(book_AllMsg.get("book_cover"));
//		book_1.add(book_AllMsg.get("phone_num"));
//		book_1.add(book_AllMsg.get("ISBN"));
		book_1.add(book_AllMsg.get("author"));
		book_1.add(book_AllMsg.get("press"));
		if (book_AllMsg.get("flag").equals("2"))
		{
			book_2 = new ArrayList<String>();
//			book_2.add(book_AllMsg.get("person_ID_2"));
			book_2.add(book_AllMsg.get("book_num_2"));
			book_2.add(book_AllMsg.get("book_name_2"));
//			book_2.add(book_AllMsg.get("user_name_2"));
			book_2.add(book_AllMsg.get("lent_time_2"));
			book_2.add(book_AllMsg.get("return_time"));
			book_2.add(book_AllMsg.get("book_cover_2"));
//			book_2.add(book_AllMsg.get("phone_num_2"));
//			book_2.add(book_AllMsg.get("ISBN_2"));
			book_2.add(book_AllMsg.get("author_2"));
			book_2.add(book_AllMsg.get("press_2"));
		}
		it.putExtra("book_1", book_1);
		it.putExtra("book_2", book_2);
		it.putExtra("state", state);
		startActivityForResult(it, REQUESTCODE_DISPLAY);
	}

	private void display_book(JSONArray jsonArray) throws JSONException
	{
		Intent it = new Intent(MainActivity.this, MessageActivity.class);
		ArrayList<String> book_1 = new ArrayList<String>();
		ArrayList<String> book_2 = null;
		for (int i=0;i<jsonArray.length();i++)
		{
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String book_id = jsonObject.getString("book_num");
			String book_name = jsonObject.getString("book_name");
			String author = jsonObject.getString("author");
			String publish = jsonObject.getString("publish");
			String book_address = jsonObject.getString("book_address");
			String book_cover = jsonObject.getString("book_cover");
			if (i == 0)
			{
				book_1.add(book_id);
				book_1.add(book_name);
				book_1.add(author);
				book_1.add(publish);
				book_1.add(book_address);
				book_1.add(book_cover);
			}
			else
			{
				book_2 = new ArrayList<String>();
				book_2.add(book_id);
				book_2.add(book_name);
				book_2.add(author);
				book_2.add(publish);
				book_2.add(book_address);
				book_2.add(book_cover);
			}
		}
		getBook(book_1, book_2, 1);
		it.putExtra("book_1", book_1);
		it.putExtra("book_2", book_2);
		it.putExtra("state", state);
		startActivityForResult(it, REQUESTCODE_DISPLAY);
	}

	/*
	 * 把扫描到的书的信息上传到数据库
	 * 并把信息存储到当班详情中
	 */
	public void upLoad(final String person_ID, final String book_num, final String book_state,
					   final String lent_time, final String retrun_time, final String book_name, final String book_cover)
	{
		try {
			new Thread(){
				public void run() {
					boolean ss = false;
					try {
						HashMap<String, String> mHashMap = new HashMap<String, String>();
						mHashMap.put("request", "RETURN_BOOK");
						mHashMap.put("ReaderId", person_ID);
						mHashMap.put("return_time", retrun_time);
						ss = new Upload().return_book(mHashMap);
//						ss = new Upload().upload(book_num, person_ID, state, lent_time, retrun_time);
						/*
						 * 如果成功，则调用Record_lent_and_return记录数据
						 * 这些数据将用来显示在当班明细中
						 */
//						if (ss && state.equals("L"))
//						{
//							Map<String, String> book = new HashMap<String, String>();
//							book.put("book_num", book_num);
//							book.put("book_name", book_name);
//							book.put("book_cover", book_cover);
//							mListMap_lent = Record_lent_and_return.count_Lent(book);
//						}
						if (ss && state.equals("R"))
						{
							Map<String, String> book = new HashMap<String, String>();
							book.put("book_num", book_num);
							book.put("book_name", book_name);
							book.put("book_cover", book_cover);
							mListMap_return = Record_lent_and_return.count_Return(book);
						}
						//提示借阅或归还书籍的操作是否成功
						Looper.prepare();
						String show = "操作失败";
						if (ss)
						{
							show = "操作成功";
						}
						Toast.makeText(getApplicationContext(), show, Toast.LENGTH_LONG).show();
						Looper.loop();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Looper.prepare();
						Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_LONG).show();
						Looper.loop();
					}
				}
			}.start();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 当用户按下返回时，不退出应用
	 * 保留该activity在栈堆里
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			moveTaskToBack(false);
		}
		return false;
	};

	private void init_toobar()
	{
		btn_left = (Button) findViewById(R.id.btn_left);
		btn_right = (Button) findViewById(R.id.btn_right);
		btn_ceter = (Button) findViewById(R.id.btn_certer);

		btn_left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				init_left();
			}
		});
		btn_ceter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				init_certer();
			}
		});
		btn_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				init_right();
			}
		});
		new Thread(){
			public void run()
			{
				try {
					mListMap_order = DownLoad.getJsonMsg();
//					HashMap<String, String> item = new HashMap<String, String>();
//					item.put("ISBN", "ISBN: "+"7532353907125");
//					item.put("book_name", "书名: "+"演讲技巧");
//					item.put("author", "作者: "+"蒂姆.欣德尔");
//					item.put("publish", "出版社: "+"上海科学技术出版社");
//					item.put("name", "姓名: "+"杨丹霞");
//					item.put("book_time", "预约时间: "+"2017-05-24");
//					item.put("status", "状态: "+"0");
//					mListMap_order.add(item);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	/*
	 * 初始化中间的页面
	 */
	private void init_certer() {
		setContentView(R.layout.activity_main);



		btn_lend = (Button) findViewById(R.id.btn_lend);
		btn_return = (Button) findViewById(R.id.btn_return);
		btn_logout = (Button) findViewById(R.id.btn_logout);
		btn_registration = (Button) findViewById(R.id.btn_registration);
		txv_list_1 = (TextView) findViewById(R.id.main_list_1);
		txv_list_3 = (TextView) findViewById(R.id.main_list_3);
		txv_list_4 = (TextView) findViewById(R.id.main_list_4);

		txv_list_1.setText("开始当班时间:  "+time);
		txv_list_3.setText("累计借出书籍数量:  "+Record_lent_and_return.getLentCouter()+" 本");
		txv_list_4.setText("累计归还书籍数量:  "+Record_lent_and_return.getReturnCouter()+" 本");

		txv_pay = (TextView) findViewById(R.id.main_txv_pay);
		txv_pay.setVisibility(View.GONE);

		main_Chronometer = (Chronometer) findViewById(R.id.main_chronometer);

		main_Chronometer.setOnChronometerTickListener(new OnChronometerTickListener(){
			@Override
			public void onChronometerTick(Chronometer cArg) {
				txv_list_3.setText("累计借出书籍数量:  "+Record_lent_and_return.getLentCouter()+" 本");
				txv_list_4.setText("累计归还书籍数量:  "+Record_lent_and_return.getReturnCouter()+" 本");
				long time = SystemClock.elapsedRealtime() - timeBase;
				int h  = (int)(time /3600000);
				int m = (int)(time - h*3600000)/60000;
				int s = (int)(time - h*3600000- m*60000)/1000 ;
				String hh = h < 10 ? "0"+h: h+"";
				String mm = m < 10 ? "0"+m: m+"";
				String ss = s < 10 ? "0"+s: s+"";
				cArg.setText(hh+":"+mm+":"+ss);
			}
		});
		main_Chronometer.setBase(SystemClock.elapsedRealtime());
		main_Chronometer.start();

		btn_registration.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent startScan = new Intent(MainActivity.this,
						CaptureActivity.class);
				startActivityForResult(startScan, 3);
			}
		});
		btn_lend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent startScan = new Intent(MainActivity.this,
						CaptureActivity.class);
				startActivityForResult(startScan, 1);
			}
		});

		btn_return.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent startScan = new Intent(MainActivity.this,
						CaptureActivity.class);
				startActivityForResult(startScan, 2);
			}
		});
		btn_logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder mdialog = new AlertDialog.Builder(MainActivity.this)
						.setMessage("确定注销用户？")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent it = new Intent(MainActivity.this, LoginActivity.class);
								startActivity(it);
								finish();
							}
						})
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						});
				mdialog.create().show();

			}
		});

		init_toobar();
	}

	/*
	 * 初始化左边页面
	 */
	private void init_left()
	{
		setContentView(R.layout.toolbar_left);

		btn_search = (Button) findViewById(R.id.btn_search);
		search_edi = (EditText) findViewById(R.id.search_edi);
		img_delete = (ImageView) findViewById(R.id.img_delete);
		txv_state_lent = (TextView) findViewById(R.id.txv_state_lent);
		txv_state_return = (TextView) findViewById(R.id.txv_state_return);
		mListView = (ListView) findViewById(R.id.mListView);
		btn_search = (Button) findViewById(R.id.btn_search);
		txv_left_norecord = (TextView) findViewById(R.id.left_txv_norecord);

		//如果没有记录，则控件显示--暂无记录
		if (mListMap_lent.isEmpty()){
			txv_left_norecord.setVisibility(View.VISIBLE);
		}
		else{
			txv_left_norecord.setVisibility(View.GONE);
		}

		img_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				search_edi.setText("");
				if (page == 1)
				{
					doSimpleAdapter(mListMap_lent);
				}
				if (page == 2)
				{
					doSimpleAdapter(mListMap_return);
				}
			}
		});
		txv_state_lent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//如果没有记录，则控件显示--暂无记录
				if (mListMap_lent.isEmpty()){
					txv_left_norecord.setVisibility(View.VISIBLE);
				}
				else{
					txv_left_norecord.setVisibility(View.GONE);
				}
				txv_state_lent.setTextColor(MainActivity.this.getResources().getColor(R.color.main_color));
				txv_state_return.setTextColor(MainActivity.this.getResources().getColor(R.color.black));
				page = 1;
				doSimpleAdapter(mListMap_lent);
			}
		});
		txv_state_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//如果没有记录，则控件显示--暂无记录
				if (mListMap_return.isEmpty()){
					txv_left_norecord.setVisibility(View.VISIBLE);
				}
				else{
					txv_left_norecord.setVisibility(View.GONE);
				}
				txv_state_return.setTextColor(MainActivity.this.getResources().getColor(R.color.main_color));;
				txv_state_lent.setTextColor(MainActivity.this.getResources().getColor(R.color.black));
				page = 2;
				doSimpleAdapter(mListMap_return);
			}
		});
		btn_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String search_text = search_edi.getText().toString();
				List<HashMap<String, String>> result_List = new ArrayList<HashMap<String,String>>();
				/*
				 * 当page=1时，表示借阅情况
				 * 当page=2时，表示归还情况
				 */
				if (page == 1)
				{
					for (HashMap<String, String> mListMap : mListMap_lent)
					{
						/*
						 * 如果有与用户搜索信息匹配的，则加入到显示列表中
						 */
						if (mListMap.get("book_num").contains(search_text)||
								mListMap.get("book_name").contains(search_text))
						{
							result_List.add(mListMap);
						}
					}
				}
				if (page == 2)
				{
					for (HashMap<String, String> mListMap : mListMap_return)
					{
						/*
						 * 如果有与用户搜索信息匹配的，则加入到显示列表中
						 */
						if (mListMap.get("book_num").contains(search_text)||
								mListMap.get("book_name").contains(search_text))
						{
							result_List.add(mListMap);
						}
					}
				}
				doSimpleAdapter(result_List);
			}
		});
		doSimpleAdapter(mListMap_lent);
		init_toobar();
	}

	/*
	 * 初始化右边页面
	 */
	private void init_right()
	{
		setContentView(R.layout.toolbar_right);
		btn_search = (Button) findViewById(R.id.btn_search);
		search_edi = (EditText) findViewById(R.id.search_edi);
		img_delete = (ImageView) findViewById(R.id.img_delete);
		mRListView = (ListView) findViewById(R.id.mRight_ListView);
		btn_search = (Button) findViewById(R.id.btn_search);
		txv_untreated = (TextView) findViewById(R.id.txv_state_untreated);
		txv_await = (TextView) findViewById(R.id.txv_state_await);
		mListMap_untreaded.clear();
		mListMap_await.clear();
		for (HashMap<String, String> mhashmap : mListMap_order)
		{
			if (mhashmap.get("BookId").substring(5).equals(""))
			{
				mListMap_await.add(mhashmap);
			}
			else{
				mListMap_untreaded.add(mhashmap);
			}
		}
		doSimpleAdapter_right(mListMap_untreaded, 1);
		txv_untreated.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				txv_untreated.setTextColor(MainActivity.this.getResources().getColor(R.color.main_color));
				txv_await.setTextColor(MainActivity.this.getResources().getColor(R.color.black));
				doSimpleAdapter_right(mListMap_untreaded, 1);
			}
		});
		txv_await.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				txv_await.setTextColor(MainActivity.this.getResources().getColor(R.color.main_color));
				txv_untreated.setTextColor(MainActivity.this.getResources().getColor(R.color.black));
				doSimpleAdapter_right(mListMap_await, 0);
			}
		});
		img_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				search_edi.setText("");
				doSimpleAdapter_right(mListMap_order, 0);
			}
		});
		btn_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				List<HashMap<String, String>> result_List = new ArrayList<HashMap<String,String>>();
				for (HashMap<String, String> mHashMap : mListMap_order)
				{
					/*
					 * 如果有与用户搜索信息匹配的，则加入到显示列表中
					 */
					if (mHashMap.get("BooklistISBN").contains(search_edi.getText().toString()))
					{
						result_List.add(mHashMap);
					}
					if (mHashMap.get("UserId").contains(search_edi.getText().toString()))
					{
						result_List.add(mHashMap);
					}
					if (mHashMap.get("BookId").contains(search_edi.getText().toString()))
					{
						result_List.add(mHashMap);
					}
				}
				doSimpleAdapter_right(result_List, 0);
			}
		});
		init_toobar();
	}

	/*
	 * 为左视图listview装载数据
	 */
	private void doSimpleAdapter(final List<HashMap<String, String>> mListMap)
	{
		new Thread()
		{
			public void run()
			{
				mListMap_new.clear();
				for (HashMap<String, String> mHashMap : mListMap)
				{
					HashMap<String, Object> mHashMap_new = new HashMap<String, Object>();
					mHashMap_new.put("book_num", mHashMap.get("book_num"));
					mHashMap_new.put("book_name", mHashMap.get("book_name"));
					mHashMap_new.put("book_counter", mHashMap.get("book_counter"));
					mHashMap_new.put("book_cover", DownLoad_Picture.download(mHashMap.get("book_cover")));
					mListMap_new.add(mHashMap_new);
				}
				handler.sendEmptyMessage(0x111);
			}
		}.start();

	}
	/*
	 * 为右视图listview装载数据
	 */
	private void doSimpleAdapter_right(final List<HashMap<String, String>> mListMap, int flag)
	{
//		SimpleAdapter mAdapter = new SimpleAdapter(getApplicationContext(), mListMap, R.layout.mrlistview,
//				new String[]{"ISBN","book_name","author","publish","name","book_time"},
//				new int[]{R.id.txv_ISBN,R.id.txv_book_name,R.id.txv_author,R.id.txv_publish,
//			              R.id.txv_name,R.id.txv_book_time});
//		SimpleAdapter mAdapter = new SimpleAdapter(getApplicationContext(), mListMap, R.layout.mrlistview,
//				new String[]{"BooklistISBN","UserId","ReservationGiveTime","BookId"},
//				new int[]{R.id.txv_ISBN,R.id.txv_publish,R.id.txv_book_name,R.id.txv_author});
//		mRListView.setAdapter(mAdapter);
		MyClickListener mClickListener = new MyClickListener() {

			@Override
			public void myOnClick(int position, View v) {
				LayoutInflater li = LayoutInflater.from(MainActivity.this);
				final View mview = li.inflate(R.layout.dialog_input, null);
				AlertDialog.Builder mdialog = new AlertDialog.Builder(MainActivity.this);
				mdialog.setView(mview);
				final EditText edi = (EditText) mview.findViewById(R.id.dialog_edi);
				final String msg = mListMap.get(position).get("BookId").toString().substring(5);
				final String phone = mListMap.get(position).get("UserId").toString().substring(6);
				mdialog.setCancelable(false)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								Log.i("edidi", edi.getText().toString()+msg);
								new Thread(){
									public void run() {
										try {
											int number = (int)(Math.random() * 10000000);
											String TakeNumber = "" + number;
											new Upload().subCode(edi.getText().toString(), TakeNumber, msg);
											new Upload().send_take_code(phone, TakeNumber);
										} catch (Exception e) {
											e.printStackTrace();
										}
										handler.sendEmptyMessage(0x333);
									}
								}.start();
								Toast.makeText(getApplicationContext(), "操作成功", Toast.LENGTH_SHORT).show();
							}
						})
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						});

				AlertDialog alertdialog = mdialog.create();
				alertdialog.show();
			}
		};
		mRListView.setAdapter(new MyAdapter(getApplicationContext(), mListMap, mClickListener, flag));
	}
	/*
	 * 获取book_1和book_2的信息
	 * 参数flag等于0则代表取数据
	 * 等于1传入数据
	 */
	private List<ArrayList<String>> getBook(ArrayList<String> book_1, ArrayList<String> book_2, int flag)
	{

		book_list.add(book_1);
		book_list.add(book_2);
		return book_list;
	}

	/*
	 *
	 */
	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(android.os.Message msg)
		{
			if (msg.what == 0x111)
			{
				SimpleAdapter mAdapter = new SimpleAdapter(getApplicationContext(), mListMap_new, R.layout.mlistview,
						new String[]{"book_num","book_name","book_counter","book_cover"},
						new int[]{R.id.txv_book_num,R.id.txv_book_name,R.id.txv_book_couter,R.id.mlistview_img_book});
				mListView.setAdapter(mAdapter);
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
			}
			if (msg.what == 0x222)
			{
				/*
				 * 支付成功，可以提交信息
				 */
				List<ArrayList<String>> list = getBook(null, null, 0);
				ArrayList<String> book_1 = list.get(0);
				ArrayList<String> book_2 = list.get(1);
				final HashMap<String, String> map = new HashMap<String, String>();
//				map.put("CirculationId", CirculationId);
				map.put("ReaderId", ReaderId);
				map.put("num", "1");
				map.put("state", "L");
				map.put("BorrowTime", new MessageActivity().getTime(0));
				map.put("DueTime", new MessageActivity().getTime(1));
				if (state.equals("L"))
				{
					Map<String, String> book = new HashMap<String, String>();
					book.put("book_num", book_1.get(0));
					book.put("book_name", book_1.get(1));
					book.put("book_cover", book_1.get(5));
					map.put("BookId", book_1.get(0));
					mListMap_lent = Record_lent_and_return.count_Lent(book);
					if (book_2 != null)
					{
						Map<String, String> book_n = new HashMap<String, String>();
						book_n.put("book_num", book_2.get(0));
						book_n.put("book_name", book_2.get(1));
						book_n.put("book_cover", book_2.get(5));
						map.put("BookId_2", book_2.get(0));
						map.put("num", "2");
						mListMap_lent = Record_lent_and_return.count_Lent(book_n);
					}
					try {
						new Thread(){
							public void run()
							{
								new Upload().upload_book(map);
							};
						}.start();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				CirculationId = "";
				book_list = new ArrayList<ArrayList<String>>();
				Toast.makeText(getApplicationContext(), "支付成功", Toast.LENGTH_LONG).show();
				init_certer();
			}
			if (msg.what == 0x333)
			{
//				try {
//					mListMap_order = DownLoad.getJsonMsg();
//					mListMap_untreaded.clear();
//					mListMap_await.clear();
//					for (HashMap<String, String> mhashmap : mListMap_order)
//					{
//						if (mhashmap.get("BookId").substring(5).equals(""))
//						{
//							mListMap_await.add(mhashmap);
//						}
//						else{
//							mListMap_untreaded.add(mhashmap);
//						}
//					}
//					doSimpleAdapter_right(mListMap_untreaded, 1);
//				} catch (Exception e) {
//				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				init_right();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				init_right();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				init_right();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				init_right();
			}
		};
	};
	/*
	 * 管理员确认后，上传数据等待用户支付的操作
	 */
	private void waitForPay(final String[] ISBN)
	{
		new Thread(){
			@Override
			public void run()
			{
				try {
					new Upload().upLoad_Book_Msg(mJSONArray);
					while (CirculationId.equals(""))
					{
						Log.d("Thread", "ok_4");
						Thread.sleep(200);
						CirculationId = DownLoad.getCirculationId();
					}
					Log.d("Thread", "ok_3");
					handler.sendEmptyMessage(0x222);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}
	/*
	 * 设置txv_pay的状态
	 * 当state为1时可见
	 * 当state为0时不可见
	 */
	public void setTextView(String s, int state)
	{
		setContentView(R.layout.pay_xml);
		txv_pay = (TextView) findViewById(R.id.pay_txv);
		txv_pay.setText(s);
		if (state == 0)
		{
			txv_pay.setVisibility(View.GONE);
		}
		else if (state == 1)
		{
			txv_pay.setVisibility(View.VISIBLE);
		}
	}
}
