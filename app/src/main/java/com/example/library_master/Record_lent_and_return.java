package com.example.library_master;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.util.Log;

public class Record_lent_and_return {
	/*
	 * mListMap_lent用来记录当班详情中的借阅情况
	 * mListMap_return用来记录当班详情中的归还书籍情况
	 */
	private static List<HashMap<String, String>> mListMap_lent = new ArrayList<HashMap<String,String>>();
	private static List<HashMap<String, String>> mListMap_return = new ArrayList<HashMap<String,String>>();

	//用来记录同款书登记的次数
	private static Map<String, Integer> counter_lent = new HashMap<String, Integer>();
	private static Map<String, Integer> counter_return = new HashMap<String, Integer>();


	private static boolean findIt = false;



	public static List<HashMap<String, String>> count_Lent(Map<String, String> book_AllMsg)
	{
		HashMap<String, String> mMap = new HashMap<>();
		int index = 0;
		for (HashMap<String, String> mHashMap: mListMap_lent)
		{
			if (mHashMap.get("book_num").contains(book_AllMsg.get("book_num")))
			{
				/*
				 * 如果counter_lent存在这本书，则findIt记为true
				 * 并跳出循环
				 */
				findIt = true;
				break;
			}
			index++;
		}
		if (findIt)
		{
			//counter_lent存在这本书，所以记它的次数加一
			int times = counter_lent.get(book_AllMsg.get("book_num"));
			counter_lent.put(book_AllMsg.get("book_num"), times+1);
			mListMap_lent.get(index).put("book_counter","数量:"+(times+1));
			findIt = false;
		}
		else
		{
			Log.i("lent", mListMap_lent.toString());
			//因为counter_lent不存在这本书，所以记它的次数为一
			counter_lent.put(book_AllMsg.get("book_num"),1);
			mMap.put("book_num", "索书号: "+book_AllMsg.get("book_num"));
			mMap.put("book_name", "书名: "+book_AllMsg.get("book_name"));
			mMap.put("book_cover", book_AllMsg.get("book_cover"));
			mMap.put("book_counter", "数量:"+counter_lent.get(book_AllMsg.get("book_num")));
			mListMap_lent.add(mMap);
		}

		return mListMap_lent;
	}
	public static List<HashMap<String, String>> count_Return(Map<String, String> book_AllMsg)
	{
		HashMap<String, String> mMap = new HashMap<>();
		int index = 0;
		for (HashMap<String, String> mHashMap: mListMap_return)
		{
			if (mHashMap.get("book_num").contains(book_AllMsg.get("book_num")))
			{
				/*
				 * 如果counter_return存在这本书，则findIt记为true
				 * 并跳出循环
				 */
				findIt = true;
				break;
			}
//			else
//			{
//				//如果counter_return不存在这本书，则记它的次数为一
//				counter_return.put(book_AllMsg.get("book_num"),1);
//				mMap.put("book_num", book_AllMsg.get("book_num"));
//				mMap.put("book_name", book_AllMsg.get("book_name"));
//				mMap.put("book_counter", "数量:"+counter_return.get(book_AllMsg.get("book_num")));
//				mListMap_return.add(mMap);
//			}
			index++;
		}
		if (findIt)
		{

			//counter_return存在这本书，所以记它的次数加一
			int times = counter_return.get(book_AllMsg.get("book_num"));
			counter_return.put(book_AllMsg.get("book_num"), times+1);
			mListMap_return.get(index).put("book_counter","数量:"+(times+1));

			findIt = false;
		}
		else
		{
			//因为counter_return不存在这本书，所以记它的次数加一
			counter_return.put(book_AllMsg.get("book_num"),1);
			mMap.put("book_num", "索书号: "+book_AllMsg.get("book_num"));
			mMap.put("book_name", "书名: "+book_AllMsg.get("book_name"));
			mMap.put("book_cover", book_AllMsg.get("book_cover"));
			mMap.put("book_counter", "数量:"+counter_return.get(book_AllMsg.get("book_num")));
			mListMap_return.add(mMap);
		}
		return mListMap_return;
	}

	//返回累计借出书本本数
	public static int getLentCouter()
	{
		int sum = 0;
		Collection<Integer> values = counter_lent.values();
		for (int num : values)
		{
			sum += num;
		}
		return sum;
	}

	//返回累计归还书本本数
	public static int getReturnCouter()
	{
		int sum = 0;
		Collection<Integer> values = counter_return.values();
		for (int num : values)
		{
			sum += num;
		}
		return sum;
	}

}
