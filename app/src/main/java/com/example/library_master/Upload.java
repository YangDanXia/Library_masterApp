package com.example.library_master;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;
import android.util.Log;

public class Upload {

	private final String path = "http://www.hqinfo.xyz/GET/GET";

	/*
	 * 将取书码和存储柜码上传到数据库,并将`RECORD_RESERVATION`对应的bookid设置为空
	 */
	public void subCode(String Electronicld, String TakeNumber, String BookId) throws Exception
	{
		Map<String, String> params = new HashMap<>();
		params.put("request", "SUBCODE");
		params.put("Electronicld", Electronicld);
		params.put("TakeNumber", TakeNumber);
		params.put("BookId", BookId);
		sendGETRequest(path, params, "UTF-8");
	}
	/*
	 * 上传ISBN的值，从而根据ISBN获得书本的完整数据
	 */
	public JSONArray get_Msg_From_ISBN(String[] ISBN) throws Exception
	{
		Map<String, String> params = new HashMap<>();
		params.put("request", "get_Msg_From_ISBN");
		params.put("ISBN_1", ISBN[0]);
		if (ISBN.length > 1)
		{
			params.put("ISBN_2", ISBN[1]);
		}
		JSONArray jsonArray = new JSONArray(sendGETRequest_JSON(path, params, "UTF-8"));
		return jsonArray;
	}
	/*
	 * 把传入的book_num(索书号)，book_state(借阅状态)
	 * person_ID(借书人的ID)上传到服务器
	 * path:云服务器端的主机名
	 */
	public boolean upload(String book_num, String person_ID, String book_state, String lent_time, String return_time) throws Exception
	{
		Map<String, String> params = new HashMap<>();
		params.put("request", "UP");
		params.put("book_num", book_num);
		params.put("open_ID", person_ID);
		params.put("state", book_state);
		params.put("lent_time", lent_time);
		params.put("return_time", return_time);
		if (sendGETRequest(path, params, "UTF-8") == 444 )
		{
			return true;
		}
		return false;
	}
	/*
	 * 还书操作
	 */
	public boolean return_book(HashMap<String, String> mHashMap) throws Exception
	{
		if (sendGETRequest(path, mHashMap, "UTF-8") == 200)
		{
			return true;
		}
		return false;
	}

