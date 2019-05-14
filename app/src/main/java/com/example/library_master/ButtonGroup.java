package com.example.library_master;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ButtonGroup {

	private Button btn_1;
	private Button btn_2;
	private Button btn_3;
	private int Damage_Level;


	public ButtonGroup(final Button btn, Button btn_1, Button btn_2, Button btn_3)
	{
		this.btn_1 = btn_1;
		this.btn_2 = btn_2;
		this.btn_3 = btn_3;
		this.btn_1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setViewGone();
				btn.setText("轻微受损");
				Damage_Level = 1;
			}
		});
		this.btn_2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setViewGone();
				btn.setText("一般受损");
				Damage_Level = 2;
			}
		});
		this.btn_3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setViewGone();
				btn.setText("严重受损");
				Damage_Level = 3;
			}
		});
		btn.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				btn.setText("损坏登记");
				setViewGone();
				Damage_Level = 0;
				return true;
			}
		});
	}

	public void setViewGone()
	{
		btn_1.setVisibility(View.GONE);
		btn_2.setVisibility(View.GONE);
		btn_3.setVisibility(View.GONE);
	}

	public void setViewVisible()
	{
		btn_1.setVisibility(View.VISIBLE);
		btn_2.setVisibility(View.VISIBLE);
		btn_3.setVisibility(View.VISIBLE);
	}

	/*
	 * 将书本的情况以及用户ID和图书的一些信息上传到数据库进行登记
	 */
	public boolean upLoad_Damage(String openID, String ISBN, String book_name, String author)
	{
		try {
			if(new Upload().upLoad_Damage_Msg(ISBN, ""+Damage_Level))
			{
				Log.i("Damage", "success");
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("Damage", "dafult");
		return false;
	}

}
