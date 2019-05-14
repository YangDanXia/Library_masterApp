package com.example.library_master;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.net.HttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.util.Log;

public class DownLoad {

	private static final String path = "http://www.hqinfo.xyz/ServerForJSON/GetJSON";

	/*
	 * 从服务器下载预约详情
	 * 然后返回List用来显示在listView中
	 */
	public static List<HashMap<String, String>> getJsonMsg() throws Exception
	{
		List<Message> mList;
		List<HashMap<String, String>> data = new ArrayList<HashMap<String,String>>();
		HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == 200)
		{
			InputStream json = conn.getInputStream();
			mList = parseJSON(json);
			for (Message msg : mList)
			{
				HashMap<String, String> item = new HashMap<String, String>();
				item.put("BooklistISBN", "ISBN: "+msg.getBooklistISBN());
				item.put("UserId", "用户ID: "+msg.getUserId());
				item.put("ReservationGiveTime", "预约取书时间: "+msg.getReservationGiveTime());
				item.put("BookId", "索书号: "+msg.getBookId());
//				item.put("name", "姓名: "+msg.getName());
//				item.put("book_time", "预约时间: "+msg.getBook_time());
//				item.put("status", "状态: "+msg.getStatus());
				data.add(item);
			}
		}
		return data;
	}
	/*
	 * 获取支付成功后的信息
	 * 借书单号
	 */
	public static String getCirculationId() throws Exception
	{
		String path = "http://www.hqinfo.xyz/ServerForCommunicate/Get?request=afterPay";
		HttpURLConnection conn = (HttpURLConnection) new URL(path.toString()).openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		InputStream is = conn.getInputStream();
		String json = StreamTool.streamToString(is);
		JSONArray jsonArray = new JSONArray(json);
		JSONObject jsonObject = jsonArray.getJSONObject(0);
		String CirculationId = jsonObject.getString("CirculationId");
		return CirculationId;
	}

	/*
	 * 将json转化成list
	 */
	private static List<Message> parseJSON(InputStream jsonStream) throws Exception
	{
		List<Message> mlist = new ArrayList<Message>();
		String json = StreamTool.streamToString(jsonStream);
//		//test_b
//		json = "[{ISBN:\"i\",book_name:\"fd\",author:\"h\",publish:\"fd\",name:\"fd\",book_time:\"f\",status:\"df\"}]";
		Log.d("json",json);
//		//test_e
		JSONArray jsonArray = new JSONArray(json);
		for (int i=0;i<jsonArray.length();i++)
		{
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String BooklistISBN = jsonObject.getString("BooklistISBN");
			String UserId = jsonObject.getString("UserId");
			String ReservationGiveTime = jsonObject.getString("ReservationGiveTime");
			String BookId = jsonObject.getString("BookId");
//			String name = jsonObject.getString("name");
//			String book_time = jsonObject.getString("book_time");
//			String status = jsonObject.getString("status");
			mlist.add(new Message(BooklistISBN, UserId, ReservationGiveTime, BookId));
		}
		return mlist;
	}

}