	/*
	 * 把CirculationId(借书单号)、BookId(索书号)、ReaderId(借书证)、
	 * BorrowTime(借书时间)、DueTime(到期时间)、ReturnTime(还书时间)
	 * 上传到数据库
	 */
	public void upload_book(HashMap<String, String> mHashMap)
	{
		Map<String, String> params = mHashMap;
		params.put("request", "UP_BOOK");
		Log.i("map_1", params.toString());
		try {
			sendGETRequest(path, params, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * 把传入的账户和密码上传到服务器
	 */
	public boolean login(String name, String pass) throws Exception
	{
		Map<String, String> params = new HashMap<>();
		params.put("request", "LOGIN");
		params.put("name", name);
		params.put("pass", pass);
		Log.d("TTT",params.get("request"));
		return requestLogin(path, params, "UTF-8");
	}
	/*
	 * 把新密码上传到服务器
	 */
	public boolean alter_pass(String new_pass,String name) throws MalformedURLException, IOException
	{
		StringBuilder sb = new StringBuilder(path);
		sb.append("?").append("new_pass=").append(new_pass)
				.append("&name=").append(name).append("&request=ALTER");
		HttpURLConnection conn = (HttpURLConnection) new URL(sb.toString()).openConnection();
		Log.d("RRR",sb.toString());
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == 555)
		{
			return true;
		}
		return false;
	}
	/*
	 * get请求
	 * @param path:请求路径
	 * @param params:请求参数
	 */
	private int sendGETRequest(String path, Map<String, String> params, String encoding) throws Exception
	{
		StringBuilder sb = new StringBuilder(path);
		if(params != null && !params.isEmpty())
		{
			sb.append("?");
			for (Map.Entry<String, String> entry : params.entrySet())
			{
				sb.append(entry.getKey()).append("=");
				sb.append(URLEncoder.encode(entry.getValue(), encoding));
				sb.append("&");
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		HttpURLConnection conn = (HttpURLConnection) new URL(sb.toString()).openConnection();
		Log.d("PPP",sb.toString());
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
//	    if (conn.getResponseCode() == 444)
//	    {
//	    	return true;
//	    }
//		return false;
		Log.d("resp", ""+conn.getResponseCode());
		return conn.getResponseCode();
	}
	/*
	 * get请求
	 * 返回JSON数据
	 * @param path:请求路径
	 * @param params:请求参数
	 */
	private String sendGETRequest_JSON(String path, Map<String, String> params, String encoding) throws Exception
	{
		StringBuilder sb = new StringBuilder(path);
		if(params != null && !params.isEmpty())
		{
			sb.append("?");
			for (Map.Entry<String, String> entry : params.entrySet())
			{
				sb.append(entry.getKey()).append("=");
				sb.append(URLEncoder.encode(entry.getValue(), encoding));
				sb.append("&");
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		HttpURLConnection conn = (HttpURLConnection) new URL(sb.toString()).openConnection();
		Log.d("PPP",sb.toString());
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
//	    if (conn.getResponseCode() == 444)
//	    {
//	    	return true;
//	    }
//		return false;
		InputStream is = conn.getInputStream();
		String json = StreamTool.streamToString(is);
		Log.d("kJson", json.toString());
		return json;
	}
	/*
	 * get请求
	 * 查询是否存在用户并且密码正确
	 * 返回是否允许登录
	 */
	private boolean requestLogin(String path, Map<String, String> params, String encoding) throws Exception
	{
		StringBuilder sb = new StringBuilder(path);
		if(params != null && !params.isEmpty())
		{
			sb.append("?");
			for (Map.Entry<String, String> entry : params.entrySet())
			{
				sb.append(entry.getKey()).append("=");
				sb.append(URLEncoder.encode(entry.getValue(), encoding));
				sb.append("&");
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		path = sb.toString();
		HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
		Log.d("MMMMM",sb.toString());
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == 333)
		{
			return true;
		}
		else return false;

	}
	/*
	 * 图书损坏登记
	 * 把图书受损程度及相关信息上传到书籍库
	 */
	public boolean upLoad_Damage_Msg(String ISBN, String level) throws Exception
	{
		Map<String, String> params = new HashMap<>();
		params.put("request", "DAMAGE");
		params.put("BookId", ISBN);
		params.put("DamageLevel", level);
		if (sendGETRequest(path, params, "UTF-8") == 666)
		{
			return true;
		}
		return false;
	}
	/*
	 * 提交数据，让小程序核对
	 */
	public void upLoad_Book_Msg(JSONArray jsonArray) throws Exception
	{
		String path = "http://www.hqinfo.xyz/ServerForCommunicate/Get?request=uploadMsg";
		StringBuilder sb = new StringBuilder(path);
		Map<String, String> params = new HashMap<String, String>();
		params.put("JSON", jsonArray.toString());
		Log.d("JSON_up", jsonArray.toString());
		Log.d("JSON_up", params.toString());
		if(params != null && !params.isEmpty())
		{
			sb.append("?");
			for (Map.Entry<String, String> entry : params.entrySet())
			{
				sb.append(entry.getKey()).append("=");
				sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
//				sb.append(entry.getValue().toString());
				sb.append("&");
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		HttpURLConnection conn = (HttpURLConnection) new URL(sb.toString()).openConnection();
		Log.d("PPP",sb.toString());
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		Log.d("kkkq", conn.getResponseCode()+"");
	}
	/*
	 * 当工作人员存柜完成后，向用户发送取书码
	 */
	public void send_take_code(String phone_number, String TakeNumber) throws Exception
	{
		String path = "https://www.hqinfo.xyz/Server_Java/SendMessage";
		StringBuilder sb = new StringBuilder(path);
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", phone_number);
		params.put("template", "SMS_86620139");
		params.put("param", "{\"number\":\""+TakeNumber+"\"}");
		if(params != null && !params.isEmpty())
		{
			sb.append("?");
			for (Map.Entry<String, String> entry : params.entrySet())
			{
				sb.append(entry.getKey()).append("=");
				sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
				sb.append("&");
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		HttpURLConnection conn = (HttpURLConnection) new URL(sb.toString()).openConnection();
		Log.d("PPP",sb.toString());
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		Log.d("kkkq", conn.getResponseCode()+"");
	}

}
