package com.example.library_master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.string;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter{

	private List<HashMap<String,String>> mContentList;
	private LayoutInflater mInflater;
	private MyClickListener mOnClickListener;
	//当flag为1的时候显示存柜按钮，0则不显示
	int mflag;


	public MyAdapter(Context context, List<HashMap<String,String>> contentlist, MyClickListener onClickListener, int flag) {
		mContentList = contentlist;
		mInflater = LayoutInflater.from(context);
		mOnClickListener = onClickListener;
		mflag = flag;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mContentList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mContentList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.mrlistview, null);
		TextView txv1 = (TextView) convertView.findViewById(R.id.txv_ISBN);
		TextView txv2 = (TextView) convertView.findViewById(R.id.txv_book_name);
		TextView txv3 = (TextView) convertView.findViewById(R.id.txv_author);
		TextView txv4 = (TextView) convertView.findViewById(R.id.txv_publish);
		Button btn = (Button) convertView.findViewById(R.id.btn_store);
		btn.setOnClickListener(mOnClickListener);
		btn.setTag(position);
		if (mflag == 0)
		{
			btn.setVisibility(View.GONE);
		}
		txv1.setText(mContentList.get(position).get("BooklistISBN"));
		txv2.setText(mContentList.get(position).get("UserId"));
		txv3.setText(mContentList.get(position).get("ReservationGiveTime"));
		txv4.setText(mContentList.get(position).get("BookId"));
		return convertView;
	}

	public static abstract class MyClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			myOnClick((int) v.getTag(), v);
		};
		public abstract void myOnClick(int position, View v);
	}
}
